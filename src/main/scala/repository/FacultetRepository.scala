package repository

import java.util.Date
import org.mongodb.scala.Document
import org.mongodb.scala.bson.{BsonArray, BsonDocument, BsonInt32, BsonString}
import org.mongodb.scala.model.Filters.{equal, regex}
import org.mongodb.scala.model.Updates.{addToSet, combine, set}
import org.mongodb.scala.result.UpdateResult
import scala.util.Try
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Connection._
import Model._

import java.text.SimpleDateFormat

class FacultetRepository(implicit ec: ExecutionContext){

  def getAllFacultets(): Future[List[Facultet]] = {
    val futureFacultets = Mongodbcollection.facultetCollection.find().toFuture()

    futureFacultets.map { docs =>
      Option(docs).map(_.map { doc =>
        Facultet(
          facultetId = doc.getInteger("facultetId"),
          name = doc.getString("name"),
          spisok_kafedr = Option(doc.getList("spisok_kafedr", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
          spisok_studentof = Option(doc.getList("spisok_studentof", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
          raspolozhenya = doc.getString("raspolozhenya"),
          god_osnovanya =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("god_osnovanya")),
          kontaktnyi_tel = doc.getString("kontaktnyi_tel"),
          email = doc.getString("email"),
          spisok_sportivnyx_komand = Option(doc.getList("spisok_sportivnyx_komand", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
          spisok_nauchnyx_center = Option(doc.getList("spisok_nauchnyx_center", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
          spisok_kursov = Option(doc.getList("spisok_kursov", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getFacultetById(facultetId: String): Future[Option[Facultet]] = {
    val facultetDocument = Document("facultetId" -> facultetId.toInt)

    Mongodbcollection.facultetCollection.find(facultetDocument).headOption().map {
      case Some(doc) =>
        Some(
          Facultet(
            facultetId = doc.getInteger("facultetId"),
            name = doc.getString("name"),
            spisok_kafedr = Option(doc.getList("spisok_kafedr", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
            spisok_studentof = Option(doc.getList("spisok_studentof", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
            raspolozhenya = doc.getString("raspolozhenya"),
            god_osnovanya =  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("god_osnovanya")),
            kontaktnyi_tel = doc.getString("kontaktnyi_tel"),
            email = doc.getString("email"),
            spisok_sportivnyx_komand = Option(doc.getList("spisok_sportivnyx_komand", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
            spisok_nauchnyx_center = Option(doc.getList("spisok_nauchnyx_center", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
            spisok_kursov = Option(doc.getList("spisok_kursov", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)
          )
        )
      case None => None
    }
  }
  def facultetfiltr(param: String): Future[List[Facultet]] = {
    val keyValue = param.split("=")
    if (keyValue.length == 2) {
      val key = keyValue(0)
      val value = keyValue(1)
      val facultyDocument = Document(key -> value)
      Mongodbcollection.facultetCollection
        .find(facultyDocument)
        .toFuture()
        .map { docs =>
          docs.map { doc =>
            Facultet(
              facultetId = doc.getInteger("facultetId"),
              name = doc.getString("name"),
              spisok_kafedr = Option(doc.getList("spisok_kafedr", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
              spisok_studentof = Option(doc.getList("spisok_studentof", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
              raspolozhenya = doc.getString("raspolozhenya"),
              god_osnovanya = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").parse(doc.getString("god_osnovanya")),
              kontaktnyi_tel = doc.getString("kontaktnyi_tel"),
              email = doc.getString("email"),
              spisok_sportivnyx_komand = Option(doc.getList("spisok_sportivnyx_komand", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
              spisok_nauchnyx_center = Option(doc.getList("spisok_nauchnyx_center", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty),
              spisok_kursov = Option(doc.getList("spisok_kursov", classOf[Integer])).map(_.asScala.map(_.toInt).toList).getOrElse(List.empty)
            )
          }.toList
        }
    } else {
      // Обработка некорректного ввода
      Future.failed(new IllegalArgumentException("Неверный формат параметра"))
    }
  }

  def addFacultet(facultet: Facultet): Future[String] = {
    val facultetDocument = BsonDocument(
      "facultetId" -> BsonInt32(facultet.facultetId),
      "name" -> BsonString(facultet.name),
      "spisok_kafedr" -> BsonArray(facultet.spisok_kafedr.map(BsonInt32(_))),
      "spisok_studentof" -> BsonArray(facultet.spisok_studentof.map(BsonInt32(_))),
      "raspolozhenya" -> BsonString(facultet.raspolozhenya),
      "god_osnovanya" ->  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(facultet.god_osnovanya) ,
      "kontaktnyi_tel" -> BsonString(facultet.kontaktnyi_tel),
      "email" -> BsonString(facultet.email),
      "spisok_sportivnyx_komand" -> BsonArray(facultet.spisok_sportivnyx_komand.map(BsonInt32(_))),
      "spisok_nauchnyx_center" -> BsonArray(facultet.spisok_nauchnyx_center.map(BsonInt32(_))),
      "spisok_kursov" -> BsonArray(facultet.spisok_kursov.map(BsonInt32(_)))
    )

    Mongodbcollection.facultetCollection.insertOne(facultetDocument).toFuture().map(_ => s"Факультет - ${facultet.name} был добавлен в базу данных.")
  }

  def deleteFacultet(facultetId: String): Future[String] = {
    val facultetDocument = Document("facultetId" -> facultetId.toInt)
    Mongodbcollection.facultetCollection.deleteOne(facultetDocument).toFuture().map(_ => s"Факультет с id ${facultetId} был удален из базы данных.")
  }

  def updateFacultet(facultetId: String, updatedFacultet: Facultet): Future[String] = {
    val filter = Document("facultetId" -> facultetId.toInt)

    val facultetDocument = BsonDocument(
      "$set" -> BsonDocument(
        "facultetId" -> BsonInt32(updatedFacultet.facultetId),
        "name" -> BsonString(updatedFacultet.name),
        "spisok_kafedr" -> BsonArray(updatedFacultet.spisok_kafedr.map(BsonInt32(_))),
        "spisok_studentof" -> BsonArray(updatedFacultet.spisok_studentof.map(BsonInt32(_))),
        "raspolozhenya" -> BsonString(updatedFacultet.raspolozhenya),
        "god_osnovanya" ->  new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(updatedFacultet.god_osnovanya) ,
        "kontaktnyi_tel" -> BsonString(updatedFacultet.kontaktnyi_tel),
        "email" -> BsonString(updatedFacultet.email),
        "spisok_sportivnyx_komand" -> BsonArray(updatedFacultet.spisok_sportivnyx_komand.map(BsonInt32(_))),
        "spisok_nauchnyx_center" -> BsonArray(updatedFacultet.spisok_nauchnyx_center.map(BsonInt32(_))),
        "spisok_kursov" -> BsonArray(updatedFacultet.spisok_kursov.map(BsonInt32(_)))
      )
    )

    Mongodbcollection.facultetCollection.updateOne(filter, facultetDocument).toFuture().map { updatedResult =>
      if (updatedResult.wasAcknowledged() && updatedResult.getModifiedCount > 0) {
        s"Информация о факультете с id ${facultetId} была успешно обновлена."
      } else {
        s"Обновление информации о факультете с id ${facultetId} не выполнено. Возможно, факультет не найден или произошла ошибка в базе данных."
      }

    }
  }
}