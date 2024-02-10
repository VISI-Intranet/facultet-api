package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model._

class PrepodavatelyRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val prepodCollection: MongoCollection[Document] = db.getCollection("prepodavateli")

  def getAllPrepodavateli(): Future[List[Prepodavately]] = {
    val futurePrepodavateli = prepodCollection.find().toFuture()

    futurePrepodavateli.map { docs =>
      Option(docs).map(_.map { doc =>
        Prepodavately(
          prepodId = doc.getInteger("prepodId"),
          fio = doc.getString("fio")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getPrepodavatelById(prepodId: String): Future[Option[Prepodavately]] = {
    val prepodDocument = Document("prepodId" -> prepodId.toInt)

    prepodCollection.find(prepodDocument).headOption().map {
      case Some(doc) =>
        Some(
          Prepodavately(
            prepodId = doc.getInteger("prepodId"),
            fio = doc.getString("fio")
          )
        )
      case None => None
    }
  }

  def addPrepodavatel(prepodavatel: Prepodavately): Future[String] = {
    val prepodDocument = Document(
      "prepodId" -> prepodavatel.prepodId,
      "fio" -> prepodavatel.fio
    )

    prepodCollection.insertOne(prepodDocument).toFuture().map(_ => s"Преподаватель - ${prepodavatel.fio} был добавлен в базу данных.")
  }

  def deletePrepodavatel(prepodId: String): Future[String] = {
    val prepodDocument = Document("prepodId" -> prepodId.toInt)
    prepodCollection.deleteOne(prepodDocument).toFuture().map(_ => s"Преподаватель с id ${prepodId} был удален из базы данных.")
  }

  def updatePrepodavatel(prepodId: String, updatedPrepodavatel: Prepodavately): Future[String] = {
    val filter = Document("prepodId" -> prepodId.toInt)

    val prepodDocument = Document(
      "$set" -> Document(
        "prepodId" -> updatedPrepodavatel.prepodId,
        "fio" -> updatedPrepodavatel.fio
      )
    )

    prepodCollection.updateOne(filter, prepodDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о преподавателе с id ${prepodId} была успешно обновлена."
      } else {
        s"Обновление информации о преподавателе с id ${prepodId} не выполнено. Возможно, преподаватель не найден или произошла ошибка в базе данных."
      }
    }
  }
}