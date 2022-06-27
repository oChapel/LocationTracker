package ua.com.foxminded.locationtrackera.models.shared_preferences

interface SharedPreferencesModel {
    fun setSharedPreferencesServiceFlag(flag: Boolean)
    fun getSharedPreferencesServiceFlag(): Boolean
}
