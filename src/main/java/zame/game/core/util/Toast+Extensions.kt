package zame.game.core.util

import android.widget.Toast
import zame.game.App

/**
 * Created by vinhdn on 01-Mar-18.
 */
fun toast(message: Any, length: Int = Toast.LENGTH_LONG) {
    when (message) {
        is String -> Toast.makeText(App.self, message, length).show()
        is Int -> Toast.makeText(App.self, message, length).show()
        else -> throw IllegalArgumentException("Argument message type is invalid. The first argument is only accepted on Int or String")
    }
}