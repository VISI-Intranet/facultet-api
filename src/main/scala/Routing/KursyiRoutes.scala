package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import repository.KursyiRepository
import Model._

class KursyiRoutes(implicit val kursyiRepository: KursyiRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats: Formats = JsonFormats.formats


  val route =
    pathPrefix("kursyi") {
      concat(
        pathEnd {
          concat(
            get {
              complete(kursyiRepository.getAllKursyi())
            },
            post {
              entity(as[Kursyi]) { kursyi =>
                complete(kursyiRepository.addKursyi(kursyi))
              }
            }
          )
        },
        path(Segment) { kursId =>
          concat(
            get {
              complete(kursyiRepository.getKursyiById(kursId))
            },
            put {
              entity(as[Kursyi]) { updatedKursyi =>
                complete(kursyiRepository.updateKursyi(kursId, updatedKursyi))
              }
            },
            delete {
              complete(kursyiRepository.deleteKursyi(kursId))
            }
          )
        }
      )
    }
}