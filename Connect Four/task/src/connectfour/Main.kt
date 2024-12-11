package connectfour

fun main() {
    println("Connect Four")
    println("First player's name:")
    val firstPlayerName = readln()
    println("Second player's name:")
    val secondPlayerName = readln()
    var rows = 6
    var columns = 7
    var isInputRight = false
    while (!isInputRight) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val input = readln().replace("\\s".toRegex(), "")
        if (input.isNotEmpty()) {
            val regex = Regex("..?x..?", setOf(RegexOption.IGNORE_CASE))
            if (regex.matches(input)) {
                try {
                    rows = input.first().toString().toInt()
                    columns = input.last().toString().toInt()
                    check(rows in 5..9){"Board rows should be from 5 to 9"}
                    check(columns in 5..9){"Board columns should be from 5 to 9"}
                    isInputRight = true
                } catch (e: NumberFormatException) {
                    println("Invalid input")
                } catch (e: IllegalStateException) {
                    println(e.message)
                }
            } else println("Invalid input")
        } else isInputRight = true
    }
    println("$firstPlayerName VS $secondPlayerName")
    println("$rows X $columns board")
}