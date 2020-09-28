package com.bpapps.game2048clone.model

data class SquareMovement(
    var direction: @MoveDirection Int,
    val from: Coordinate,
    val to: Coordinate,
    val valueAtTheEnd: Int,
    val merged: Boolean
) {
}