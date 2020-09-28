package com.bpapps.game2048clone.viewmodel

import androidx.lifecycle.ViewModel
import com.bpapps.game2048clone.model.GameEngine
import com.bpapps.game2048clone.model.MoveFinishedDataHolder

class MainViewViewModel : ViewModel(), GameEngine.IOnMoveFinished, GameEngine.IOnBestScoreUpdated,
    GameEngine.IOnGameScoreUpdates {
    private val gameEngine: GameEngine = GameEngine(this, this, this)

    var bestScore: Int = 0
        set(value) {
            gameEngine.bestScore = value
            if (field != value)
                field = value
        }

    var boardStatus: Array<Array<GameEngine.Square>>? = null
        get() = gameEngine.board
        private set

    private var moveFinishedCallBack: IOnMoveFinishedListener? = null
    private var bestScoreUpdatedCallback: IOnBestScoreUpdatedListener? = null
    private var scoreUpdatedCallback: IOnScoreUpdatedListener? = null
    private var gameFinishedCallback: IOnGameFinishedListener? = null

    fun startNewGame() {
        gameEngine.startNewGame()
    }

    fun gameFinished() {
        gameFinishedCallback?.onGameFinished(gameEngine.isVictories)
    }

    fun swipeUp() {
        if (gameEngine.isGameFinished) gameFinished() else gameEngine.swipeUp()
    }

    fun swipeDawn() {
        if (gameEngine.isGameFinished) gameFinished() else gameEngine.swipeDown()
    }

    fun swipeLeft() {
        if (gameEngine.isGameFinished) gameFinished() else gameEngine.swipeLeft()
    }

    fun swipeRight() {
        if (gameEngine.isGameFinished) gameFinished() else gameEngine.swipeRight()
    }

    override fun onMoveFinished(data: MoveFinishedDataHolder) {
        gameEngine.logBoard()
        moveFinishedCallBack?.omMoveFinished(data)
    }

    override fun onBestScoreUpdated(newBestScore: Int) {
        if (bestScore != newBestScore) {
            bestScore = newBestScore
        }
        bestScoreUpdatedCallback?.onBestScoreUpdated(newBestScore)
    }

    override fun onScoreUpdated(newScore: Int) {
        scoreUpdatedCallback?.onScoreUpdated(newScore)
    }

    fun registerForMoveFinishedCallback(callback: IOnMoveFinishedListener) {
        moveFinishedCallBack = callback
    }

    fun unRegisterMoveFinishedCallback() {
        moveFinishedCallBack = null
    }

    fun registerForBestScoreUpdatedCallback(callback: IOnBestScoreUpdatedListener) {
        bestScoreUpdatedCallback = callback
    }

    fun unRegisterBestScoreUpdatedCallback() {
        bestScoreUpdatedCallback = null
    }

    fun registerForScoreUpdatedCallback(callback: IOnScoreUpdatedListener) {
        scoreUpdatedCallback = callback
    }

    fun unRegistertScoreUpdatedCallback() {
        bestScoreUpdatedCallback = null
    }

    fun registerForGameFinishedCallback(callback: IOnGameFinishedListener) {
        gameFinishedCallback = callback
    }

    fun unregisterGameFinishedCallback() {
        gameFinishedCallback = null
    }

    interface IOnMoveFinishedListener {
        fun omMoveFinished(data: MoveFinishedDataHolder)
    }

    interface IOnScoreUpdatedListener {
        fun onScoreUpdated(newScore: Int)
    }

    interface IOnBestScoreUpdatedListener {
        fun onBestScoreUpdated(newBestScore: Int)
    }

    interface IOnGameFinishedListener {
        fun onGameFinished(victories: Boolean)
    }
}