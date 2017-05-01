package DataSources

import Common._
import java.io.FileReader
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.async.Async.{async, await}
import Common.Data.{DataArray, DataNothing, DataRecord, DataString}
import org.apache.commons.csv.{CSVFormat, CSVParser}

class CSVDataSource extends DataSource {

  def exec(parameters: Parameters): Observable[DataSet] = ???

  def run(observer: Observer[DataSet], parameters: Parameters): Future[Unit] = async {
    import collection.JavaConverters._

    val filePath = parameters("filePath").stringOption.getOrElse("")
    val batchSize: Int = 10
    val in = new FileReader(filePath)
    val parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in)
    val it = parser.asScala.grouped(batchSize)

    // need to loop here

    val i = it.next()

    await { observer.next(DataArray(i.map(r =>
      DataRecord(r.toMap.asScala.map(c => DataString(c._1, c._2)).toList)).toList)) }

    await { observer.completed() }
  }
}
