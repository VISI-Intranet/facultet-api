package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.KafedraRepository
import Model._

class KafedraRoutes(implicit val kafedraRepository:KafedraRepository)  extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("kafedra") {
      concat(
        pathEnd {
          concat(
            get {
              complete(kafedraRepository.getAllKafedras())
            },
            post {
              entity(as[Kafedra]) { kafedra =>
                complete(kafedraRepository.addKafedra(kafedra))
              }
            }
          )
        },
        path(Segment) { kafedraId =>
          concat(
            get {
              complete(kafedraRepository.getKafedraById(kafedraId))
            },
            put {
              entity(as[Kafedra]) { updatedKafedra =>
                complete(kafedraRepository.updateKafedra(kafedraId, updatedKafedra))
              }
            },
            delete {
              complete(kafedraRepository.deleteKafedra(kafedraId))
            }
          )
        }
      )
    }
}