package actio.datapipes.pipeline

import actio.common.Data.{DataNothing, DataSet}
import actio.common.{Dom, Observable, Observer}
import actio.datapipes.pipescript.Pipeline.Operation
import actio.datapipes.task.Task
import com.typesafe.scalalogging.Logger

object SimpleExecutor {

  trait TaskOperation extends Observable[Dom] with Observer[Dom] {

    def start(): Unit = next(Dom())

    def start(ds: DataSet) =  next(Dom() ~ Dom("start", Nil, ds, DataNothing()))
  }

  val logger = Logger("SimpleExecutor")

  def getRunnable(operation: Operation): TaskOperation = operation match {

    case t: actio.datapipes.pipescript.Pipeline.Task => new TaskOperation {

      val myTask = Task(t.name, t.taskType, t.config)

      def next(value: Dom): Unit = {
        val size = value.headOption.map(_.success.elems.size).getOrElse(0)

        logger.debug(s"=== Task ${t.name} received Dom with last successful DataSet of size ($size) ===")
        myTask.next(value)
      }

      def completed(): Unit = { logger.info(s"=== Operation ${operation.name} completed ==="); myTask.completed() }

      def error(exception: Throwable): Unit = myTask.error(exception)

      def subscribe(observer: Observer[Dom]): Unit = myTask.subscribe(observer)
    }

    case p: actio.datapipes.pipescript.Pipeline.Pipe => new TaskOperation {

      val l = getRunnable(p.left)
      val r = getRunnable(p.right)

      l.subscribe(r)

      def next(value: Dom): Unit = {
        l.next(value)
      }

      def completed(): Unit = {
        logger.info(s"=== Operation ${operation.name} completed ===")
        l.completed()
        r.completed()
      }

      def error(exception: Throwable): Unit = {
        l.error(exception)
        r.error(exception)
      }

      def subscribe(observer: Observer[Dom]): Unit = r.subscribe(observer)
    }
  }

  def getService(operation: Operation, observer: Observer[Dom]): TaskOperation = {
    val runnable = getRunnable(operation)

    runnable.subscribe(observer)
    runnable
  }
}