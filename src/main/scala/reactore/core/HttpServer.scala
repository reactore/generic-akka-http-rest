package reactore.core

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.{Directives, Route, RouteConcatenation}
import akka.stream.ActorMaterializer
import reactore.rest.{EmployeeRestApi, UserRestApi}
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by yadu on 14/10/16.
  */
object HttpServer extends RouteConcatenation with App with RouteBuilder {
  implicit val system = ActorSystem("HttpSystem")

  implicit val mat = ActorMaterializer()

  val routes: Route = buildFullRoutes

  Http().bindAndHandle(routes, interface = "localhost", port = 8280).map { x =>
    println("successfully bound to http://localhost:8280")
  }.recover {
    case ex => println("Failed to bind to localhost:8280")
  }
}

trait RouteBuilder extends Directives {
  def buildFullRoutes = {
    pathPrefix("hr") {
      EmployeeRestApi.moreRoutes ~ UserRestApi.route
    }
  }
}