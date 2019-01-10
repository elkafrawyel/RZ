package com.hmaserv.rz.framework.database

import com.hmaserv.rz.data.database.IDatabase
import io.objectbox.BoxStore

class Database(val boxStore: BoxStore) : IDatabase {

    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getInstance(
            boxStore: BoxStore
        ): Database {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Database(boxStore).also { INSTANCE = it }
            }
        }
    }
}

