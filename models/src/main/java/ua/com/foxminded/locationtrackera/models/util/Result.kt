package ua.com.foxminded.locationtrackera.models.util

open class Result<T> private constructor() {

    override fun toString(): String = when(this) {
        is Success<*> -> "Success[data=" + this.data.toString() + "]"
        is Error<*> -> "Error[exception=" + this.error.toString() + "]"
        else -> ""
    }

    val isSuccessful: Boolean
        get() = this is Success<*>

    class Success<T>(val data: T) : Result<T>()

    class Error<T>(val error: Throwable?) : Result<T>()
}