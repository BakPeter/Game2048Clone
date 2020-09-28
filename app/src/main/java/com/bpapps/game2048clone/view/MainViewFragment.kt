package com.bpapps.game2048clone.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bpapps.game2048clone.R
import com.bpapps.game2048clone.model.MoveFinishedDataHolder
import com.bpapps.game2048clone.viewmodel.MainViewViewModel
import kotlin.math.abs

class MainViewFragment : Fragment(), View.OnTouchListener, MainViewViewModel.IOnMoveFinishedListener,
    MainViewViewModel.IOnBestScoreUpdatedListener, MainViewViewModel.IOnScoreUpdatedListener,
    MainViewViewModel.IOnGameFinishedListener {

    private val viewModel by viewModels<MainViewViewModel>()

    private lateinit var btnNewGame: AppCompatButton
    private lateinit var tvScore: AppCompatTextView
    private lateinit var tvBestScore: AppCompatTextView

    private lateinit var board: Board
    private var x1: Float = 0F
    private var x2: Float = 0F
    private var y1: Float = 0F
    private var y2: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)?.let { sharedPref ->
            viewModel.bestScore = sharedPref.getInt(PREFERENCES_BEST_SCORE, 0)
        }

        activity?.window?.decorView?.layoutDirection = View.LAYOUT_DIRECTION_LTR 
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_view_fragment, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnNewGame = view.findViewById(R.id.btnNewGame)
        btnNewGame.setOnClickListener {
            viewModel.startNewGame()
            board.updateBoard(viewModel.boardStatus)
        }

        board = view.findViewById(R.id.board)
        board.setOnTouchListener(this)

        tvBestScore = view.findViewById(R.id.tvBestScore)

        tvScore = view.findViewById(R.id.tvScore)
        onBestScoreUpdated(viewModel.bestScore)
    }

    override fun onResume() {
        super.onResume()
        board.updateBoard(viewModel.boardStatus)

        viewModel.registerForMoveFinishedCallback(this)
        viewModel.registerForBestScoreUpdatedCallback(this)
        viewModel.registerForScoreUpdatedCallback(this)
        viewModel.registerForGameFinishedCallback(this)
    }

    override fun onStop() {
        super.onStop()
        activity?.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)?.edit()
            ?.let { editor ->
                editor.putInt(PREFERENCES_BEST_SCORE, viewModel.bestScore)
                editor.commit()
            }
        viewModel.unRegisterMoveFinishedCallback()
        viewModel.unRegisterBestScoreUpdatedCallback()
        viewModel.unRegistertScoreUpdatedCallback()
        viewModel.unregisterGameFinishedCallback()
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
                            viewModel.swipeUp()
//                            board.moveSquare(Coordinate(3, 0), Coordinate(0,0))
                        } else {
                            Log.d(TAG, "swipe down")
                            viewModel.swipeDawn()
//                            board.moveSquare(Coordinate(0, 0), Coordinate(3,0))
                        }
                    }
                    dX > dY -> {
                        if (x1 > x2) {
                            Log.d(TAG, "swipe left")
                            viewModel.swipeLeft()
//                            board.moveSquare(Coordinate(0, 3), Coordinate(0,0))
                        } else {
                            Log.d(TAG, "swipe right")
                            viewModel.swipeRight()
//                            board.moveSquare(Coordinate(0, 0), Coordinate(0,3))
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

    override fun omMoveFinished(data: MoveFinishedDataHolder) {
        board.updateMove(
            data,
            object : Board.IOnAnimationStarted {
                override fun onAnimationStarted() {
//                    Log.d(TAG, "animation started")
                    board.isEnabled = false
                }
            },
            object : Board.IOnAnimationFinished {
                override fun onAnimationFinished() {
//                    Log.d(TAG, "animation ended")
                    board.isEnabled = true

                    if(data.isGameFinished) {
                        viewModel.gameFinished()
                    }
                }
            })

    }

    override fun onBestScoreUpdated(newBestScore: Int) {
        tvBestScore.text = newBestScore.toString()
    }

    override fun onScoreUpdated(newScore: Int) {
        tvScore.text = newScore.toString()
    }

    override fun onGameFinished(victories: Boolean) {
        if(victories) {
            AlertDialog.Builder(requireContext()).setTitle("You won!!!")
                .setMessage("Push 'NEW GAME' to start again.")
                .setPositiveButton("OK") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        } else {
            AlertDialog.Builder(requireContext()).setTitle("Game Over!!")
                .setMessage("Push 'NEW GAME' to start again.")
                .setPositiveButton("OK") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
        }

    }
}
