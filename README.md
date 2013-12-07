# Selectable

An example CLJS app replicating David Nolan's
['CSP is Responsive Design'][1] blog, with a slightly different
approach.

[1]: http://swannodette.github.io/2013/07/31/extracting-processes/

## Why?

I originally started this in preparation for my talk about patterns in
ClojureScript at ClojureX in December 2013, and wanted to convince
myself (and, by extension, the audience) that the patterns that I'll
be presenting were good enough, and new enough, to warrant a conference
talk!

The code splits nicely into 3 distinct sections:

* Model - listening to an events channel and updating a state atom (vanilla Clojure)
* Widget component - interacting with JS (no DOM code anywhere else!)
* Widget binding - watching the state atom, updating the DOM, and putting DOM events on the events channel

Oh, and any CLJS practise is good practise!

## Usage

`git clone`, and `lein dev` should do the trick.

The project is based on my [lein template][2] which gets CLJS
applications off the ground quickly - the code that runs the front end
is in [`src/cljs/selectable/cljs/app.cljs`][3].

[2]: https://github.com/james-henderson/splat
[3]: https://github.com/james-henderson/selectable/blob/master/src/cljs/selectable/cljs/app.cljs
