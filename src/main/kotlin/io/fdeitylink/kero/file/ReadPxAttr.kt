package io.fdeitylink.kero.file

import java.nio.channels.SeekableByteChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

import io.fdeitylink.util.toUInt

import io.fdeitylink.kero.tile.PxAttr

@Suppress("UsePropertyAccessSyntax")
internal fun PxAttr.Companion.fromChannel(chan: SeekableByteChannel): PxAttr {
    validateHeader(chan, PxAttr.HEADER_STRING, "PxAttr")

    ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).let {
        chan.read(it)
        it.flip()

        val width = it.getShort().toUInt()
        val height = it.getShort().toUInt()

        validate(width == PxAttr.WIDTH && height == PxAttr.HEIGHT) { "dimensions of PxAttr must be $WIDTH x $HEIGHT" }
    }

    chan.position(chan.position() + 1) // TODO: Verify that this byte is always 0

    return ByteBuffer.allocate(PxAttr.WIDTH * PxAttr.HEIGHT).let {
        chan.read(it)
        PxAttr(it.array().map(Byte::toUInt))
    }
}