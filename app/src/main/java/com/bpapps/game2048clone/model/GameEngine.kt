package com.bpapps.game2048clone.model

import android.util.Log


class GameEngine(
    private var moveFinishedCallBack: IOnMoveFinished,
    private var bestScoreUpdatedCallback: IOnBestScoreUpdated?,
    private var scoreUpdatedCallback: IOnGameScoreUpdates?
) {
    private val dimens = Configurations.BOARD_DIMENSIONS

    val board: Array<Array<Square>>

    init {
//        debug
//        board =
//            arrayOf(
//                arrayOf(Square(2), Square(4), Square(8), Square(16)),
//                arrayOf(Square(16), Square(8), Square(1024), Square(2)),
//                arrayOf(Square(8), Square(16), Square(32), Square(64)),
//                arrayOf(Square(1), Square(1), Square(1), Square(128))
//            )

        board =
            Array(dimens) {
                Array(dimens) {
                    Square(EMPTY_SQUARE)
                }
            }
        addRandom()
        addRandom()

        logBoard()
    }

    private var score: Int = 0
        set(value) {
            field = value
            scoreUpdatedCallback?.onScoreUpdated(score)
            bestScore = field
        }

    var bestScore: Int = 0
        set(value) {
            if (value > field) {
                field = value
                bestScoreUpdatedCallback?.onBestScoreUpdated(bestScore)
            }
        }

    var isVictories: Boolean = false

    var isGameFinished = false

    private fun addRandom(): Coordinate? {
        if (!isGameFinished) {
            val emptySquaresCoordinates: ArrayList<Coordinate> = arrayListOf()

            board.forEachIndexed { row, lines ->
                lines.forEachIndexed { col, square ->
                    if (square.num == EMPTY_SQUARE) {
                        emptySquaresCoordinates.add(Coordinate(row, col))
                    }
                }
            }

            if (emptySquaresCoordinates.size > 0) {
                val value = arrayListOf(2, 4).random()
                val coordinate = emptySquaresCoordinates.random()
                board[coordinate.row][coordinate.col].num = value

                return coordinate
            }
        }

        return null
    }

    fun startNewGame() {
        isGameFinished = false
        board.forEach { line ->
            line.forEach { square ->
                square.num = EMPTY_SQUARE
            }
        }

        addRandom()
        addRandom()

        logBoard()

        score = 0
        bestScore = 0
    }

    fun swipeUp() {
        if (!isGameFinished) {
            val moves = arrayListOf<SquareMovement>()
            var scoreUpdateValue = 0

            var col = 0
            while (col < dimens) {

                var row = 1
                while (row < dimens) {
                    if (board[row][col].isNotEmpty()) {

                        var row1 = row - 1
                        loop@ while (row1 >= 0) {
                            if (board[row1][col].isNotEmpty()) {
                                break@loop
                            } else {
                                row1--
                            }
                        }

                        val toCoordinate = Coordinate(-1, col)
                        var merged = false
                        var valueAtTheEnd = board[row][col].num

                        if (row1 < 0) {
                            //move to (0,col)
                            moveSquare(board[row][col], board[0][col])
                            toCoordinate.row = 0
                        } else {
                            if (board[row][col] == board[row1][col]) {
                                //merge to (row1, col)
                                mergeSquares(board[row][col], board[row1][col])
                                toCoordinate.row = row1
                                merged = true
                                valueAtTheEnd *= 2
                                scoreUpdateValue += valueAtTheEnd
                            } else {
                                //move to (row1 + 1, col)
                                if (row != row1 + 1) {
                                    moveSquare(board[row][col], board[row1 + 1][col])
                                    toCoordinate.row = row1 + 1
                                }
                            }
                        }

                        if (row != row1 + 1 || merged) {
                            moves.add(
                                SquareMovement(
                                    MoveDirection.UP,
                                    Coordinate(row, col),
                                    toCoordinate,
                                    valueAtTheEnd,
                                    merged
                                )
                            )
                        }
                    }

                    row++
                }

                col++
            }

            val addedSquareCoordinate = addRandom()
            checkIfGameFinished()

            val retVal = MoveFinishedDataHolder(moves, isGameFinished)
            addedSquareCoordinate?.let { coordinate ->
                retVal.addedSquareCoordinate = coordinate
                retVal.addSquareValue =
                    board[coordinate.row][coordinate.col].num
            }

            logChanges(retVal)
            moveFinishedCallBack?.onMoveFinished(retVal)

            if (scoreUpdateValue > 0) {
                score += scoreUpdateValue
            }
        }
    }

    fun swipeDown() {
        if (!isGameFinished) {
            val moves = arrayListOf<SquareMovement>()
            var scoreUpdateValue = 0

            var col = 0
            while (col < dimens) {

                var row = dimens - 2
                while (row >= 0) {
                    if (board[row][col].isNotEmpty()) {

                        var row1 = row + 1
                        loop@ while (row1 <= dimens - 1) {
                            if (board[row1][col].isNotEmpty()) {
                                break@loop
                            } else {
                                row1++
                            }
                        }

                        val toCoordinate = Coordinate(-1, col)
                        var merged = false
                        var valueAtTheEnd = board[row][col].num

                        if (row1 > dimens - 1) {
                            //move to (dimens - 1 ,col)
                            moveSquare(board[row][col], board[dimens - 1][col])
                            toCoordinate.row = dimens - 1
                        } else {
                            if (board[row][col] == board[row1][col]) {
                                //merge to (row1, col)
                                mergeSquares(board[row][col], board[row1][col])
                                toCoordinate.row = row1
                                merged = true
                                valueAtTheEnd *= 2
                                scoreUpdateValue += valueAtTheEnd
                            } else {
                                //move to (row1 - 1, col)
                                if (row != row1 - 1) {
                                    moveSquare(board[row][col], board[row1 - 1][col])
                                    toCoordinate.row = row1 - 1
                                }
                            }
                        }

                        if (row != row1 - 1 || merged) {
                            moves.add(
                                SquareMovement(
                                    MoveDirection.DOWN,
                                    Coordinate(row, col),
                                    toCoordinate,
                                    valueAtTheEnd,
                                    merged
                                )
                            )
                        }
                    }

                    row--
                }

                col++
            }

            val addedSquareCoordinate = addRandom()
            checkIfGameFinished()

            val retVal = MoveFinishedDataHolder(moves, isGameFinished)
            addedSquareCoordinate?.let { coordinate ->
                retVal.addedSquareCoordinate = coordinate
                retVal.addSquareValue =
                    board[coordinate.row][coordinate.col].num
            }

            logChanges(retVal)
            moveFinishedCallBack?.onMoveFinished(retVal)


            if (scoreUpdateValue > 0) {
                score += scoreUpdateValue
            }
        }
    }

    fun swipeLeft() {
        if (!isGameFinished) {
            val moves = arrayListOf<SquareMovement>()
            var scoreUpdateValue = 0

            var row = 0
            while (row < dimens) {

                var col = 1
                while (col < dimens) {
                    if (board[row][col].isNotEmpty()) {

                        var col1 = col - 1
                        loop@ while (col1 >= 0) {
                            if (board[row][col1].isNotEmpty()) {
                                break@loop
                            } else {
                                col1--
                            }
                        }

                        val toCoordinate = Coordinate(row, -1)
                        var merged = false
                        var valueAtTheEnd = board[row][col].num

                        if (col1 < 0) {
                            //move to (row, 0)
                            moveSquare(board[row][col], board[row][0])
                            toCoordinate.col = 0
                        } else {
                            if (board[row][col] == board[row][col1]) {
                                //merge to (row, col1)
                                mergeSquares(board[row][col], board[row][col1])
                                toCoordinate.col = col1
                                merged = true
                                valueAtTheEnd *= 2
                                scoreUpdateValue += valueAtTheEnd
                            } else {
                                //move to (row, col1 + 1) if not neighbors
                                if (col != col1 + 1) {
                                    moveSquare(board[row][col], board[row][col1 + 1])
                                    toCoordinate.col = col1 + 1
                                }

                            }
                        }

                        if (col != col1 + 1 || merged) {
                            moves.add(
                                SquareMovement(
                                    MoveDirection.LEFT,
                                    Coordinate(row, col),
                                    toCoordinate,
                                    valueAtTheEnd,
                                    merged
                                )
                            )
                        }
                    }

                    col++
                }

                row++
            }

            val addedSquareCoordinate = addRandom()
            checkIfGameFinished()

            val retVal = MoveFinishedDataHolder(moves, isGameFinished)
            addedSquareCoordinate?.let { coordinate ->
                retVal.addedSquareCoordinate = coordinate
                retVal.addSquareValue =
                    board[coordinate.row][coordinate.col].num
            }

            logChanges(retVal)
            moveFinishedCallBack?.onMoveFinished(retVal)

            if (scoreUpdateValue > 0) {
                score += scoreUpdateValue
            }
        }
    }

    fun swipeRight() {
        if (!isGameFinished) {
            val moves = arrayListOf<SquareMovement>()
            var scoreUpdateValue = 0

            var row = 0
            while (row < dimens) {

                var col = dimens - 2
                while (col >= 0) {
                    if (board[row][col].isNotEmpty()) {

                        var col1 = col + 1
                        loop@ while (col1 < dimens) {
                            if (board[row][col1].isNotEmpty()) {
                                break@loop
                            } else {
                                col1++
                            }
                        }

                        val toCoordinate = Coordinate(row, -1)
                        var merged = false
                        var valueAtTheEnd = board[row][col].num

                        if (col1 > dimens - 1) {
                            //move to (row, 0)
                            moveSquare(board[row][col], board[row][dimens - 1])
                            toCoordinate.col = dimens - 1
                        } else {
                            if (board[row][col] == board[row][col1]) {
                                //merge to (row, col1)
                                mergeSquares(board[row][col], board[row][col1])
                                toCoordinate.col = col1
                                merged = true
                                valueAtTheEnd *= 2
                                scoreUpdateValue += valueAtTheEnd
                            } else {
                                //move to (row, col1 + 1) if not neighbors
                                if (col != col1 - 1) {
                                    moveSquare(board[row][col], board[row][col1 - 1])
                                    toCoordinate.col = col1 - 1
                                }

                            }
                        }

                        if (col != col1 - 1 || merged) {
                            moves.add(
                                SquareMovement(
                                    MoveDirection.RIGHT,
                                    Coordinate(row, col),
                                    toCoordinate,
                                    valueAtTheEnd,
                                    merged
                                )
                            )
                        }
                    }

                    col--
                }

                row++
            }

            val addedSquareCoordinate = addRandom()
            checkIfGameFinished()

            val retVal = MoveFinishedDataHolder(moves, isGameFinished)
            addedSquareCoordinate?.let { coordinate ->
                retVal.addedSquareCoordinate = coordinate
                retVal.addSquareValue =
                    board[coordinate.row][coordinate.col].num
            }

            logChanges(retVal)
            moveFinishedCallBack?.onMoveFinished(retVal)

            if (scoreUpdateValue > 0) {
                score += scoreUpdateValue
            }
        }
    }

    private fun mergeSquares(from: Square, to: Square) {
        to.num *= 2
        from.num = EMPTY_SQUARE
    }

    private fun moveSquare(from: Square, to: Square) {
        to.num = from.num
        from.num = EMPTY_SQUARE
    }


    private fun checkIfGameFinished(): Boolean {
        if (!isGameFinished) {
            isGameFinished = true

            for (i in 0 until dimens) {
                for (j in 0 until dimens) {
                    if (board[i][j].num == VICTORY_GAME_VALUE) {
                        isGameFinished = true
                        isVictories = true
                        return isGameFinished
                    }

                    if (board[i][j].num == EMPTY_SQUARE) {
                        isGameFinished = false
                        return isGameFinished
                    }

                    if (i > 0 && board[i][j] == board[i - 1][j]) {
                        isGameFinished = false
                        return isGameFinished
                    }
                    if (i < dimens - 1 && board[i][j] == board[i + 1][j]) {
                        isGameFinished = false
                        return isGameFinished
                    }

                    if (j > 0 && board[i][j] == board[i][j - 1]) {
                        isGameFinished = false
                        return isGameFinished
                    }
                    if (j < dimens - 1 && board[i][j] == board[i][j + 1]) {
                        isGameFinished = false
                        return isGameFinished
                    }
                }
            }
        }

        return isGameFinished
    }

//    private fun updateScore(value: Int) {
//        score += value
//        scoreUpdatedCallback?.onScoreUpdated(score)
//        updateBestScore()
//    }
//
//    private fun updateBestScore() {
//        if (score > bestScore) {
//            bestScore = score
//            bestScoreUpdatedCallback?.onBestScoreUpdated(bestScore)
//        }
//    }

    companion object {
        private const val TAG = "TAG.GameEngine"
        const val EMPTY_SQUARE = 1
        const val VICTORY_GAME_VALUE = 2048
    }

    interface IOnMoveFinished {
        fun onMoveFinished(data: MoveFinishedDataHolder)
    }

    interface IOnGameScoreUpdates {
        fun onScoreUpdated(newScore: Int)
    }

    interface IOnBestScoreUpdated {
        fun onBestScoreUpdated(newBestScore: Int)
    }

    fun logBoard() {
        val retVal = StringBuilder()
        board.forEachIndexed { _, line ->
            line.forEachIndexed { _, square ->
                retVal.append("${square} ")
            }
            retVal.append('\n')
            Log.d(TAG, retVal.toString())
            retVal.clear()
        }

        Log.d(TAG, "==================")
    }

    private fun logChanges(retVal: MoveFinishedDataHolder) {
        Log.d(TAG, retVal.toString())
        retVal.moves.forEach { move ->
            Log.d(TAG, move.toString())
        }
    }


    data class Square(var num: Int) {

        override fun toString(): String {
            return num.toString()
        }

        fun isNotEmpty(): Boolean {
            return num != EMPTY_SQUARE
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Square

            if (num != other.num) return false

            return true
        }

        override fun hashCode(): Int {
            return num
        }
    }

}