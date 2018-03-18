@file:Suppress("UsePropertyAccessSyntax")

package io.fdeitylink.kero.file

import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

import io.fdeitylink.util.toUInt

import io.fdeitylink.util.Quadruple
import io.fdeitylink.util.Quintuple

import io.fdeitylink.util.enumMapOf

import io.fdeitylink.kero.CHARSET

import io.fdeitylink.kero.map.PxPack
import io.fdeitylink.kero.map.Head
import io.fdeitylink.kero.map.BackgroundColor
import io.fdeitylink.kero.map.LayerProperties
import io.fdeitylink.kero.map.VisibilityType
import io.fdeitylink.kero.map.ScrollType
import io.fdeitylink.kero.map.TileLayer
import io.fdeitylink.kero.map.PxUnit

import io.fdeitylink.kero.validateName

internal fun PxPack.Companion.fromChannel(chan: SeekableByteChannel): PxPack {
    val head = Head.fromChannel(chan)
    val layers = enumMapOf(*TileLayer.Type.values().map { Pair(it, TileLayer.fromChannel(chan)) }.toTypedArray())

    val numUnits = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).let {
        chan.read(it)
        it.flip()
        it.getShort().toUInt()
    }
    val units = List(numUnits) { PxUnit.fromChannel(chan) }

    return PxPack(head, layers, units)
}

private fun Head.Companion.fromChannel(chan: ReadableByteChannel): Head {
    validateHeader(chan, HEADER_STRING, "PxPack")

    val desc = String.fromChannel(chan)
    validate(desc.toByteArray(CHARSET).size <= MAXIMUM_DESCRIPTION_LENGTH) { "description is too long (desc: $desc)" }

    val maps = List(NUMBER_OF_REFERENCED_MAPS) { nameFromChannel(chan, "map") }
            .let { (first, second, third, fourth) -> Quadruple(first, second, third, fourth) }

    val spritesheet = nameFromChannel(chan, "spritesheet")

    val unknownBytes = ByteBuffer.allocate(5).let {
        chan.read(it)
        it.array().let { (first, second, third, fourth, fifth) -> Quintuple(first, second, third, fourth, fifth) }
    }

    val bgColor = ByteBuffer.allocate(3).let {
        chan.read(it)
        it.flip()
        BackgroundColor(it.get(), it.get(), it.get())
    }

    val layerProperties = enumMapOf(
            *TileLayer.Type.values().map {
                val tileset = nameFromChannel(chan, "tileset")

                val (visibilityType, scrollType) = ByteBuffer.allocate(2).let {
                    chan.read(it)
                    it.flip()
                    Pair(VisibilityType(it.get().toUInt()), ScrollType.values()[it.get().toUInt()])
                }

                Pair(it, LayerProperties(tileset, visibilityType, scrollType))
            }.toTypedArray()
    )

    return Head(desc, maps, spritesheet, unknownBytes, bgColor, layerProperties)
}

private fun TileLayer.Companion.fromChannel(chan: SeekableByteChannel): TileLayer {
    validateHeader(chan, HEADER_STRING, "tile layer")

    val (width, height) = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).let {
        chan.read(it)
        it.flip()
        Pair(it.getShort().toUInt(), it.getShort().toUInt())
    }

    return if (width * height == 0) {
        TileLayer()
    }
    else {
        chan.position(chan.position() + 1) // TODO: Verify that this byte is always 0

        ByteBuffer.allocate(width * height).let {
            chan.read(it)
            TileLayer(width, height, it.array().map(Byte::toUInt))
        }
    }
}

private fun PxUnit.Companion.fromChannel(chan: SeekableByteChannel) =
        ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN).let {
            chan.read(it)
            it.flip()

            val flags = it.get()
            val type = it.get().toUInt()
            val unknownByte = it.get()

            val x = it.getShort().toUInt()
            val y = it.getShort().toUInt()

            val unknownBytes = Pair(it.get(), it.get())

            val name = nameFromChannel(chan, "unit")

            PxUnit(flags, type, unknownByte, x, y, unknownBytes, name)
        }

private fun nameFromChannel(chan: ReadableByteChannel, type: String) =
        String.fromChannel(chan).also { it.validateName(type) }

private fun String.Companion.fromChannel(chan: ReadableByteChannel): String {
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