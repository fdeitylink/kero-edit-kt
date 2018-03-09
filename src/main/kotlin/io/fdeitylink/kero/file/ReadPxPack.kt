@file:Suppress("UsePropertyAccessSyntax")

package io.fdeitylink.kero.file

import java.util.EnumMap

import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.ByteBuffer
import java.nio.ByteOrder

import kotlinx.collections.immutable.immutableListOf

import io.fdeitylink.util.toUInt

import io.fdeitylink.util.Quadruple
import io.fdeitylink.util.Quintuple

import io.fdeitylink.kero.map.PxPack
import io.fdeitylink.kero.map.Head
import io.fdeitylink.kero.map.BackgroundColor
import io.fdeitylink.kero.map.LayerProperties
import io.fdeitylink.kero.map.VisibilityType
import io.fdeitylink.kero.map.ScrollType
import io.fdeitylink.kero.map.TileLayer
import io.fdeitylink.kero.map.PxUnit

import io.fdeitylink.kero.Name
import io.fdeitylink.kero.MapName
import io.fdeitylink.kero.SpritesheetName
import io.fdeitylink.kero.TilesetName
import io.fdeitylink.kero.UnitName
import io.fdeitylink.kero.isValidName

internal fun PxPack.Companion.fromChannel(chan: SeekableByteChannel): PxPack {
    val head = Head.fromChannel(chan)
    val layers = EnumMap<TileLayer.Type, TileLayer>(TileLayer.Type::class.java).also { map ->
        TileLayer.Type.values().forEach { map[it] = TileLayer.fromChannel(chan) }
    }
    val numUnits = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).let {
        chan.read(it)
        it.flip()
        it.getShort().toUInt()
    }
    val units = immutableListOf(*Array(numUnits) { PxUnit.fromChannel(chan) })

    return PxPack(head, layers, units)
}

private fun Head.Companion.fromChannel(chan: ReadableByteChannel): Head {
    validateHeader(chan, HEADER_STRING, "PxPack")

    val desc = String.fromChannel(chan)
    validate(desc.length <= MAXIMUM_DESCRIPTION_LENGTH) { "description is too long (desc: $desc)" }

    val maps = List(NUMBER_OF_REFERENCED_MAPS) { Name.fromChannel(chan, ::MapName) }
            .let { (first, second, third, fourth) -> Quadruple(first, second, third, fourth) }

    val spritesheet = Name.fromChannel(chan, ::SpritesheetName)

    val unknownBytes = ByteBuffer.allocate(5).let {
        chan.read(it)
        it.array().let { (first, second, third, fourth, fifth) -> Quintuple(first, second, third, fourth, fifth) }
    }

    val bgColor = ByteBuffer.allocate(3).let {
        chan.read(it)
        it.flip()
        BackgroundColor(it.get(), it.get(), it.get())
    }

    val layerProperties = EnumMap<TileLayer.Type, LayerProperties>(TileLayer.Type::class.java).also { map ->
        TileLayer.Type.values().forEach {
            val tileset = Name.fromChannel(chan, ::TilesetName)

            val (visibilityType, scrollType) = ByteBuffer.allocate(2).let {
                chan.read(it)
                it.flip()
                Pair(VisibilityType(it.get()), ScrollType.values()[it.get().toUInt()])
            }

            map[it] = LayerProperties(tileset, visibilityType, scrollType)
        }
    }

    return Head(desc, maps, spritesheet, unknownBytes, bgColor, layerProperties)
}

private fun TileLayer.Companion.fromChannel(chan: SeekableByteChannel): TileLayer {
    validateHeader(chan, HEADER_STRING, "tile layer")

    val (width, height) = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).let {
        chan.read(it)
        it.flip()
        Pair(it.getShort(), it.getShort())
    }

    return if (width * height == 0) {
        TileLayer()
    }
    else {
        chan.position(chan.position() + 1) // TODO: Verify that this byte is always 0

        ByteBuffer.allocate(width.toUInt() * height.toUInt()).let {
            chan.read(it)
            TileLayer(width, height, immutableListOf(*it.array().toTypedArray()))
        }
    }
}

private fun PxUnit.Companion.fromChannel(chan: SeekableByteChannel) =
        ByteBuffer.allocate(9).order(ByteOrder.LITTLE_ENDIAN).let {
            chan.read(it)
            it.flip()

            val flag = it.get()
            val type = it.get()
            val unknownByte = it.get()

            val x = it.getShort()
            val y = it.getShort()

            val unknownBytes = Pair(it.get(), it.get())

            val name = Name.fromChannel(chan, ::UnitName)

            PxUnit(flag, type, unknownByte, x, y, unknownBytes, name)
        }

private inline fun <reified T : Name> Name.Companion.fromChannel(chan: ReadableByteChannel, ctor: (String) -> T) =
        String.fromChannel(chan).let {
            val type = T::class.simpleName!!.toLowerCase().removeSuffix("name")
            validate(it.isValidName()) { "$type name is invalid (name: $it)" }
            ctor(it)
        }