package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.{set, combine, addToSet}
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model.{Kursyi, KursStatus}

class KursyiRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val kursyiCollection: MongoCollection[Document] = db.getCollection("kursyi")

  def getAllKursyi(): Future[List[Kursyi]] = {
    val futureKursyi = kursyiCollection.find().toFuture()

    futureKursyi.map { docs =>
      Option(docs).map(_.map { doc =>
        Kursyi(
          kursId = doc.getInteger("kursId"),
          status = KursStatus.withName(doc.getString("status"))
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getKursyiById(kursId: String): Future[Option[Kursyi]] = {
    val kursyiDocument = Document("kursId" -> kursId.toInt)

    kursyiCollection.find(kursyiDocument).headOption().map {
      case Some(doc) =>
        Some(
          Kursyi(
            kursId = doc.getInteger("kursId"),
            status = KursStatus.withName(doc.getString("status"))
          )
        )
      case None => None
    }
  }

  def addKursyi(kursyi: Kursyi): Future[String] = {
    val kursyiDocument = Document(
      "kursId" -> kursyi.kursId,
      "status" -> kursyi.status.toString,
    )

    kursyiCollection.insertOne(kursyiDocument).toFuture().map(_ => s"Курс - ${kursyi.kursId} был добавлен в базу данных.")
  }

  def deleteKursyi(kursId: String): Future[String] = {
    val kursyiDocument = Document("kursId" -> kursId.toInt)
    kursyiCollection.deleteOne(kursyiDocument).toFuture().map(_ => s"Курс с id ${kursId} был удален из базы данных.")
  }

  def updateKursyi(kursId: String, updatedKursyi: Kursyi): Future[String] = {
    val filter = Document("kursId" -> kursId.toInt)

    val kursyiDocument = Document(
      "$set" -> Document(
        "kursId" -> updatedKursyi.kursId,
        "status" -> updatedKursyi.status.toString
      )
    )

    kursyiCollection.updateOne(filter, kursyiDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о курсе с id ${kursId} была успешно обновлена."
      } else {
        s"Обновление информации о курсе с id ${kursId} не выполнено. Возможно, курс не найден или произошла ошибка в базе данных."
      }
    }
  }
}