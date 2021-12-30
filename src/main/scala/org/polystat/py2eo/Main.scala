import java.nio.file.{Path, Paths}

object Main {
    def main(args: Array[String]) = {
		    val path = Paths.get(args(0))
        println(args)
    }
}