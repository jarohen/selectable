# Selectable

An example CLJS app replicating David Nolan's
['CSP is Responsive Design'][1] blog, with a slightly different
approach.

[1]: http://swannodette.github.io/2013/07/31/extracting-processes/

## Why?

I'm due to be presenting a talk about patterns in ClojureScript at
ClojureX in December 2013, and wanted to convince myself (and, by
extension, the audience) that the patterns that I'll be presenting are
good enough, and new enough, to warrant a conference talk!

The code splits nicely into 5 distinct sections:

* Rendering an instance of the state (i.e. a value)
* Creating commands based on key events
* Updating the state based on the commands
* Re-rendering the list based on the state
* Wiring it all up

Oh, and any CLJS practise is good practise!

## Usage

`git clone`, and `lein dev` should do the trick.

The project is based on my [lein template][2] which gets CLJS
applications off the ground quickly - the code that runs the front end
is in [`src/cljs/selectable/cljs/app.cljs`][3].

[2]: https://github.com/james-henderson/cljs-spa-template
[3]: https://github.com/james-henderson/selectable/blob/master/src/cljs/selectable/cljs/app.cljs
