package com.bpapps.game2048clone.model

import androidx.annotation.IntDef
import com.bpapps.game2048clone.model.MoveDirection.Companion.UP
import com.bpapps.game2048clone.model.MoveDirection.Companion.DOWN
import com.bpapps.game2048clone.model.MoveDirection.Companion.LEFT
import com.bpapps.game2048clone.model.MoveDirection.Companion.RIGHT

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.SOURCE)
@IntDef(UP, DOWN, LEFT, RIGHT)
annotation class MoveDirection {
    companion object {
        const val UP = 1
        const val DOWN = 2
        const val LEFT = 3
        const val RIGHT = 4

    }
}