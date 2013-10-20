(ns selectable.cljs.app
  (:require [clojure.string :as s]
            [goog.events.KeyCodes :as kc]
            [cljs.core.async :as a]
            [dommy.core :as d]
            clojure.browser.repl)
  (:require-macros [dommy.macros :refer [node sel1]]
                   [cljs.core.async.macros :refer [go]]))

(defn render-options [{:keys [options highlighted selected]}]
  (node
   [:pre {:style {:font-family "monospace" :padding "2em" :width "10em"}}
    (interpose "\n"
               (for [[index option] (map vector (range) options)]
                 (str
                  (if (= highlighted index) ">" " ")
                  (if (= selected index) "* " "  ")
                  option)))]))

(defn render-list [bind-list!]
  (doto (node [:div {:style {:margin-top :1em}}])
    bind-list!))

(def key-code->command
  {kc/UP :up
   kc/DOWN :down
   kc/ENTER :select})

(defn listen [$el event]
  (let [out (a/chan)]
    (d/listen! $el event
               (fn [e]
                 (a/put! out e)
                 (.preventDefault e)))
    out))

(defn keys->commands [command-ch]
  (-> (listen js/document :keyup)
      (->> (a/mapcat< (fn [e]
                        (when-let [command (key-code->command (.-keyCode e))]
                          [command]))))
      (a/pipe command-ch)))

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

(defn process-commands [command-ch !options]
  (go
   (loop []
     (when-let [command (a/<! command-ch)]
       (swap! !options process-command command)
       (recur)))))

(defn list-binder [!options]
  (letfn [(show-list! [$list options]
            (d/replace-contents! $list (render-options options)))]
    (fn [$list]
      (show-list! $list @!options)
      (add-watch !options ::binder
                 (fn [_ _ _ options]
                   (show-list! $list options))))))

(set! (.-onload js/window)
      (fn []
        (let [command-ch (a/chan)
                     !options (atom {:options ["Red" "Green" "Blue"]
                                     :highlighted 0})]
                 (keys->commands command-ch)
                 (process-commands command-ch !options)
                 (d/replace-contents! (sel1 :#content)
                                      (render-list
                                       (list-binder !options))))))
