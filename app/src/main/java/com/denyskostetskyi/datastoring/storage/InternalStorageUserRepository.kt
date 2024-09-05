package com.denyskostetskyi.datastoring.storage

import android.content.Context
import com.denyskostetskyi.datastoring.domain.model.User
import java.io.File

class InternalStorageUserRepository(context: Context) {
    private val applicationContext = context.applicationContext

    fun saveUser(user: User) = getUserFile().writeText(userToString(user))

    fun getUser(): User {
        val file = getUserFile()
        if (!file.exists()) {
            return User.DEFAULT
        }
        val text = file.readText()
        return parseUser(text)
    }

    fun updateUser(user: User) = saveUser(user)

    fun deleteUser() = getUserFile().delete()

    private fun getUserFile() = File(applicationContext.filesDir, FILE_NAME)

    private fun userToString(user: User) = buildString {
        append(user.id)
        append(DELIMITER)
        append(user.firstName)
        append(DELIMITER)
        append(user.lastName)
    }

    private fun parseUser(text: String): User {
        val data = text.split(DELIMITER)
        return if (data.size == 3) {
            val id = data[0].toIntOrNull() ?: return User.DEFAULT
            User(id, data[1], data[2])
        } else {
            User.DEFAULT
        }
    }

    companion object {
        private const val FILE_NAME = "user.txt"
        private const val DELIMITER = ","
    }
}
