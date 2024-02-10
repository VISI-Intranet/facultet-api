package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import repository.SportKomandRepository
import Model._


class SportKomandRoutes(implicit val sportKomandRepository: SportKomandRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("sport_komand") {
      concat(
        pathEnd {
          concat(
            get {
              parameter("name") { name =>
                complete(sportKomandRepository.getSportKomandByName(name))
              }
            }
            ,
          concat(
            get {
              complete(sportKomandRepository.getAllSportKomands())
            },
            post {
              entity(as[Sportkomand]) { sportKomand =>
                complete(sportKomandRepository.addSportKomand(sportKomand))
              }
            }
          )
          )
        },
        path(Segment) { komandID =>
          concat(
            get {
              complete(sportKomandRepository.getSportKomandById(komandID))
            },
            put {
              entity(as[Sportkomand]) { updatedSportKomand =>
                complete(sportKomandRepository.updateSportKomand(komandID, updatedSportKomand))
              }
            },
            delete {
              complete(sportKomandRepository.deleteSportKomand(komandID))
            }
          )
        }
      )
    }
}