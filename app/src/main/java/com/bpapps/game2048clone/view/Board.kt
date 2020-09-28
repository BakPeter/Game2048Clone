package com.bpapps.game2048clone.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.bpapps.game2048clone.R
import com.bpapps.game2048clone.model.*
import java.util.*

class Board(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private lateinit var squares: Array<Array<Square>>
    private lateinit var squaresAnim: Array<Square>

    init {
        View.inflate(context, R.layout.board, this)
        initSquares(rootView)
    }

    private fun initSquares(view: View) {
        val tv00: Square = view.findViewById(R.id.tvSquare00)
        val tv01: Square = view.findViewById(R.id.tvSquare01)
        val tv02: Square = view.findViewById(R.id.tvSquare02)
        val tv03: Square = view.findViewById(R.id.tvSquare03)
        val tv10: Square = view.findViewById(R.id.tvSquare10)
        val tv11: Square = view.findViewById(R.id.tvSquare11)
        val tv12: Square = view.findViewById(R.id.tvSquare12)
        val tv13: Square = view.findViewById(R.id.tvSquare13)
        val tv20: Square = view.findViewById(R.id.tvSquare20)
        val tv21: Square = view.findViewById(R.id.tvSquare21)
        val tv22: Square = view.findViewById(R.id.tvSquare22)
        val tv23: Square = view.findViewById(R.id.tvSquare23)
        val tv30: Square = view.findViewById(R.id.tvSquare30)
        val tv31: Square = view.findViewById(R.id.tvSquare31)
        val tv32: Square = view.findViewById(R.id.tvSquare32)
        val tv33: Square = view.findViewById(R.id.tvSquare33)

        squares = arrayOf(
            arrayOf(tv00, tv01, tv02, tv03),
            arrayOf(tv10, tv11, tv12, tv13),
            arrayOf(tv20, tv21, tv22, tv23),
            arrayOf(tv30, tv31, tv32, tv33)
        )

        val tvAnim0: Square = view.findViewById(R.id.tvMoveAnim0)
        val tvAnim1: Square = view.findViewById(R.id.tvMoveAnim1)
        val tvAnim2: Square = view.findViewById(R.id.tvMoveAnim2)
        val tvAnim3: Square = view.findViewById(R.id.tvMoveAnim3)
        val tvAnim4: Square = view.findViewById(R.id.tvMoveAnim4)
        val tvAnim5: Square = view.findViewById(R.id.tvMoveAnim5)
        val tvAnim6: Square = view.findViewById(R.id.tvMoveAnim6)
        val tvAnim7: Square = view.findViewById(R.id.tvMoveAnim7)
        val tvAnim8: Square = view.findViewById(R.id.tvMoveAnim8)
        val tvAnim9: Square = view.findViewById(R.id.tvMoveAnim9)
        val tvAnim10: Square = view.findViewById(R.id.tvMoveAnim10)
        val tvAnim11: Square = view.findViewById(R.id.tvMoveAnim11)

        squaresAnim = arrayOf(
            tvAnim0,
            tvAnim1,
            tvAnim2,
            tvAnim3,
            tvAnim4,
            tvAnim5,
            tvAnim6,
            tvAnim7,
            tvAnim8,
            tvAnim9,
            tvAnim10,
            tvAnim11
        )
    }

    fun updateBoard(boardStatus: Array<Array<GameEngine.Square>>?) {
        boardStatus?.let {
            squares.forEachIndexed { row, line ->
                line.forEachIndexed { col, square ->
                    square.num = boardStatus[row][col].num
                }
            }
        }
    }

    fun updateMove(
        data: MoveFinishedDataHolder,
        animationStartedCallback: IOnAnimationStarted?,
        animationFinishedCallback: IOnAnimationFinished?
    ) {
        AnimatorSet().apply {
            playTogether(getAnimations(data.moves))

            duration = 200

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    initAnimSquares(data.moves.size)
                    updateBoardSquaresValues(data.moves)

                    data.addedSquareCoordinate?.let { _ ->
                        addNewSquare(data.addedSquareCoordinate!!, data.addSquareValue!!)
                    }

                    animationFinishedCallback?.onAnimationFinished()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    animationStartedCallback?.onAnimationStarted()
                }
            })

            start()
        }
    }

    private fun addNewSquare(coordinate: Coordinate, value: Int) {
        squares[coordinate.row][coordinate.col].num = value
    }

    private fun updateBoardSquaresValues(moves: ArrayList<SquareMovement>) {
        moves.forEach { move -> squares[move.to.row][move.to.col].num = move.valueAtTheEnd }
    }

    private fun initAnimSquares(size: Int) {
        squaresAnim.forEach { square ->
            square.emptied()
            square.visibility = View.GONE
        }
//        for (i in 0 until size) {
//            squaresAnim[i].visibility = View.GONE
//        }
    }

    private fun getAnimations(moves: ArrayList<SquareMovement>): MutableCollection<Animator>? {
        val retVal = mutableListOf<Animator>()

        moves.forEachIndexed { index, move ->
            retVal.add(getAnimationForMove(index, move))
        }

        return retVal
    }

    private fun getAnimationForMove(index: Int, move: SquareMovement): Animator {
        val squareAnim = squaresAnim[index]

        val startSquare = squares[move.from.row][move.from.col]

        val endSquare = squares[move.to.row][move.to.col]

        squareAnim.x = startSquare.x
        squareAnim.y = startSquare.y
        squareAnim.num = startSquare.num
        squareAnim.visibility = View.VISIBLE
//        squareAnim.bringToFront()

        startSquare.emptied()

        val propertyName =
            if (move.direction == MoveDirection.UP || move.direction == MoveDirection.DOWN) "translationY" else "translationX"
        val value =
            if (move.direction == MoveDirection.UP || move.direction == MoveDirection.DOWN) endSquare.y else endSquare.x

        return ObjectAnimator.ofFloat(
            squareAnim,
            propertyName,
            value
        )
    }


    companion object {
        private const val TAG = "TAG.Board"
    }

    interface IOnAnimationStarted {
        fun onAnimationStarted()
    }

    interface IOnAnimationFinished {
        fun onAnimationFinished()
    }
}