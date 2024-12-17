package connectfour

fun main() {
    //Название игры
    println("Connect Four")

    //Имена игроков
    println("First player's name:")
    val firstPlayerName = readln()
    println("Second player's name:")
    val secondPlayerName = readln()

    //Количество строк и столбцов игрового поля по умолчанию
    var rows = 6
    var columns = 7
    //Для проверки правильности ввода размера поля
    var isInputRight = false

    //Цикл выполняется пока пользователь не введет корректный размер поля
    while (!isInputRight) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        //Убираем все пробелы из ввода
        val input = readln().replace("\\s".toRegex(), "")
        //Если ввод не пустой
        if (input.isNotEmpty()) {
            //Регул выражение корректного ввода размера игрового поля
            val regex = Regex("..?x..?", setOf(RegexOption.IGNORE_CASE))
            //Если ввод соответствует регул выражению
            if (regex.matches(input)) {
                //Проверяем что там числа и они в диапазоне 5-9
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
                //Если ввод не соответствует рег выражению
            } else println("Invalid input")
            //Если пользователь сразу нажал Enter, то используются значения по умолчанию
        } else isInputRight = true
    }

    //Число игр: 1 - режим синглплей, > 1 - режим мультиплей
    var numberOfGames = 1
    //Для проверки правильности ввода числа игр
    isInputRight = false
    //Пока пользователь не введет корректное число игр, повторять
    while (!isInputRight) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        //Ввод пользователя
        val input = readln()
        //Если ввод не пустой
        if (input.isNotEmpty()) {
            //Проверяем, что там положительные числа
            try {
                checkNotNull(input.toIntOrNull()){"Invalid input"}
                check(input.toInt() >= 1){"Invalid input"}
                //Если введена 1 то это синглплей по умолчанию
                if (input.toInt() == 1) isInputRight = true
                //Если ввод >1, то это мультиплей с указанным числом игр
                else {
                    numberOfGames = input.toInt()
                    isInputRight = true
                }
            } catch (e: IllegalStateException) {
                println(e.message)
            }
        } else {
            //Если пользователь нажал Enter, то считать по умолчанию режим синглплей
            isInputRight = true
        }
    }
    //Первоначальный вывод перед стартом игры
    println("$firstPlayerName VS $secondPlayerName")
    println("$rows X $columns board")

    //Режим мультиплей
    if (numberOfGames > 1) {
        //Переменные для режима мультиплей
        var currentGame = 1 //номер текущей игры в сэте
        var scorePlayer1 = 0    //очки 1-ого игрока
        var scorePlayer2 = 0    //очки 2-ого игрока

        println("Total $numberOfGames games")

        //Цикл сэта игр
        while (currentGame <= numberOfGames) {
            // Игровое поле
            val field : MutableList<MutableList<Char>> = MutableList(columns){ MutableList(rows){' '} }
            //Для цикла игры (раунда)
            var gameState = true
            //Поочередно меняется очередь первого хода
            var isFirstPlayerTurn = currentGame % 2 != 0
            //Для слежения за заполненностью поля и условия ничьи
            val fullColumnIndexList = mutableListOf<Int>()

            //Вывод пустого поля перед стартом раунда
            println("Game #$currentGame")
            printField(rows, columns, field)
            //Цикл раунда игры
            while (gameState) {
                //Печатаем чей ход
                if (isFirstPlayerTurn) println("$firstPlayerName's turn:")
                else println("$secondPlayerName's turn:")
                //Ввод от игрока номера столбца поля
                val input = readln()
                when  {
                    //Если ввод число
                    (input).toIntOrNull() != null -> {
                        //Номер столбца в двумерном листе поля
                        val columnNum = input.toInt() - 1
                        //Проверяем введенный диапазон столбца
                        try {
                            check(columnNum in 0 until columns)
                            {"The column number is out of range (1 - $columns)"}
                        } catch (e: IllegalStateException) {
                            println(e.message)
                            continue
                        }
                        //Индекс последней свободной ячейки в данном столбце поля
                        val lastFreeCellIndex = field[columnNum].lastIndexOf(' ')
                        //Если свободная ячейка есть
                        if (lastFreeCellIndex != -1) {
                            //при ходе 1-ого игрока заполняем ее 'o'
                            if (isFirstPlayerTurn) {
                                field[columnNum][lastFreeCellIndex] = 'o'
                            }
                            //при ходе 2-ого игрока заполняем ее '*'
                            else {
                                field[columnNum][lastFreeCellIndex] = '*'
                            }
                            //Печатаем обновленное поле
                            printField(rows, columns, field)
                            //Проверка условия выигрыша одним из игроков
                            if (checkWinCondition(columnNum, lastFreeCellIndex, field, isFirstPlayerTurn)){
                                //Завершение цикла раунда
                                gameState = false
                                //Меняем значение текущей игры в сэте
                                currentGame++
                                //Вывод выигрыша и зачисление победных очков
                                if (isFirstPlayerTurn) {
                                    println("Player $firstPlayerName won")
                                    scorePlayer1 += 2
                                }
                                else {
                                    println("Player $secondPlayerName won")
                                    scorePlayer2 += 2
                                }
                                //Вывод счета
                                printScore(firstPlayerName, secondPlayerName, scorePlayer1, scorePlayer2)
                            }
                            //Если это последняя ячейка в столбце добавляем его индекс в лист заполненных столбцов
                            if (lastFreeCellIndex == 0) fullColumnIndexList.add(columnNum)
                            //Если все ячейки заполнены, то это ничья
                            if (fullColumnIndexList.size == field.size) {
                                //Завершение цикла раунда
                                gameState = false
                                //Меняем значение текущей игры в сэте
                                currentGame++
                                //Зачисление очков ничьи
                                scorePlayer1++
                                scorePlayer2++
                                //Вывод счета
                                println("It is a draw")
                                printScore(firstPlayerName, secondPlayerName, scorePlayer1, scorePlayer2)
                            }
                            //Переход хода в раунде
                            isFirstPlayerTurn = !isFirstPlayerTurn
                        } else {
                            //Если введен уже заполненный столбец
                            println("Column ${columnNum + 1} is full")
                        }
                    }
                    //Если игрок ввел end завершаем игру
                    input == "end" -> {
                        //Завершение цикла раунда
                        gameState = false
                        //Завершение цикла сэта
                        currentGame = numberOfGames + 1
                    }
                    //Если ввод не число
                    else -> println("Incorrect column number")
                }   //завершение цикла ввода от игрока
            }   //завершение цикла раунда игры
        }   //завершение цикла сэта игр
    } else { //завершение режима мультиплей
        //Режим синглплей

        //Блок переменных состояний, из названий понятно для чего
        val field : MutableList<MutableList<Char>> = MutableList(columns){ MutableList(rows){' '} }
        var gameState = true
        var isFirstPlayerTurn = true
        val fullColumnIndexList = mutableListOf<Int>()

        println("Single game")
        //Печатаем пустое игровое поле
        printField(rows, columns, field)

        //Цикл игры
        while (gameState) {
            //Печатаем чей ход
            if (isFirstPlayerTurn) println("$firstPlayerName's turn:")
            else println("$secondPlayerName's turn:")
            //Ввод от игрока номера столбца поля
            val input = readln()
            when  {
                //Если ввод число
                (input).toIntOrNull() != null -> {
                    //Номер столбца в двумерном листе поля
                    val columnNum = input.toInt() - 1
                    //Проверяем введенный диапазон столбца
                    try {
                        check(columnNum in 0 until columns)
                        {"The column number is out of range (1 - $columns)"}
                    } catch (e: IllegalStateException) {
                        println(e.message)
                        continue
                    }
                    //Индекс последней свободной ячейки в данном столбце поля
                    val lastFreeCellIndex = field[columnNum].lastIndexOf(' ')
                    //Если свободная ячейка есть
                    if (lastFreeCellIndex != -1) {
                        //при ходе 1-ого игрока заполняем ее 'o'
                        if (isFirstPlayerTurn) {
                            field[columnNum][lastFreeCellIndex] = 'o'
                        }
                        //при ходе 2-ого игрока заполняем ее '*'
                        else {
                            field[columnNum][lastFreeCellIndex] = '*'
                        }
                        //Печатаем обновленное поле
                        printField(rows, columns, field)
                        //Проверка условия выигрыша одним из игроков
                        if (checkWinCondition(columnNum, lastFreeCellIndex, field, isFirstPlayerTurn)){
                            gameState = false
                            if (isFirstPlayerTurn) println("Player $firstPlayerName won")
                            else println("Player $secondPlayerName won")
                        }
                        //Если это последняя ячейка в столбце добавляем его индекс в лист заполненных столбцов
                        if (lastFreeCellIndex == 0) fullColumnIndexList.add(columnNum)
                        //Если все ячейки заполнены, то это ничья
                        if (fullColumnIndexList.size == field.size) {
                            gameState = false
                            println("It is a draw")
                        }
                        //Переход хода
                        isFirstPlayerTurn = !isFirstPlayerTurn
                    } else {
                        //Если введен уже заполненный столбец
                        println("Column ${columnNum + 1} is full")
                    }
                }
                //Если игрок ввел end завершаем игру
                input == "end" -> gameState = false
                //Если ввод не число
                else -> println("Incorrect column number")
            }
        }
    }
    println("Game over!")
}

//Функция отображения игрового поля
fun printField(rows: Int, columns: Int, field: MutableList<MutableList<Char>>) {
    //Первая строка с нумерацией столбцов поля
    repeat(columns) { print(" ${it + 1}") }
    println()
    //Основная часть поля
    for (i in 0 until rows) {
        print("║")
        for (j in 0 until columns) {
            print("${field[j][i]}║")
        }
        println()
    }
    //Последняя строка поля
    print("╚")
    repeat(columns - 1) { print("═╩") }
    println("═╝")
}

//Функция проверки условия выйгрыша
fun checkWinCondition(
    colCell: Int,   //индекс текущего столбца
    rowCell: Int,   //индекс текущей строки
    field: MutableList<MutableList<Char>>,
    isFirstPlayerTurn: Boolean
): Boolean {
    //Строка-столбец для данной ячейки поля
    val column = field[colCell].joinToString("")
    //Строка-строка для данной ячейки поля
    val row = List(field.size) { index -> field[index][rowCell] }.joinToString("")
    //1-ая диагональ для данной ячейки
    val diagonal1 = getDiagonal1(colCell, rowCell, field)
    //2-ая диагональ для данной ячейки
    val diagonal2 = getDiagonal2(colCell, rowCell, field)
    //Рег выражение для повтора символа 'o' или '*' 4 раза подряд
    val regex = if (isFirstPlayerTurn) {Regex("o{4}")} else {Regex("\\*{4}")}
    //Проверка соответствия столбца, строки и диагоналей данному рег выражению
    val isColumnOrRow =
        regex.containsMatchIn(column)
                || regex.containsMatchIn(row)
                || regex.containsMatchIn(diagonal1)
                || regex.containsMatchIn(diagonal2)
    return isColumnOrRow
}

//Получение ячеек 1-ой диагонали в виде String
fun getDiagonal1(colCell: Int, rowCell: Int, field: MutableList<MutableList<Char>>): String {
    val diagonal1 = mutableListOf<Char>()
    val columns = field.lastIndex
    val rows = field[colCell].lastIndex
    var c = colCell
    var r = rowCell
    while (c > 0 && r >0) {
        c--
        r--
    }
    while (c <= columns  && r <= rows) {
        diagonal1.add(field[c][r])
        c++
        r++
    }
    return diagonal1.joinToString("")
}

//Получение ячеек 2-ой диагонали в виде String
fun getDiagonal2(colCell: Int, rowCell: Int, field: MutableList<MutableList<Char>>): String {
    val diagonal2 = mutableListOf<Char>()
    val columns = field.lastIndex
    val rows = field[colCell].lastIndex
    var c = colCell
    var r = rowCell
    while (c > 0 && r < rows) {
        c--
        r++
    }
    while (c <= columns  && r >= 0) {
        diagonal2.add(field[c][r])
        c++
        r--
    }
    return diagonal2.joinToString("")
}

//Функция печати текущего счета
fun printScore(
    firstPlayerName: String,
    secondPlayerName: String,
    scorePlayer1: Int,
    scorePlayer2: Int
) {
    println("Score")
    println("$firstPlayerName: $scorePlayer1 $secondPlayerName: $scorePlayer2")
}