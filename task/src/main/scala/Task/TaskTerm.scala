package Task


import DataPipes.Common._
import DataPipes.Common.Data._
import Term.TermExecutor
import scala.meta._

import scala.meta.Term

class TaskTerm(name: String, config: DataSet, version: String) extends TaskTransform(name) {

  val term: Term = config("term").stringOption.getOrElse("").parse[Term].get
  val executor = new TermExecutor(config("namespace").stringOption.getOrElse("Term.Legacy.Functions"))

  def transform(dom: Dom): Seq[DataSet] = {
    val ds = executor.eval(dom.headOption.map(_.success).getOrElse(DataNothing()), term)

    if(version.contains("v2"))
      List(ds)
    else
      ds.elems
  }

}
