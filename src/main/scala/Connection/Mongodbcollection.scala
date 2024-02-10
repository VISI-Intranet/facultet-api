package Connection
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoDatabase}
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._



object Mongodbcollection {
  private val mongoClient = MongoClient("mongodb://localhost:27017")
  val database: MongoDatabase = mongoClient.getDatabase("UniverFacultet")
  val facultetCollection: MongoCollection[Document] = database.getCollection("facultet")
  val kafedraCollection: MongoCollection[Document] = database.getCollection("kafedra")
  val kursyiCollection: MongoCollection[Document] = database.getCollection("kursyi")
  val studentCollection: MongoCollection[Document] = database.getCollection("student")
  val sport_komandCollection: MongoCollection[Document] = database.getCollection("sport_komand")
  val prepodavatelyCollection: MongoCollection[Document] = database.getCollection("prepodavately")
  val nauchnyi_centerCollection: MongoCollection[Document] = database.getCollection("nauchnyi_center")
}