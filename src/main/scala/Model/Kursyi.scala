package Model
import org.json4s.JsonAST.JString
import org.json4s.{CustomSerializer, DefaultFormats, Formats, MappingException}
object KursStatus extends Enumeration {
  type KursStatus = Value
  val Завершено, Активный = Value
}

case class Kursyi(
                   kursId: Int,
                   status: KursStatus.KursStatus,

                 )
object KursStatusSerializer extends CustomSerializer[KursStatus.KursStatus](format => (
  {
    case JString(s) => KursStatus.withName(s)
    case value => throw new MappingException(s"Can't convert $value to KursStatus")
  },
  {
    case kursStatus: KursStatus.KursStatus => JString(kursStatus.toString)
  }
))
object JsonFormats {
  implicit val formats: Formats = DefaultFormats + KursStatusSerializer
}