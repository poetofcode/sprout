package data.utils

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object DateFormatter {

    private val sourceDateFormat by lazy {
        DateTimeFormatter.ISO_ZONED_DATE_TIME
    }

    private val humanDateFormat by lazy {
        "dd.MM.yyyy 'в' HH:mm"
    }

    private val locale by lazy {
        Locale.getDefault()
    }

    fun getHumanDate(date: String): String =
        runCatching {
            val serverData = LocalDateTime.parse(date, sourceDateFormat)
            val offset = OffsetDateTime.now().offset
            val dateTe = Date.from(serverData.toInstant(offset))
            SimpleDateFormat(humanDateFormat, locale).format(dateTe)
        }.getOrDefault(defaultValue = date)
}