package Model

import java.util.Date


case class Facultet(
                     facultetId:Int,
                     name:String,
                     spisok_kafedr:List[Int],
                     spisok_studentof:List[Int],
                     raspolozhenya:String,
                     god_osnovanya:Date,
                     kontaktnyi_tel:String,
                     email:String,
                     spisok_sportivnyx_komand:List[Int],
                     spisok_nauchnyx_center:List[Int],
                     spisok_kursov:List[Int],
                   )
