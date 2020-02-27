package ru.smartro.worknote.database.Converters

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateConverter {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String {
        return date.format(formatter)
    }


    @TypeConverter
    fun toLocalDate(date: String): LocalDate {
        return date.let {
            LocalDate.parse(date, formatter)
        }
    }

}
