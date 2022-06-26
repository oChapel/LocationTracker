package ua.com.foxminded.locationtrackera.util

import android.widget.EditText

object Utils {

    fun getTextFromEditText(
        editText: EditText
    ): String = editText.text.toString().trim { it <= ' ' }

    fun hasMoreThanFiveChars(
        string: String?
    ): Boolean = if (string == null) false else string.trim { it <= ' ' }.length > 5
}
