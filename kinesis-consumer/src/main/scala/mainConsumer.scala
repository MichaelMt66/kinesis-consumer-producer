
import java.util.concurrent.Executors

object mainConsumer {

  def main(args: Array[String]): Unit = {

    val executor = Executors.newFixedThreadPool(1)
    executor.submit(new Consumer)

  }

}
