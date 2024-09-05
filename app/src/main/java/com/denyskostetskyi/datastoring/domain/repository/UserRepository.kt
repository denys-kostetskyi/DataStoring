package com.denyskostetskyi.datastoring.domain.repository

import com.denyskostetskyi.datastoring.domain.model.User

interface UserRepository {
    suspend fun saveUser(user: User)

    suspend fun getUser(id: Int): User

    suspend fun updateUser(user: User): Boolean

    suspend fun deleteUser(id: Int): Boolean
}
