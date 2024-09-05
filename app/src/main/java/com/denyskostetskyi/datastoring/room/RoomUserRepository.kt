package com.denyskostetskyi.datastoring.room

import com.denyskostetskyi.datastoring.model.User

class RoomUserRepository(private val userDao: UserDao, private val userMapper: UserMapper) {
    suspend fun saveUser(user: User) {
        val userDbModel = userMapper.mapEntityToDbModel(user)
        userDao.saveUser(userDbModel)
    }

    suspend fun getUser(id: Int): User {
        val userDbModel = userDao.getUser(id)
        return userMapper.mapDbModelToEntity(userDbModel)
    }

    suspend fun updateUser(user: User) {
        val userDbModel = userMapper.mapEntityToDbModel(user)
        userDao.updateUser(userDbModel)
    }

    suspend fun deleteUser(id: Int) = userDao.deleteUser(id)
}
