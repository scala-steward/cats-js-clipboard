package com.planetholt.clipboard

import cats._
import cats.effect._
import cats.implicits._
import org.scalajs.dom.html.Document
import org.scalajs.dom.raw._

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters._

@JSExportTopLevel("Clipboard")
object Clipboard {
  import scala.concurrent.ExecutionContext.Implicits.global
  private implicit val document = org.scalajs.dom.document
  private implicit val window = org.scalajs.dom.window

  @JSExport
  def copy(text: String): Promise[Boolean] =
    new Clipboard[IO]
      .copy(text)
      .unsafeToFuture()
      .toJSPromise

}

class Clipboard[F[_] : Effect](implicit document: Document, window: Window) {
  def copy(text: String): F[Boolean] =
    for {
      textElement ← createElementWithText("pre", text)
      button ← createElementWithText("button", "Click to Copy")
      _ ← buildListener(textElement, button)
      _ ← appendChild(textElement)
      _ ← appendChild(button)
      copySuccess ← copyNodeContents(textElement)
      _ ← if (copySuccess) cleanup(textElement, button) else scrollElementIntoView(button)
    } yield copySuccess

  private def scrollElementIntoView(element: Element): F[Unit] = Sync[F].delay(element.scrollIntoView())

  private def appendChild(element: Element): F[Node] = Sync[F].delay(document.body.appendChild(element))

  private def createElementWithText(tag: String, textContent: String): F[Element] = Sync[F].delay {
    val elem = document.createElement(tag)
    elem.appendChild(document.createTextNode(textContent))

    elem
  }

  private def buildListener(textElement: Element, button: Element): F[Unit] = Sync[F].delay {
    lazy val clickListener: js.Function1[Event, Any] = (_: Event) ⇒ {
      val value: F[Unit] = for {
        success ← copyNodeContents(textElement)
        _ ← if (success)
          for {
            _ ← cleanup(textElement, button)
            _ ← Sync[F].delay(button.removeEventListener("click", clickListener))
          } yield ()
        else Applicative[F].unit
      } yield ()

      IO.async { cb: (Either[Throwable, Unit] ⇒ Unit) ⇒
        Effect[F].runAsync(value)(r ⇒ IO(cb(r))).unsafeRunSync()
      }.unsafeRunAsync(_ ⇒ ())
    }

    button.addEventListener("click", clickListener)
  }

  private def windowSelection(): F[Selection] = Sync[F].delay(window.getSelection())

  private def removeAllRanges(): F[Unit] =
    for {
      selection ← windowSelection()
      _ ← Sync[F].delay(selection.removeAllRanges())
    } yield ()

  private def copyNodeContents(element: Element): F[Boolean] =
    for {
      _ ← removeAllRanges()
      range ← Sync[F].delay(document.createRange())
      _ ← Sync[F].delay(range.selectNode(element))
      selection ← windowSelection()
      _ ← Sync[F].delay(selection.addRange(range))
      success ← Sync[F].delay(document.execCommand("copy"))
    } yield success

  private def cleanup(elements: Element*): F[Unit] =
    for {
      _ ← removeAllRanges()
      _ ← elements.toList.map(node ⇒ Sync[F].delay(node.parentNode.removeChild(node))).sequence
    } yield ()
}
