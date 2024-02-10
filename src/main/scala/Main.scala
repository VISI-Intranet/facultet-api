import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mongodb.scala.MongoClient
import repository._
import Routing._
import Model._

import scala.concurrent.{ExecutionContextExecutor, Future}
import java.util.Date
import Model._
import akka.http.scaladsl.Http
object Main extends App {

  implicit val system: ActorSystem = ActorSystem("MyAkkaHttpServer")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  // Подключение к базе данных
  val client = MongoClient()
  implicit val db = client.getDatabase("UniverFacultet")

  implicit val facultetRepository = new FacultetRepository()
  implicit val kafedraRepository = new KafedraRepository()
  implicit val kursyiRepository = new KursyiRepository()
  implicit val nauchnyiCenterRepository = new NauchnyiCenterRepository()
  implicit val prepodavatelyRepository = new PrepodavatelyRepository()
  implicit val sportKomandRepository = new SportKomandRepository()
  implicit val studentRepository = new StudentRepository()

  val facultetRoute = new FacultetRoutes()
  val kafedraRoutes = new KafedraRoutes()
  val kursyiRoutes = new KursyiRoutes()
  val nauchnyiCenterRoutes = new NauchnyiCenterRoutes()
  val prepodavatelyRoutes = new PrepodavatelyRoutes()
  val sportKomandRoutes = new SportKomandRoutes()
  val studentRoutes = new StudentRoutes()


  // Старт сервера
  private val bindingFuture = Http().bindAndHandle(sportKomandRoutes.route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/")

  // Остановка сервера при завершении приложения
  sys.addShutdownHook {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}