(ns selectable.cljs.app
  (:require [clojure.string :as s]
            [goog.events.KeyCodes :as kc]
            [cljs.core.async :as a]
            [dommy.core :as d]
            clojure.browser.repl)
  (:require-macros [dommy.macros :refer [node sel1]]
                   [cljs.core.async.macros :refer [go]]))

;; ---------- Component Protocol ----------

(defprotocol MenuComponent
  (menu->node [_])
  (render-menu! [_ menu])
  (event-ch [_]))

;; ---------- Component implementation as <pre> ----------

(def key-code->command
  {kc/UP :up
   kc/DOWN :down
   kc/ENTER :select})

(defn listen [$el event]
  (let [out (a/chan)]
    (d/listen! $el event
               (fn [e]
                 (a/put! out e)))
    out))

(defn keys->commands [ch]
  (->> ch
       (a/mapcat< (fn [e]
                    (when-let [command (key-code->command (.-keyCode e))]
                      (.preventDefault e)
                      [command])))))

(defn render-options [{:keys [options highlighted selected]}]
  (node
   [:pre {:style {:font-family "monospace" :padding "2em" :width "10em"}}
    (interpose "\n"
               (for [[index option] (map vector (range) options)]
                 (str
                  (if (= highlighted index) ">" " ")
                  (if (= selected index) "* " "  ")
                  option)))]))

(defn make-menu-component []
  (let [menu-el (node [:div {:style {:margin-top "2em"}}])]
    (reify MenuComponent
      (menu->node [_] menu-el)
      (render-menu! [_ menu]
        (d/replace-contents! menu-el (render-options menu)))
      (event-ch [_]
        (keys->commands (listen js/document :keydown))))))

;; ---------- Widget bindings ----------

(defn watch-options! [$menu !options]
  (render-menu! $menu @!options)
  (add-watch !options ::binder
             (fn [_ _ _ options]
               (render-menu! $menu options))))

(defn listen-for-keypresses! [$menu command-ch]
  (a/pipe (event-ch $menu) command-ch))

;; ---------- Model ----------

(defmulti process-command #(identity %2))

(defmethod process-command :up [{:keys [highlighted options] :as state} _]
  (assoc state
    :highlighted (mod (dec highlighted) (count options))))

(defmethod process-command :down [{:keys [highlighted options] :as state} _]
  (assoc state
    :highlighted (mod (inc highlighted) (count options))))

(defmethod process-command :select [{:keys [highlighted selected] :as state} _]
  (assoc state
    :selected highlighted))

(defmethod process-command :default [options _] options)

(defn process-commands [!options command-ch]
  (go
   (loop []
     (when-let [command (a/<! command-ch)]
       (swap! !options process-command command)
       (recur)))))

;; ---------- Wiring it all up ----------

(set! (.-onload js/window)
      (fn []
        (let [command-ch (a/chan)
              !options (atom {:options ["Red" "Green" "Blue"]
                              :highlighted 0})

              menu-component (doto (make-menu-component)
                               (watch-options! !options)
                               (listen-for-keypresses! command-ch))]

          (doto !options
            (process-commands command-ch))

          (d/replace-contents! (sel1 :#content) (menu->node menu-component)))))
