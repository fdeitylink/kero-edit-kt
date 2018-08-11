/*
 * Copyright 2018 Brian "FDeityLink" Christian
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

@file:Suppress("UsePropertyAccessSyntax")

package io.fdeitylink.kero.file

import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

import io.fdeitylink.util.toUInt

import io.fdeitylink.util.toEnumMap

import io.fdeitylink.kero.CHARSET

import io.fdeitylink.kero.map.PxPack
import io.fdeitylink.kero.map.Head
import io.fdeitylink.kero.map.BackgroundColor
import io.fdeitylink.kero.map.LayerProperties
import io.fdeitylink.kero.map.ScrollType
import io.fdeitylink.kero.map.TileLayer
import io.fdeitylink.kero.map.PxUnit

import io.fdeitylink.kero.validateName

internal fun PxPack.Companion.fromChannel(chan: SeekableByteChannel): PxPack {
    val head = Head.fromChannel(chan)
    val layers = TileLayer.Type.values().associate { it to TileLayer.fromChannel(chan) }.toEnumMap()

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

    val description = String.fromChannel(chan)
    validate(description.toByteArray(CHARSET).size <= MAXIMUM_DESCRIPTION_LENGTH)
    { "description length must be <= $MAXIMUM_DESCRIPTION_LENGTH (description: $description)" }

    val maps = List(NUMBER_OF_REFERENCED_MAPS) { nameFromChannel(chan, "map") }

    val spritesheet = nameFromChannel(chan, "spritesheet")

    val unknownBytes = ByteBuffer.allocate(NUMBER_OF_UNKNOWN_BYTES).let {
        chan.read(it)
        it.array().toList()
    }

    val bgColor = ByteBuffer.allocate(3).let {
        chan.read(it)
        it.flip()
        BackgroundColor(it.get(), it.get(), it.get())
    }

    val layerProperties =
            TileLayer.Type.values().associate {
                val tileset = nameFromChannel(chan, "tileset")

                val (visibilityType, scrollType) = ByteBuffer.allocate(2).let {
                    chan.read(it)
                    it.flip()
                    Pair(it.get(), ScrollType.values()[it.get().toUInt()])
                }

                it to LayerProperties(tileset, visibilityType, scrollType)
            }.toEnumMap()

    return Head(description, maps, spritesheet, unknownBytes, bgColor, layerProperties)
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