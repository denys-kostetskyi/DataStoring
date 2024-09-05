package com.denyskostetskyi.datastoring.data.storage

import android.content.Context
import com.denyskostetskyi.datastoring.domain.model.User
import com.denyskostetskyi.datastoring.domain.repository.UserRepository
import java.io.File

class InternalStorageUserRepository(context: Context) : UserRepository {
    private val applicationContext = context.applicationContext

    override suspend fun saveUser(user: User) = getUserFile().writeText(userToString(user))

    override suspend fun getUser(id: Int): User {
        val file = getUserFile()
        if (!file.exists()) {
            return User.DEFAULT
        }
        val text = file.readText()
        return parseUser(text)
    }

    override suspend fun updateUser(user: User): Boolean {
        saveUser(user)
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        getUserFile().delete()
        return true
    }

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
