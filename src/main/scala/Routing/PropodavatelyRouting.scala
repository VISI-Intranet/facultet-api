package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import repository.PrepodavatelyRepository
import Model._

class PrepodavatelyRoutes(implicit val prepodavatelyRepository: PrepodavatelyRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("prepodavately") {
      concat(
        pathEnd {
          concat(
            get {
              complete(prepodavatelyRepository.getAllPrepodavateli())
            },
            post {
              entity(as[Prepodavately]) { prepodavatel =>
                complete(prepodavatelyRepository.addPrepodavatel(prepodavatel))
              }
            }
          )
        },
        path(Segment) { prepodId =>
          concat(
            get {
              complete(prepodavatelyRepository.getPrepodavatelById(prepodId))
            },
            put {
              entity(as[Prepodavately]) { updatedPrepodavatel =>
                complete(prepodavatelyRepository.updatePrepodavatel(prepodId, updatedPrepodavatel))
              }
            },
            delete {
              complete(prepodavatelyRepository.deletePrepodavatel(prepodId))
            }
          )
        }
      )
    }
}