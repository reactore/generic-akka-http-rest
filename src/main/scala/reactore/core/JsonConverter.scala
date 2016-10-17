package reactore.core

import java.text.SimpleDateFormat

import akka.http.javadsl.model.ResponseEntity
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.ContentNegotiator.Alternative.MediaType
import org.json4s.JsonAST.{JNull, JString}
import org.json4s.{CustomSerializer, DefaultFormats, Formats}
import org.json4s.jackson.Serialization

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by yadu on 14/10/16.
  */

trait DateSerializer {
  case object SqlDateSerializer extends CustomSerializer[java.sql.Date](format => ( {
    case JString(date) => {
      val utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
      new java.sql.Date(utilDate.getTime)
    }
    case JNull         => null
  }, {
    case date: java.sql.Date => JString(date.toString)
  }))

}

trait JsonConverter extends DateSerializer {
  implicit val formats: Formats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd")
  } ++ List(SqlDateSerializer)

  def toJson(obj: AnyRef): String = {
    Serialization.write(obj)
  }

  def futureToJson(obj: Future[AnyRef]): Future[HttpResponse] = {
    obj.map { x =>
      println("received data " + x)
      HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, Serialization.write(x)))
    }.recover {
      case ex =>  ex.printStackTrace(); HttpResponse(status = StatusCodes.InternalServerError)
    }

  }

  def futureToJsonAny(obj: Future[Any]): Future[HttpResponse] = {
    obj.map { x =>
      HttpResponse(status = StatusCodes.OK, entity = HttpEntity(MediaTypes.`application/json`, s"""{status : ${x}"""))
    }.recover {
      case ex => HttpResponse(status = StatusCodes.InternalServerError)
    }

  }

  def fromJson[E](json: String)(implicit m: Manifest[E]): E = {
    Serialization.read[E](json)
  }

}