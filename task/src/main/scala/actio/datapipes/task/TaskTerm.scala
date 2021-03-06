package actio.datapipes.task

import Term.TermExecutor
import actio.common.Data.{DataArray, DataNothing, DataSet}
import actio.common.Dom

import scala.meta._

class TaskTerm(name: String, config: DataSet, taskSetting: TaskSetting) extends TaskTransform(name) {

  val term: Term = config("term").toString.parse[Term].get
  val executor = new TermExecutor(taskSetting)

  def transform(dom: Dom): Seq[DataSet] = {
    val behavior = config("behavior").stringOption


    if (taskSetting.version.contains("v2")) {
      if(behavior.contains("batch"))
        List(executor.eval(dom.success, term))
      else if(behavior.contains("expand"))
        List(DataArray(dom.success.flatMap(r => executor.eval(r, term).elems).toList))
      else
        List(DataArray(dom.success.map(r => executor.eval(r, term)).toList))
    }
    else
      executor.eval(dom.success, term).elems
  }

}
