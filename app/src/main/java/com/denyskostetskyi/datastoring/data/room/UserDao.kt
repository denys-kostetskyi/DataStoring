package com.denyskostetskyi.datastoring.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUser(id: Int): UserDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: UserDbModel)

    @Update
    suspend fun updateUser(user: UserDbModel): Int

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Int): Int
}
