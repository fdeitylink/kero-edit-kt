package io.fdeitylink.kero.file

import io.fdeitylink.util.ushr

/**
 * Converts a [Short] into a [ByteArray] with the bytes of `this` [Short] in little-endian order
 */
internal fun Short.toBytes() = byteArrayOf(toByte(), (this ushr 8).toByte())