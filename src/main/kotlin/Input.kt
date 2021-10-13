import java.io.File

fun parse(args: Array<String>): File? {
    return when {
        args.isEmpty() ->
            null
        args.size > 1 -> {
            System.err.println("Too many arguments, so reading from standard input")
            null
        }
        !File(args.first()).isFile -> {
            System.err.println("Cannot open '${args.first()}' file, so reading from standard input")
            null
        }
        else ->
            File(args.first())
    }
}