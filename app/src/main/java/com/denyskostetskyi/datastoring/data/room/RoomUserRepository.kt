package com.denyskostetskyi.datastoring.data.room

import com.denyskostetskyi.datastoring.domain.model.User
import com.denyskostetskyi.datastoring.domain.repository.UserRepository

class RoomUserRepository(
    private val userDao: UserDao,
    private val userMapper: UserMapper
) : UserRepository {
    override suspend fun saveUser(user: User) {
        val userDbModel = userMapper.mapEntityToDbModel(user)
        userDao.saveUser(userDbModel)
    }

    override suspend fun getUser(id: Int): User {
        val userDbModel = userDao.getUser(id)
        return if (userDbModel == null) User.DEFAULT else userMapper.mapDbModelToEntity(userDbModel)
    }

    override suspend fun updateUser(user: User): Boolean {
        val userDbModel = userMapper.mapEntityToDbModel(user)
        return userDao.updateUser(userDbModel) > 0
    }

    override suspend fun deleteUser(id: Int): Boolean {
        return userDao.deleteUser(id) > 0
    }
}
