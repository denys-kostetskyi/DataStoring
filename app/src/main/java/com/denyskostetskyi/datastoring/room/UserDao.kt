package com.denyskostetskyi.datastoring.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserDbModel>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun getUser(id: Int): Flow<UserDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserDbModel)

    @Update
    suspend fun updateUser(user: UserDbModel)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Int)
}
