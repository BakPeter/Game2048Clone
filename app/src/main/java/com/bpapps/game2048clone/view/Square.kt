package com.bpapps.game2048clone.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.bpapps.game2048clone.R
import com.bpapps.game2048clone.model.GameEngine

class Square(context: Context, attrs: AttributeSet?) : AppCompatTextView(context, attrs) {
    var num: Int = GameEngine.EMPTY_SQUARE
        set(value) {
            field = value
            text = if (field == GameEngine.EMPTY_SQUARE) "" else field.toString()

            when (field) {
                2 -> setBackgroundColor(resources.getColor(R.color._2Back, null))
                4 -> setBackgroundColor(resources.getColor(R.color._4Back, null))
                8 -> setBackgroundColor(resources.getColor(R.color._8Back, null))
                16 -> setBackgroundColor(resources.getColor(R.color._16Back, null))
                32 -> setBackgroundColor(resources.getColor(R.color._32Back, null))
                64 -> setBackgroundColor(resources.getColor(R.color._64Back, null))
                128 -> setBackgroundColor(resources.getColor(R.color._128Back, null))
                256 -> setBackgroundColor(resources.getColor(R.color._256Back, null))
                512 -> setBackgroundColor(resources.getColor(R.color._512Back, null))
                1024 -> setBackgroundColor(resources.getColor(R.color._1024Back, null))
                2048 -> setBackgroundColor(resources.getColor(R.color._2048Back, null))
                else -> setBackgroundColor(
                    resources.getColor(
                        R.color.empty_square_background,
                        null
                    )
                )
            }
        }

    fun emptied() {
        num = GameEngine.EMPTY_SQUARE
    }
}