package io.fdeitylink.kero.file

import java.nio.channels.ReadableByteChannel
import java.nio.ByteBuffer

import io.fdeitylink.kero.CHARSET

internal fun validateHeader(chan: ReadableByteChannel, header: String, type: String) {
    ByteBuffer.allocate(header.toByteArray().size).let {
        chan.read(it)
        validate(String.fromBytes(it.array()) == header) { "Invalid $type header" }
    }
}

internal inline fun validate(value: Boolean, lazyMessage: () -> Any) {
    if (!value) {
        throw ParseException(lazyMessage().toString())
    }
}

internal fun String.Companion.fromBytes(bytes: ByteArray) = String(bytes, CHARSET)