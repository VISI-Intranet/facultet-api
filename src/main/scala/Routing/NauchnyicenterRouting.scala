package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, jackson}
import repository.NauchnyiCenterRepository
import Model._

class NauchnyiCenterRoutes(implicit val centerRepository: NauchnyiCenterRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("nauchnyi_center") {
      concat(
        pathEnd {
          concat(
            get {
              complete(centerRepository.getAllCenters())
            },
            post {
              entity(as[Nauchnyicenter]) { center =>
                complete(centerRepository.addCenter(center))
              }
            }
          )
        },
        path(Segment) { centerId =>
          concat(
            get {
              complete(centerRepository.getCenterById(centerId))
            },
            put {
              entity(as[Nauchnyicenter]) { updatedCenter =>
                complete(centerRepository.updateCenter(centerId, updatedCenter))
              }
            },
            delete {
              complete(centerRepository.deleteCenter(centerId))
            }
          )
        }
      )
    }
}