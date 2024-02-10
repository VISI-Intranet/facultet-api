package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model._

import scala.util.Try

class SportKomandRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val sportKomandCollection: MongoCollection[Document] = db.getCollection("sport_komands")

  def getAllSportKomands(): Future[List[Sportkomand]] = {
    val futureSportKomands = sportKomandCollection.find().toFuture()

    futureSportKomands.map { docs =>
      Option(docs).map(_.map { doc =>
        Sportkomand(
          komandID = doc.getInteger("komandID"),
          name = doc.getString("name"),
          vid_sporta = doc.getString("vid_sporta")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getSportKomandById(komandID: String): Future[Option[Sportkomand]] = {
    val sportKomandDocument = Document("komandID" -> komandID.toInt)

    sportKomandCollection.find(sportKomandDocument).headOption().map {
      case Some(doc) =>
        Some(
          Sportkomand(
            komandID = doc.getInteger("komandID"),
            name = doc.getString("name"),
            vid_sporta = doc.getString("vid_sporta")
          )
        )
      case None => None
    }
  }

//  def getSportKomandByNAME(param: String): Future[Option[Sport_komand]] = {
//    val keyValue = param.split("=")
//
//    if (keyValue.length == 2) {
//      val key = keyValue(0)
//      val value = Try(keyValue(1).toInt).toOption;
//      val sportKomandDocument = Document(key -> value)
//      sportKomandCollection.find(sportKomandDocument).headOption().map {
//        case Some(doc) =>
//          Some(
//            Sport_komand(
//              komandID = doc.getInteger("komandID"),
//              name = doc.getString("name"),
//              vid_sporta = doc.getString("vid_sporta")
//            )
//          )
//        case None => None
//      }
//    } else {
//      // Обработка некорректного ввода
//      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
//    }
//    }


  def getSportKomandByName(typeSport: String): Future[List[Option[Sportkomand]]] = {
    val sportKomandDocument = equal("vid_sporta", typeSport)

    sportKomandCollection.find(sportKomandDocument).toFuture().map { docs =>
      docs.map { doc =>
        Some(
          Sportkomand(
            komandID = doc.getInteger("komandID"),
            name = doc.getString("name"),
            vid_sporta = doc.getString("vid_sporta")
          )
        )
      }.toList
    }
  }
//    Some(
//      Sport_komand(
//        komandID = doc.getInteger("komandID"),
//        name = doc.getString("name"),
//        vid_sporta = doc.getString("vid_sporta")
//      )
//    )
    def addSportKomand(sportKomand: Sportkomand): Future[String] = {
      val sportKomandDocument = Document(
        "komandID" -> sportKomand.komandID,
        "name" -> sportKomand.name,
        "vid_sporta" -> sportKomand.vid_sporta
      )

      sportKomandCollection.insertOne(sportKomandDocument).toFuture().map(_ => s"Спортивная команда - ${sportKomand.name} была добавлена в базу данных.")
    }

    def deleteSportKomand(komandID: String): Future[String] = {
      val sportKomandDocument = Document("komandID" -> komandID.toInt)
      sportKomandCollection.deleteOne(sportKomandDocument).toFuture().map(_ => s"Спортивная команда с id ${komandID} была удалена из базы данных.")
    }

    def updateSportKomand(komandID: String, updatedSportKomand: Sportkomand): Future[String] = {
      val filter = Document("komandID" -> komandID.toInt)

      val sportKomandDocument = Document(
        "$set" -> Document(
          "komandID" -> updatedSportKomand.komandID,
          "name" -> updatedSportKomand.name,
          "vid_sporta" -> updatedSportKomand.vid_sporta

        )
      )

      sportKomandCollection.updateOne(filter, sportKomandDocument).toFuture().map { updatedResult =>
        if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
          s"Информация о спортивной команде с id ${komandID} была успешно обновлена."
        } else {
          s"Обновление информации о спортивной команде с id ${komandID} не выполнено. Возможно, команда не найдена или произошла ошибка в базе данных."
        }
      }
    }

  }
