package com.bpapps.game2048clone.model

data class MoveFinishedDataHolder(
    val moves: ArrayList<SquareMovement>,
    val isGameFinished: Boolean,
    var addedSquareCoordinate: Coordinate? = null,
    var addSquareValue: Int? = null
)