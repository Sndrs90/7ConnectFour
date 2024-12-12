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

    val field : MutableList<MutableList<Char>> = MutableList(columns){ MutableList(rows){' '} }
    var gameState = true
    var isFirstPlayerTurn = true

    printField(rows, columns, field)

    while (gameState) {
        if (isFirstPlayerTurn) println("$firstPlayerName's turn:")
        else println("$secondPlayerName's turn:")
        val input = readln()
        when  {
            (input).toIntOrNull() != null -> {
                val columnNum = input.toInt() - 1
                try {
                    check(columnNum in 0 until columns)
                    {"The column number is out of range (1 - $columns)"}
                } catch (e: IllegalStateException) {
                    println(e.message)
                    continue
                }
                val lastFreeCellIndex = field[columnNum].lastIndexOf(' ')
                if (lastFreeCellIndex != -1) {
                    if (isFirstPlayerTurn) field[columnNum][lastFreeCellIndex] = 'o'
                    else field[columnNum][lastFreeCellIndex] = '*'
                    isFirstPlayerTurn = !isFirstPlayerTurn
                    printField(rows, columns, field)
                } else {
                    println("Column ${columnNum + 1} is full")
                }
            }
            input == "end" -> gameState = false
            else -> println("Incorrect column number")
        }
    }
    println("Game over!")
}

fun printField(rows: Int, columns: Int, field: MutableList<MutableList<Char>>) {
    repeat(columns) { print(" ${it + 1}") }
    println()
    for (i in 0 until rows) {
        print("║")
        for (j in 0 until columns) {
            print("${field[j][i]}║")
        }
        println()
    }
    print("╚")
    repeat(columns - 1) { print("═╩") }
    println("═╝")
}