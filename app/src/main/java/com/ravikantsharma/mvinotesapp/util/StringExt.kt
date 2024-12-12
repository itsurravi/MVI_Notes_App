package com.ravikantsharma.mvinotesapp.util

import android.content.Context
import java.security.MessageDigest
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String?.hashedString() = this?.let {
    MessageDigest.getInstance("SHA-256")
        .digest(toByteArray())
        .fold(StringBuilder()) { sb, string -> sb.append("%02x".format(string)) }.toString()
}

fun OffsetDateTime.getDate(): String {
    val newDateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
    return this.format(newDateFormat)
}

fun OffsetDateTime.getTime(): String {
    val newDateFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
    return this.format(newDateFormat)
}

fun Context.stringResource(messageId: Int) = this.getString(messageId)