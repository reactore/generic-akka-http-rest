package reactore.core

import akka.http.scaladsl.server.Directives

/**
  * Created by yadu on 14/10/16.
  */
abstract class BaseRest[T <: BaseTable[E], E <: BaseEntity](val pathName: String, repository: BaseRepositoryComponent[T, E])(implicit m: Manifest[E])
  extends Directives with JsonConverter {

  val route = path(pathName) {
    get {
      complete(futureToJson(repository.getAll))
    } ~ post {
      entity(as[String]) { json =>
        val extractedEntity = fromJson[E](json)
        complete(futureToJson(repository.save(extractedEntity)))
      }
    }
  } ~ path(pathName / LongNumber) { id =>
    get {
      complete(futureToJson(repository.getById(id)))
    } ~ put {
      entity(as[String]) { json =>
        val extractedEntity = fromJson[E](json)
        complete(futureToJsonAny(repository.updateById(id, extractedEntity)))
      }
    } ~ delete {
      complete(futureToJsonAny(repository.deleteById(id)))
    }
  }
}