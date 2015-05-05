package io.surfkit.core.service.v1


import io.surfkit.core.api.Configuration
import io.surfkit.core.websocket.WebSocket
import akka.actor.{ ActorRef, ActorSystem }
import spray.http.StatusCodes
import spray.routing.Directives

class SurfKitService(v1 : ActorRef)(implicit system : ActorSystem) extends Directives {
  lazy val route =
    pathPrefix("v1") {
      val dir = "v1/"
      pathEndOrSingleSlash {
        getFromResource(dir + "index.html")
      } ~
        path("ws") {
          requestUri { uri =>
            val wsUri = uri.withPort(Configuration.portWs)
            system.log.debug("redirect {} to {}", uri, wsUri)
            redirect(wsUri, StatusCodes.PermanentRedirect)
          }
        } ~
        getFromResourceDirectory(dir)
    }
  lazy val wsroute =
    pathPrefix("v1") {
      path("ws") {
        implicit ctx =>
          ctx.responder ! WebSocket.Register(ctx.request, v1, true)
      }
    }
}