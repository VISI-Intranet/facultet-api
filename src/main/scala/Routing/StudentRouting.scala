package Routing

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.{DefaultFormats, Formats, jackson}
import repository.StudentRepository
import Model._

class StudentRoutes(implicit val studentRepository: StudentRepository) extends Json4sSupport {
  implicit val serialization = jackson.Serialization
  implicit val formats = DefaultFormats

  val route =
    pathPrefix("students") {
      concat(
        pathEnd {
          concat(
            get {
              complete(studentRepository.getAllStudents())
            },
            post {
              entity(as[Student]) { student =>
                complete(studentRepository.addStudent(student))
              }
            }
          )
        },
        path(Segment) { studentId =>
          concat(
            get {
              complete(studentRepository.getStudentById(studentId))
            },
            put {
              entity(as[Student]) { updatedStudent =>
                complete(studentRepository.updateStudent(studentId, updatedStudent))
              }
            },
            delete {
              complete(studentRepository.deleteStudent(studentId))
            }
          )
        }
      )
    }
}