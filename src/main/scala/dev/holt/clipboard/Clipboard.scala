package dev.holt.clipboard

import cats.effect._
import cats.effect.std.Dispatcher
import cats.syntax.all._
import org.scalajs.dom._
import org.scalajs.dom.html.Document

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation._

@JSExportTopLevel("Clipboard")
object Clipboard {

  import cats.effect.unsafe.implicits.{global => ceGlobal}
  import org.scalajs.macrotaskexecutor.MacrotaskExecutor.Implicits.{global => sjsGlobal}

  private implicit val document: Document = org.scalajs.dom.document
  private implicit val window: Window = org.scalajs.dom.window

  def apply[F[_]](implicit C: Clipboard[F]): Clipboard[F] = C

  def make[F[_] : Async](implicit document: Document, window: Window): Clipboard[F] =
    new Clipboard(Dispatcher.parallel[F])

  @JSExport
  def copy(text: String): Promise[Boolean] =
    new Clipboard(Dispatcher.parallel[IO])
      .copy(text)
      .unsafeToFuture()
      .toJSPromise
}

class Clipboard[F[_] : Sync](dispatcherResource: Resource[F, Dispatcher[F]])
                            (implicit document: Document, window: Window) {
  def copy(text: String): F[Boolean] =
    dispatcherResource
      .allocated
      .flatMap { case (dispatcher, shutdownDispatcher) =>
        for {
          textElement <- createElementWithText("pre", text)
          button <- createElementWithText("button", "Click to Copy")
          _ <- buildListener(dispatcher, shutdownDispatcher)(textElement, button)
          _ <- appendChild(textElement)
          _ <- appendChild(button)
          copySuccess <- copyNodeContents(textElement)
          _ <- if (copySuccess) cleanup(shutdownDispatcher)(textElement, button) else scrollElementIntoView(button)
        } yield copySuccess
      }

  private def scrollElementIntoView(element: Element): F[Unit] = Sync[F].delay(element.scrollIntoView())

  private def appendChild(element: Element): F[Node] = Sync[F].delay(document.body.appendChild(element))

  private def createElementWithText(tag: String, textContent: String): F[Element] =
    Sync[F].delay(document.createElement(tag))
      .flatTap(elem => Sync[F].delay(elem.appendChild(document.createTextNode(textContent))))

  private def buildListener(dispatcher: Dispatcher[F],
                            shutdownDispatcher: F[Unit])
                           (textElement: Element, button: Element): F[Unit] = Sync[F].delay {
    lazy val clickListener: js.Function1[Event, Any] = (_: Event) =>
      dispatcher.unsafeRunAndForget {
        Resource.make(copyNodeContents(textElement)) {
          (Sync[F].delay(button.removeEventListener("click", clickListener)) >> cleanup(shutdownDispatcher)(textElement, button)).whenA
        }.use_
      }

    button.addEventListener("click", clickListener)
  }

  private def windowSelection(): F[Selection] = Sync[F].delay(window.getSelection())

  private def removeAllRanges(): F[Unit] =
    windowSelection()
      .flatMap(selection => Sync[F].delay(selection.removeAllRanges()))

  private def copyNodeContents(element: Element): F[Boolean] =
    for {
      _ <- removeAllRanges()
      range <- Sync[F].delay(document.createRange())
      _ <- Sync[F].delay(range.selectNode(element))
      selection <- windowSelection()
      _ <- Sync[F].delay(selection.addRange(range))
      success <- Sync[F].delay(document.execCommand("copy"))
    } yield success

  private def cleanup(shutdownDispatcher: F[Unit])
                     (elements: Element*): F[Unit] =
    removeAllRanges() >>
      elements.toList.traverse_(node => Sync[F].delay(node.parentNode.removeChild(node))) >>
      shutdownDispatcher

}
