package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model._

class NauchnyiCenterRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val centerCollection: MongoCollection[Document] = db.getCollection("nauchnyi_center")

  def getAllCenters(): Future[List[Nauchnyicenter]] = {
    val futureCenters = centerCollection.find().toFuture()

    futureCenters.map { docs =>
      Option(docs).map(_.map { doc =>
        Nauchnyicenter(
          centerId = doc.getInteger("centerId"),
          name = doc.getString("name")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getCenterById(centerId: String): Future[Option[Nauchnyicenter]] = {
    val centerDocument = Document("centerId" -> centerId.toInt)

    centerCollection.find(centerDocument).headOption().map {
      case Some(doc) =>
        Some(
          Nauchnyicenter(
            centerId = doc.getInteger("centerId"),
            name = doc.getString("name")
          )
        )
      case None => None
    }
  }

  def addCenter(center: Nauchnyicenter): Future[String] = {
    val centerDocument = Document(
      "centerId" -> center.centerId,
      "name" -> center.name
    )

    centerCollection.insertOne(centerDocument).toFuture().map(_ => s"Научный центр - ${center.name} был добавлен в базу данных.")
  }

  def deleteCenter(centerId: String): Future[String] = {
    val centerDocument = Document("centerId" -> centerId.toInt)
    centerCollection.deleteOne(centerDocument).toFuture().map(_ => s"Научный центр с id ${centerId} был удален из базы данных.")
  }

  def updateCenter(centerId: String, updatedCenter: Nauchnyicenter): Future[String] = {
    val filter = Document("centerId" -> centerId.toInt)

    val centerDocument = Document(
      "$set" -> Document(
        "centerId" -> updatedCenter.centerId,
        "name" -> updatedCenter.name
      )
    )

    centerCollection.updateOne(filter, centerDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о научном центре с id ${centerId} была успешно обновлена."
      } else {
        s"Обновление информации о научном центре с id ${centerId} не выполнено. Возможно, центр не найден или произошла ошибка в базе данных."
      }
    }
  }
}