package repository

import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.{addToSet, set}
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import org.mongodb.scala.Document
import Model._
import Connection._

class KafedraRepository(implicit ec: ExecutionContext) {

  def getAllKafedras(): Future[List[Kafedra]] = {
    val futureKafedras = Mongodbcollection.kafedraCollection.find().toFuture()

    futureKafedras.map { docs =>
      Option(docs).map(_.map { doc =>
        Kafedra(
          kafedraId = doc.getInteger("kafedraId"),
          name = doc.getString("name"),

        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getKafedraById(kafedraId: String): Future[Option[Kafedra]] = {
    val kafedraDocument = Document("kafedraId" -> kafedraId.toInt)

    Mongodbcollection.kafedraCollection.find(kafedraDocument).headOption().map {
      case Some(doc) =>
        Some(
          Kafedra(
            kafedraId = doc.getInteger("kafedraId"),
            name = doc.getString("name"),

          )
        )
      case None => None
    }
  }

  def addKafedra(kafedra: Kafedra): Future[String] = {
    val kafedraDocument = BsonDocument(
      "kafedraId" -> BsonInt32(kafedra.kafedraId),
      "name" -> BsonString(kafedra.name),

    )

    Mongodbcollection.kafedraCollection.insertOne(kafedraDocument).toFuture().map(_ => s"Кафедра - ${kafedra.name} была добавлена в базу данных.")
  }

  def deleteKafedra(kafedraId: String): Future[String] = {
    val kafedraDocument = Document("kafedraId" -> kafedraId.toInt)
    Mongodbcollection.kafedraCollection.deleteOne(kafedraDocument).toFuture().map(_ => s"Кафедра с id ${kafedraId} была удалена из базы данных.")
  }

  def updateKafedra(kafedraId: String, updatedKafedra: Kafedra): Future[String] = {
    val filter = Document("kafedraId" -> kafedraId.toInt)

    val kafedraDocument = BsonDocument(
      "$set" -> BsonDocument(
        "kafedraId" -> BsonInt32(updatedKafedra.kafedraId),
        "name" -> BsonString(updatedKafedra.name),

      )
    )

    Mongodbcollection.kafedraCollection.updateOne(filter, kafedraDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о кафедре с id ${kafedraId} была успешно обновлена."
      } else {
        s"Обновление информации о кафедре с id ${kafedraId} не выполнено. Возможно, кафедра не найдена или произошла ошибка в базе данных."
      }
    }
  }
}