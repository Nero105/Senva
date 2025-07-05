package com.example.senva

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SenvaDB"
        private const val DATABASE_VERSION = 1

        // Tabla de citas
        private const val TABLE_CITAS = "citas"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DIRECCION = "direccion"
        private const val COLUMN_PROVINCIA = "provincia"
        private const val COLUMN_DISTRITO = "distrito"
        private const val COLUMN_LATITUD = "latitud"
        private const val COLUMN_LONGITUD = "longitud"
        private const val COLUMN_ESPECIALIDAD = "especialidad"
        private const val COLUMN_FECHA_CREACION = "fecha_creacion"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_CITAS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DIRECCION TEXT,
                $COLUMN_PROVINCIA TEXT,
                $COLUMN_DISTRITO TEXT,
                $COLUMN_LATITUD REAL,
                $COLUMN_LONGITUD REAL,
                $COLUMN_ESPECIALIDAD TEXT,
                $COLUMN_FECHA_CREACION DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """.trimIndent()
        
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CITAS")
        onCreate(db)
    }

    // Insertar una nueva cita
    fun insertarCita(
        direccion: String,
        provincia: String,
        distrito: String,
        latitud: Double,
        longitud: Double,
        especialidad: String
    ): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DIRECCION, direccion)
            put(COLUMN_PROVINCIA, provincia)
            put(COLUMN_DISTRITO, distrito)
            put(COLUMN_LATITUD, latitud)
            put(COLUMN_LONGITUD, longitud)
            put(COLUMN_ESPECIALIDAD, especialidad)
        }
        
        return db.insert(TABLE_CITAS, null, values)
    }

    // Obtener todas las citas
    fun obtenerTodasLasCitas(): List<Cita> {
        val citas = mutableListOf<Cita>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CITAS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_FECHA_CREACION DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val cita = Cita(
                    id = getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    direccion = getString(getColumnIndexOrThrow(COLUMN_DIRECCION)),
                    provincia = getString(getColumnIndexOrThrow(COLUMN_PROVINCIA)),
                    distrito = getString(getColumnIndexOrThrow(COLUMN_DISTRITO)),
                    latitud = getDouble(getColumnIndexOrThrow(COLUMN_LATITUD)),
                    longitud = getDouble(getColumnIndexOrThrow(COLUMN_LONGITUD)),
                    especialidad = getString(getColumnIndexOrThrow(COLUMN_ESPECIALIDAD)),
                    fechaCreacion = getString(getColumnIndexOrThrow(COLUMN_FECHA_CREACION))
                )
                citas.add(cita)
            }
        }
        cursor.close()
        return citas
    }

    // Obtener la última cita
    fun obtenerUltimaCita(): Cita? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_CITAS,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_FECHA_CREACION DESC",
            "1"
        )

        return if (cursor.moveToFirst()) {
            val cita = Cita(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                direccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIRECCION)),
                provincia = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROVINCIA)),
                distrito = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DISTRITO)),
                latitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUD)),
                longitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUD)),
                especialidad = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESPECIALIDAD)),
                fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_CREACION))
            )
            cursor.close()
            cita
        } else {
            cursor.close()
            null
        }
    }

    // Eliminar una cita por ID
    fun eliminarCita(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_CITAS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Actualizar la especialidad de una cita
    fun actualizarEspecialidadCita(id: Int, especialidad: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ESPECIALIDAD, especialidad)
        }
        return db.update(TABLE_CITAS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
}

// Clase de datos para representar una cita
data class Cita(
    val id: Int,
    val direccion: String,
    val provincia: String,
    val distrito: String,
    val latitud: Double,
    val longitud: Double,
    val especialidad: String,
    val fechaCreacion: String
) 