package com.bpapps.game2048clone.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bpapps.game2048clone.R
import com.bpapps.game2048clone.viewmodel.MainViewViewModel
import kotlinx.android.synthetic.main.board.*
import kotlin.math.abs

class MainViewFragment : Fragment(), View.OnTouchListener {

    private val viewModel by viewModels<MainViewViewModel>()

    private lateinit var btnNewGame: AppCompatButton
    private lateinit var tvScore: AppCompatTextView
    private lateinit var tvBestScore: AppCompatTextView

    private lateinit var board: FrameLayout
    private var x1: Float = 0F
    private var x2: Float = 0F
    private var y1: Float = 0F
    private var y2: Float = 0F

    private lateinit var squares: Array<Array<Square>>
    private lateinit var squareAnim1: Square
    private lateinit var squareAnim2: Square

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_view_fragment, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)?.let { sharedPref ->
            viewModel.bestScore = sharedPref.getInt(PREFERENCES_BEST_SCORE, 0)
        }

        btnNewGame = view.findViewById(R.id.btnNewGame)
        btnNewGame.setOnClickListener {
            val startSquare = squares[0][0]
            val endSquare = squares[3][3]

            squareAnim1.x = startSquare.x
            squareAnim1.y = startSquare.y
            squareAnim1.visibility = View.VISIBLE
            squareAnim1.num = 8
//            tvMoveAnim1.bringToFront()

            val anim1y = ObjectAnimator.ofFloat(tvMoveAnim1, "translationY", endSquare.y)
            val anim1x = ObjectAnimator.ofFloat(tvMoveAnim1, "translationX", endSquare.x)

            squareAnim2.x = endSquare.x
            squareAnim2.y = endSquare.y
            squareAnim2.visibility = View.VISIBLE
            squareAnim2.num = 16
//            tvMoveAnim2.bringToFront()

            val anim2y = ObjectAnimator.ofFloat(squareAnim2, "translationY", startSquare.x)
            val anim2x = ObjectAnimator.ofFloat(squareAnim2, "translationX", startSquare.y)

            AnimatorSet().apply {
                playTogether(listOf(anim1x, anim1y, anim2x, anim2y))

                duration = 1000

                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator?) {
                        TODO("Not yet implemented")
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        squareAnim1.visibility = View.GONE
                        squareAnim2.visibility = View.GONE
                        startSquare.num = startSquare.num * 2 * 2
                        endSquare.num = endSquare.num * 2
                    }

                    override fun onAnimationCancel(animation: Animator?) {
                        TODO("Not yet implemented")
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        //TODO("Not yet implemented")
                    }

                })
                start()
            }
        }

        board = view.findViewById(R.id.board)
        board.setOnTouchListener(this)


        tvBestScore = view.findViewById(R.id.tvBestScore)

        tvScore = view.findViewById(R.id.tvScore)

        initSquares(view)
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

        squareAnim1 = view.findViewById(R.id.tvMoveAnim1)
        squareAnim2 = view.findViewById(R.id.tvMoveAnim2)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        var retVal = true

        when (p1?.action) {
            MotionEvent.ACTION_DOWN -> {
                x1 = p1.x
                y1 = p1.y

//                Log.d(TAG, "(x1, y1)= ($x1, $y1)")
            }
            MotionEvent.ACTION_UP -> {
                x2 = p1.x
                y2 = p1.y

//                Log.d(TAG, "(x2, y2)= ($x2, $y2)")

                val dX = abs(x1 - x2)
                val dY = abs(y1 - y2)

                when {
                    dX < dY -> {
                        if (y1 > y2) {
                            Log.d(TAG, "swipe up")

                        } else {
                            Log.d(TAG, "swipe down")
                        }
                    }
                    dX > dY -> {
                        if (x1 > x2) {
                            Log.d(TAG, "swipe left")
                        } else {
                            Log.d(TAG, "swipe right")
                        }
                    }
                    dX == dY -> {
                        Log.d(TAG, "touch, not swipe")

                        retVal = false
                    }
                }
            }
        }

        return retVal
    }

    companion object {
        private const val TAG = "TAG.MainViewFragment"
        private const val PREFERENCES_NAME =
            "com.bpapps.ex2048clone.view.preferences_name"
        private const val PREFERENCES_BEST_SCORE =
            "com.bpapps.ex2048clone.view.preferences_best_score"
    }
}