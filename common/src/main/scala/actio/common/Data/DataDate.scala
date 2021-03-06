package actio.common.Data

import java.text.SimpleDateFormat

case class DataDate(label: String, date: Long) extends DataBase {

  override def stringOption: Option[String] = Some(this.toString)

  override def toString: String = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(date)
}

object DataDate {

  private val label = "date"

  def apply(date: java.util.Date): DataDate = DataDate(label, date.getTime)

  def apply(date: Long): DataDate = DataDate(label, date)

  def apply(label: String, date: java.util.Date): DataDate =
    DataDate(label, date.getTime)
}