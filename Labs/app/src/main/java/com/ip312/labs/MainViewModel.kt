package com.ip312.labs

import android.app.Application
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ip312.labs.data.AppDatabase
import com.ip312.labs.data.User
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)

    private val prefs =
        app.getSharedPreferences("session", Context.MODE_PRIVATE)

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    init {
        val savedId = prefs.getLong(KEY_USER_ID, NO_USER)
        if (savedId != NO_USER) {
            viewModelScope.launch {
                _currentUser.value = db.userDao().getById(savedId)
            }
        }
    }

    suspend fun getAllUsers(): List<User> =
        db.userDao().getAll().sortedBy { it.name.lowercase() }

    fun selectUser(user: User) {
        _currentUser.value = user
        prefs.edit().putLong(KEY_USER_ID, user.id).apply()
    }

    suspend fun registerUser(user: User): User? {
        val dao = db.userDao()

        if (dao.findByName(user.name) != null) {
            return null
        }

        return try {
            val id = dao.insert(user)
            user.copy(id = id).also { selectUser(it) }
        } catch (e: SQLiteConstraintException) {
            null
        }
    }

    private companion object {
        const val KEY_USER_ID = "current_user_id"
        const val NO_USER = -1L
    }
}
