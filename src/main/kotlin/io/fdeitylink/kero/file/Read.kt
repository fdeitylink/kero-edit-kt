package io.fdeitylink.kero.file

import java.nio.channels.ReadableByteChannel
import java.nio.ByteBuffer

import io.fdeitylink.util.toUInt

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

internal fun String.Companion.fromChannel(chan: ReadableByteChannel): String {
    val len = ByteBuffer.allocate(1).let {
        chan.read(it)
        it.flip()
        it.get().toUInt()
    }

    return ByteBuffer.allocate(len).let {
        chan.read(it)
        String.fromBytes(it.array())
    }
}

internal fun String.Companion.fromBytes(bytes: ByteArray) = String(bytes, charset("SJIS"))