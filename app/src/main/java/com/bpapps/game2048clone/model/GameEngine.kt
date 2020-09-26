package com.bpapps.game2048clone.model

import android.util.Log


class GameEngine(
    private var bestScore: Int,
    private var bestScoreUpdatedCallback: IOnBestScoreUpdated?,
    private var scoreUpdatedCallback: IOnGameScoreUpdates?,
//    private var addedRandomSquareCallback: IOnRandomSquareAdded?,
    private var squareMovedListener: IOnSquareMovedListener?
) {
    private val dimens = Configurations.BOARD_DIMENSIONS

    private lateinit var squares: Array<Array<Int>>

    init {
        startNewGame()
    }

    val boardStatus: Array<Array<Int>>
        get() = squares

    private var score: Int = 0
    var isGameFinished = false

    private fun addRandom() {
        if (!isGameFinished) {
            val emptySquaresCoordinates: ArrayList<Coordinate> = arrayListOf()

            squares.forEachIndexed { row, lines ->
                lines.forEachIndexed { col, num ->
                    if (num == EMPTY_SQUARE) {
                        emptySquaresCoordinates.add(Coordinate(row, col))
                    }
                }
            }

            if (emptySquaresCoordinates.size > 0) {
                val value = arrayListOf(2, 4).random()
                val coordinate = emptySquaresCoordinates.random()
                squares[coordinate.row][coordinate.col] = value
//                addedRandomSquareCallback?.onSquareAdded(coordinate, value)
            }

//            val value = if (Random.nextBoolean()) 2 else 4
//            var ind = 0
//            if (emptySquaresCoordinates.size > 1) {
//                ind = Random.nextInt(0, emptySquaresCoordinates.size - 1)
//            }
//
//            if (emptySquaresCoordinates.size > 0) {
//                squares[emptySquaresCoordinates[ind].row][emptySquaresCoordinates[ind].col] = value
//                addedRandomSquareCallback?.onSquareAdded(emptySquaresCoordinates[ind], value)
//            }
        }
    }

    fun startNewGame() {
        squares = Array(dimens) {
            Array(dimens) {
                EMPTY_SQUARE
            }
        }

//        squares = arrayOf(
//            arrayOf(2, 4, 8, 16),
//            arrayOf(4, 8, 16, 32),
//            arrayOf(8, 16, 32, 64),
//            arrayOf(16, 32, 64, 1)
//        )

        addRandom()
        addRandom()

        scoreUpdatedCallback?.onScoreUpdated(0)
        bestScoreUpdatedCallback?.onBestScoreUpdated(bestScore)

        logBoard()
    }

    fun swipeUp() {
        if (!isGameFinished) {
            var moved = false
            var row = 0
            while (row < dimens - 1) {
                var col = 0
                while (col < dimens) {
                    if (squares[row + 1][col] != EMPTY_SQUARE) {
                        if (squares[row][col] != EMPTY_SQUARE) {
                            //check if to merge
                            if (squares[row][col] == squares[row + 1][col]) {
                                //merge
                                squares[row][col] *= 2
                                squares[row + 1][col] = EMPTY_SQUARE

                                squareMovedListener?.onSquareMoved(
                                    SquareMovement(
                                        MoveDirection.UP,
                                        Coordinate(row + 1, col),
                                        Coordinate(row, col),
                                        squares[row][col],
                                        true
                                    )
                                )

                                updateScore(squares[row][col])

                                //check prev row
                                moved = true
                            }
                        } else {
                            //merge
                            squares[row][col] = squares[row + 1][col]
                            squares[row + 1][col] = EMPTY_SQUARE

                            //check prev row
                            moved = true

                            squareMovedListener?.onSquareMoved(
                                SquareMovement(
                                    MoveDirection.UP,
                                    Coordinate(row + 1, col),
                                    Coordinate(row, col),
                                    squares[row][col],
                                    false
                                )
                            )
                        }
                    }

                    col++
                }
                //update row value
                if (moved) {
                    if (row == 0) {
                        row++
                    } else {
                        row--
                    }
                } else {
                    row++
                }

                moved = false
            }

            logBoard()
            addRandom()
            logBoard()

            checkIfGameFinished()

        }
    }

    fun swipeDown() {
        if (!isGameFinished) {
            var moved = false
            var row = dimens - 1
            while (row > 0) {
                var col = 0
                while (col < dimens) {
                    if (squares[row - 1][col] != EMPTY_SQUARE) {
                        if (squares[row][col] != EMPTY_SQUARE) {
                            //check if to merge
                            if (squares[row][col] == squares[row - 1][col]) {
                                //merge
                                squares[row][col] *= 2
                                squares[row - 1][col] = EMPTY_SQUARE

                                updateScore(squares[row][col])

                                //check prev row
                                moved = true

                                squareMovedListener?.onSquareMoved(
                                    SquareMovement(
                                        MoveDirection.DOWN,
                                        Coordinate(row - 1, col),
                                        Coordinate(row, col),
                                        squares[row][col],
                                        true
                                    )
                                )
                            }
                        } else {
                            //move
                            squares[row][col] = squares[row - 1][col]
                            squares[row - 1][col] = EMPTY_SQUARE

                            //check prev row
                            moved = true

                            squareMovedListener?.onSquareMoved(
                                SquareMovement(
                                    MoveDirection.DOWN,
                                    Coordinate(row - 1, col),
                                    Coordinate(row, col),
                                    squares[row][col],
                                    false
                                )
                            )
                        }
                    }

                    col++
                }
                //update row value
                if (moved) {
                    if (row == dimens - 1) {
                        row--
                    } else {
                        row++
                    }
                } else {
                    row--
                }

                moved = false
            }

            logBoard()
            addRandom()
            logBoard()

            checkIfGameFinished()
        }
    }

    fun swipeLeft() {

        if (!isGameFinished) {
            var moved = false

            var row = 0
            while (row < dimens) {
                var col = 0
                while (col < dimens - 1) {
                    if (squares[row][col + 1] != EMPTY_SQUARE) {
                        if (squares[row][col] != EMPTY_SQUARE) {
                            //check if to merge
                            if (squares[row][col] == squares[row][col + 1]) {
                                //merge
                                squares[row][col] *= 2
                                squares[row][col + 1] = EMPTY_SQUARE

                                updateScore(squares[row][col])

                                //check prev row
                                moved = true

                                squareMovedListener?.onSquareMoved(
                                    SquareMovement(
                                        MoveDirection.DOWN,
                                        Coordinate(row, col + 1),
                                        Coordinate(row, col),
                                        squares[row][col],
                                        true
                                    )
                                )
                            }
                        } else {
                            //move
                            squares[row][col] = squares[row][col + 1]
                            squares[row][col + 1] = EMPTY_SQUARE

                            //check prev row
                            moved = true

                            squareMovedListener?.onSquareMoved(
                                SquareMovement(
                                    MoveDirection.DOWN,
                                    Coordinate(row, col + 1),
                                    Coordinate(row, col),
                                    squares[row][col],
                                    false
                                )
                            )
                        }
                    }

                    //update col value
                    if (moved) {
                        if (col == 0) {
                            col++
                        } else {
                            col--
                        }
                    } else {
                        col++
                    }

                    moved = false
                }

                row++
            }


            logBoard()
            addRandom()
            logBoard()

            checkIfGameFinished()
        }
    }


    fun swipeRight() {
        if (!isGameFinished) {
            var moved = false
            var col = dimens - 1
            while (col > 0) {
                var row = 0
                while (row < dimens) {
                    if (squares[row][col - 1] != EMPTY_SQUARE) {
                        if (squares[row][col] != EMPTY_SQUARE) {
                            //check if to merge
                            if (squares[row][col] == squares[row][col - 1]) {
                                //merge
                                squares[row][col] *= 2
                                squares[row][col - 1] = EMPTY_SQUARE

                                updateScore(squares[row][col])

                                //check prev row
                                moved = true

                                squareMovedListener?.onSquareMoved(
                                    SquareMovement(
                                        MoveDirection.UP,
                                        Coordinate(row, col - 1),
                                        Coordinate(row, col),
                                        squares[row][col],
                                        true
                                    )
                                )
                            }
                        } else {
                            //move
                            squares[row][col] = squares[row][col - 1]
                            squares[row][col - 1] = EMPTY_SQUARE

//                            //check prev row
                            moved = true

                            squareMovedListener?.onSquareMoved(
                                SquareMovement(
                                    MoveDirection.UP,
                                    Coordinate(row, col - 1),
                                    Coordinate(row, col),
                                    squares[row][col],
                                    false
                                )
                            )
                        }
                    }
                    row++
                }
                //update col value
                if (moved) {
                    if (col == dimens - 1) {
                        col--
                    } else {
                        col++
                    }
                } else {
                    col--
                }

                moved = false
            }

            logBoard()
            addRandom()
            logBoard()

            checkIfGameFinished()
        }
    }

    private fun checkIfGameFinished(): Boolean {
        if (!isGameFinished) {
            isGameFinished = true

            for (i in 0 until dimens) {
                for (j in 0 until dimens) {
                    if (squares[i][j] == END_GAME_VALUE) {
                        isGameFinished = true
                        return isGameFinished
                    }

                    if (squares[i][j] == EMPTY_SQUARE) {
                        isGameFinished = false
                        return isGameFinished
                    }

                    if (i > 0 && squares[i][j] == squares[i - 1][j]) {
                        isGameFinished = false
                        return isGameFinished
                    }
                    if (i < dimens - 1 && squares[i][j] == squares[i + 1][j]) {
                        isGameFinished = false
                        return isGameFinished
                    }

                    if (j > 0 && squares[i][j] == squares[i][j - 1]) {
                        isGameFinished = false
                        return isGameFinished
                    }
                    if (j < dimens - 1 && squares[i][j] == squares[i][j + 1]) {
                        isGameFinished = false
                        return isGameFinished
                    }
                }
            }
        }

        Log.d(TAG, "is game finished = $isGameFinished")

        return isGameFinished
    }

    private fun updateScore(value: Int) {
        score += value
        scoreUpdatedCallback?.onScoreUpdated(score)
        updateBestScore()
    }

    private fun updateBestScore() {
        if (score > bestScore) {
            bestScore = score
            bestScoreUpdatedCallback?.onBestScoreUpdated(bestScore)
        }
    }

    companion object {
        private const val TAG = "TAG.GameEngine"
        const val EMPTY_SQUARE = 1
        const val END_GAME_VALUE = 2048
    }

    interface IOnGameScoreUpdates {
        fun onScoreUpdated(newScore: Int)
    }
//
//    interface IOnRandomSquareAdded {
//        fun onSquareAdded(coordinate: Coordinate, value: Int)
//    }

    interface IOnBestScoreUpdated {
        fun onBestScoreUpdated(newBestScore: Int)
    }

    interface IOnSquareMovedListener {
        fun onSquareMoved(move: SquareMovement)
    }


    private fun logBoard() {
        val retVal = StringBuilder()
        squares.forEachIndexed { row, line ->
            line.forEachIndexed { col, num ->
                retVal.append("$num ")
            }
            retVal.append('\n')
            Log.d(TAG, retVal.toString())
            retVal.clear()
        }

        Log.d(TAG, "==================")
    }
}