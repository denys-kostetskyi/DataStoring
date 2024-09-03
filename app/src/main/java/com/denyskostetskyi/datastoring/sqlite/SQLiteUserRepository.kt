package com.denyskostetskyi.datastoring.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.denyskostetskyi.datastoring.model.User
import com.denyskostetskyi.datastoring.sqlite.UserContract.DATABASE_NAME
import com.denyskostetskyi.datastoring.sqlite.UserContract.DATABASE_VERSION
import com.denyskostetskyi.datastoring.sqlite.UserContract.UserEntry.COLUMN_FIRST_NAME
import com.denyskostetskyi.datastoring.sqlite.UserContract.UserEntry.COLUMN_ID
import com.denyskostetskyi.datastoring.sqlite.UserContract.UserEntry.COLUMN_LAST_NAME
import com.denyskostetskyi.datastoring.sqlite.UserContract.UserEntry.SQL_CREATE_ENTRIES
import com.denyskostetskyi.datastoring.sqlite.UserContract.UserEntry.SQL_DELETE_ENTRIES
import com.denyskostetskyi.datastoring.sqlite.UserContract.UserEntry.TABLE_NAME

class SQLiteUserRepository(applicationContext: Context) : SQLiteOpenHelper(
    applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val db = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun saveUser(user: User) {
        val values = ContentValues().apply {
            put(COLUMN_ID, user.id)
            put(COLUMN_FIRST_NAME, user.firstName)
            put(COLUMN_LAST_NAME, user.lastName)
        }
        db.insert(TABLE_NAME, null, values)
    }

    fun getUser(id: Int): User {
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
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

    fun updateUser(user: User): Int {
        val values = ContentValues().apply {
            put(COLUMN_FIRST_NAME, user.firstName)
            put(COLUMN_LAST_NAME, user.lastName)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(user.id.toString()))
    }

    fun deleteUser(id: Int) =
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))

    fun closeConnection() {
        db.close()
    }
}
