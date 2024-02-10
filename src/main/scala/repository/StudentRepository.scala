package repository

import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Projections.excludeId
import org.mongodb.scala.model.Sorts.descending
import org.mongodb.scala.{Document, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Updates.{combine, set}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import Model._

class StudentRepository(implicit ec: ExecutionContext, db: MongoDatabase) {

  val studentCollection: MongoCollection[Document] = db.getCollection("students")

  def getAllStudents(): Future[List[Student]] = {
    val futureStudents = studentCollection.find().toFuture()

    futureStudents.map { docs =>
      Option(docs).map(_.map { doc =>
        Student(
          studentId = doc.getInteger("studentId"),
          name = doc.getString("name")
        )
      }.toList).getOrElse(List.empty)
    }
  }

  def getStudentById(studentId: String): Future[Option[Student]] = {
    val studentDocument = Document("studentId" -> studentId.toInt)

    studentCollection.find(studentDocument).headOption().map {
      case Some(doc) =>
        Some(
          Student(
            studentId = doc.getInteger("studentId"),
            name = doc.getString("name")
          )
        )
      case None => None
    }
  }

  def addStudent(student: Student): Future[String] = {
    val studentDocument = Document(
      "studentId" -> student.studentId,
      "name" -> student.name
    )

    studentCollection.insertOne(studentDocument).toFuture().map(_ => s"Студент - ${student.name} был добавлен в базу данных.")
  }

  def deleteStudent(studentId: String): Future[String] = {
    val studentDocument = Document("studentId" -> studentId.toInt)
    studentCollection.deleteOne(studentDocument).toFuture().map(_ => s"Студент с id ${studentId} был удален из базы данных.")
  }

  def updateStudent(studentId: String, updatedStudent: Student): Future[String] = {
    val filter = Document("studentId" -> studentId.toInt)

    val studentDocument = Document(
      "$set" -> Document(
        "studentId" -> updatedStudent.studentId,
        "name" -> updatedStudent.name
      )
    )

    studentCollection.updateOne(filter, studentDocument).toFuture().map { result =>
      if (result.getMatchedCount > 0) {
        s"Информация о студенте с id ${studentId} была успешно обновлена."
      } else {
        s"Студент с id ${studentId} не найден."
      }
    }
  }
}