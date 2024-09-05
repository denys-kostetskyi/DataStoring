package com.denyskostetskyi.datastoring.data.sqlite

import android.provider.BaseColumns

object UserContract {
    const val DATABASE_NAME = "users_sqlite.db"
    const val DATABASE_VERSION = 1

    object UserEntry : BaseColumns {
        const val TABLE_NAME = "users"
        const val COLUMN_ID = "id"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        val USER_COLUMNS = arrayOf(COLUMN_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME)

        const val SQL_CREATE_ENTRIES = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY,
                $COLUMN_FIRST_NAME TEXT,
                $COLUMN_LAST_NAME TEXT
            )
        """
        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
