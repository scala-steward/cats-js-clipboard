# cats-js-clipboard

[![Build Status](https://travis-ci.org/bpholt/cats-js-clipboard.svg?branch=master)](https://travis-ci.org/bpholt/cats-js-clipboard)

Add the following to your `build.sbt` to bring `cats-js-clipboard` into your Scala.js project:

```scala
resolvers += Resolver.bintrayRepo("bpholt", "maven")
libraryDependencies += "com.planetholt" %%% "cats-js-clipboard" % "0.1.0"
```

To use:

```scala
val x: IO[Boolean] = new Clipboard[IO].copy("text")
```

When the returned effect is executed, the passed text will be added to the DOM in a `<pre>` element, selected, and copied. If the copy succeeds, the effect will return `true` and the added elements will be removed from the DOM; if not, the elements will remain in place, scrolled into view for manual copying, and the effect will return `false`.

If loaded, the copy can also be called from JavaScript:

```js
Clipboard.copy("text") // returns a JS promise with the same semantics as above
```
