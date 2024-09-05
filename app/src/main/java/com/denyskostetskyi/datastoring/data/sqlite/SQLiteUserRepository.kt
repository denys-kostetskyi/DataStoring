package com.denyskostetskyi.datastoring.data.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.DATABASE_NAME
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.DATABASE_VERSION
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.UserEntry.COLUMN_FIRST_NAME
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.UserEntry.COLUMN_ID
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.UserEntry.COLUMN_LAST_NAME
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.UserEntry.SQL_CREATE_ENTRIES
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.UserEntry.SQL_DELETE_ENTRIES
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.UserEntry.TABLE_NAME
import com.denyskostetskyi.datastoring.data.sqlite.UserContract.UserEntry.USER_COLUMNS
import com.denyskostetskyi.datastoring.domain.model.User
import com.denyskostetskyi.datastoring.domain.repository.UserRepository

class SQLiteUserRepository(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
), UserRepository {
    private val db = writableDatabase

    override fun onCreate(db: SQLiteDatabase) = db.execSQL(SQL_CREATE_ENTRIES)

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override suspend fun saveUser(user: User) {
        val values = ContentValues().apply {
            put(COLUMN_ID, user.id)
            put(COLUMN_FIRST_NAME, user.firstName)
            put(COLUMN_LAST_NAME, user.lastName)
        }
        db.insert(TABLE_NAME, null, values)
    }

    override suspend fun getUser(id: Int): User {
        val cursor = db.query(
            TABLE_NAME,
            USER_COLUMNS,
            WHERE_CLAUSE_ID,
            idToArgs(id),
            null, null, null
        )
        val user = if (cursor.moveToFirst()) {
            User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                firstName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                lastName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME))
            )
        } else {
            User.DEFAULT
        }
        cursor.close()
        return user
    }

    override suspend fun updateUser(user: User): Boolean {
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, user.firstName)
            put(COLUMN_LAST_NAME, user.lastName)
        }
        return db.update(TABLE_NAME, values, WHERE_CLAUSE_ID, idToArgs(user.id)) > 0
    }

    override suspend fun deleteUser(id: Int): Boolean =
        db.delete(TABLE_NAME, WHERE_CLAUSE_ID, idToArgs(id)) > 0

    fun closeConnection() = db.close()

    companion object {
        private const val WHERE_CLAUSE_ID = "$COLUMN_ID = ?"
        private fun idToArgs(id: Int) = arrayOf(id.toString())
    }
}
