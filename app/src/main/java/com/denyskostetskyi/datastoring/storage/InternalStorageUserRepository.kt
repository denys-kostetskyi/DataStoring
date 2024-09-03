package com.denyskostetskyi.datastoring.storage

import android.content.Context
import com.denyskostetskyi.datastoring.model.User
import java.io.File

class InternalStorageUserRepository(private val applicationContext: Context) {
    fun saveUser(user: User) {
        val file = getUserFile()
        file.writeText("${user.id}$DELIMITER${user.firstName}$DELIMITER${user.lastName}")
    }

    fun getUser(): User {
        val file = getUserFile()
        if (!file.exists()) {
            return User.DEFAULT
        }
        val data = file.readText().split(DELIMITER)
        return if (data.size == 3) {
            val id = data[0].toIntOrNull() ?: return User.DEFAULT
            User(id, data[1], data[2])
        } else {
            User.DEFAULT
        }
    }

    fun updateUser(user: User) {
        saveUser(user)
    }

    fun deleteUser() {
        val file = getUserFile()
        if (file.exists()) {
            file.delete()
        }
    }

    private fun getUserFile(): File = File(applicationContext.filesDir, FILE_NAME)

    companion object {
        private const val FILE_NAME = "user.txt"
        private const val DELIMITER = ","
    }
}