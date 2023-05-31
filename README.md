# cats-js-clipboard

![](https://github.com/bpholt/cats-js-clipboard/actions/workflows/ci.yml/badge.svg "Build Status")
![](https://img.shields.io/github/license/bpholt/cats-js-clipboard.svg?style=flat-square "MIT License")

Add the following to your `build.sbt` to bring `cats-js-clipboard` into your Scala.js project:

```scala
libraryDependencies += "dev.holt" %%% "cats-js-clipboard" % "0.5.0"
```

To use:

```scala
val x: IO[Boolean] = Clipboard.make[IO].copy("text")
```

When the returned effect is executed, the passed text will be added to the DOM in a `<pre>` element, selected, and copied. If the copy succeeds, the effect will return `true` and the added elements will be removed from the DOM; if not, the elements will remain in place, scrolled into view for manual copying, and the effect will return `false`.

If loaded, the copy can also be called from JavaScript:

```js
Clipboard.copy("text") // returns a JS promise with the same semantics as above
```

## Other Libraries

Arman Bilge's [fs2-dom](https://github.com/armanbilge/fs2-dom) project has clipboard support and many other features; you may find it to be a better option than this project.
