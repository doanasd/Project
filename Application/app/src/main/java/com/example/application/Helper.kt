package com.example.application

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class Helper(private val context: Context?) {
    private var database: SQLiteDatabase? = null
    fun openDatabase() {

        val databaseName = "db"
        val inputStream = context!!.assets.open(databaseName)

        val dbPath = context.getDatabasePath(databaseName).path
        val databaseFile = File(dbPath)

        // Nếu database chưa tồn tại, copy từ assets sang data
        if (!databaseFile.exists()) {
            try {
                copyDatabase(inputStream, dbPath)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Mở database
        database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    @Throws(IOException::class)
     fun copyDatabase(inputStream: InputStream, dbPath: String) {
        val outputStream: OutputStream = FileOutputStream(dbPath)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }
    fun closeDatabase() {
        if (database != null) {
            database!!.close()
        }
    }
    fun query(
        table: String?,
        columns: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        groupBy: String?,
        having: String?,
        orderBy: String?
    ): Cursor {
        return database!!.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
    }

    fun insertData(table: String, values: ContentValues) {
        if (database != null) {
            try {
                val newRowId = database!!.insert(table, null, values)
                // Handle successful insertion (optional)
                if (newRowId != -1L) {
                    // Log success message or perform other actions
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle insertion error
            }
        } else {
            // Handle scenario where database is not open
            //  - Throw exception, log error, etc.
        }
    }
    fun update(
        table: String,
        contentValues: ContentValues,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        if (database != null) {
            return database!!.update(table, contentValues, selection, selectionArgs)
        } else {
            throw IllegalStateException("Database is not open")
        }
    }
    fun execSQL(sql: String?, values: ContentValues) {
        database!!.execSQL(sql)
    }
}

