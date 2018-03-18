package io.fdeitylink.kero.file

import io.fdeitylink.kero.CHARSET

import io.fdeitylink.kero.map.PxPack
import io.fdeitylink.kero.map.Head
import io.fdeitylink.kero.map.LayerProperties
import io.fdeitylink.kero.map.byte
import io.fdeitylink.kero.map.TileLayer
import io.fdeitylink.kero.map.isEmpty
import io.fdeitylink.kero.map.PxUnit

/**
 * Converts a [PxPack] to a [ByteArray] that can be directly written to a PxPack file.
 *
 * This comprises a fully valid PxPack file, so a fully valid PxPack file would be formed
 * by writing solely the result of this method to a file.
 */
internal fun PxPack.toBytes() =
        head.toBytes() +
        layers.values.map(TileLayer::toBytes).reduce(ByteArray::plus) +
        units.size.toShort().toBytes() +
        units.map(PxUnit::toBytes).reduce(ByteArray::plus)

/**
 * Converts a [Head] to a [ByteArray] that can be directly written to a PxPack file.
 *
 * This does not comprise a fully valid PxPack file, just part of it.
 */
private fun Head.toBytes(): ByteArray {
    fun LayerProperties.toBytes() = tileset.toBytes() + visibilityType.type + scrollType.byte

    return Head.HEADER_STRING.toByteArray() +

           description.toBytes() +

           maps.first.toBytes() +
           maps.second.toBytes() +
           maps.third.toBytes() +
           maps.fourth.toBytes() +

           spritesheet.toBytes() +

           unknownBytes.first +
           unknownBytes.second +
           unknownBytes.third +
           unknownBytes.fourth +
           unknownBytes.fifth +

           bgColor.red +
           bgColor.green +
           bgColor.blue +

           layerProperties[TileLayer.Type.BACKGROUND]!!.toBytes() +
           layerProperties[TileLayer.Type.MIDDLEGROUND]!!.toBytes() +
           layerProperties[TileLayer.Type.FOREGROUND]!!.toBytes()
}

/**
 * Converts a [TileLayer] into a [ByteArray] that can be directly written to a PxPack file.
 *
 * This does not comprise a fully valid PxPack file, just part of it.
 */
private fun TileLayer.toBytes() =
        TileLayer.HEADER_STRING.toByteArray() +
        if (isEmpty()) {
            ByteArray(4) { 0 }
        }
        else {
            width.toBytes() +
            height.toBytes() +
            0 + // TODO: Verify that this byte is always 0
            tiles
        }

/**
 * Converts a [PxUnit] into a [ByteArray] that can be directly written to a PxPack file.
 *
 * This does not comprise a fully valid PxPack file, just part of it.
 */
private fun PxUnit.toBytes() =
        byteArrayOf(
                flag,
                type,
                unknownByte
        ) +
        x.toBytes() +
        y.toBytes() +
        unknownBytes.first +
        unknownBytes.second +
        name.toBytes()

/**
 * Converts a [String] into an SJIS-encoded [ByteArray], with its length in bytes placed at the head of the array
 */
private fun String.toBytes() = toByteArray(CHARSET).let { byteArrayOf(it.size.toByte()) + it }