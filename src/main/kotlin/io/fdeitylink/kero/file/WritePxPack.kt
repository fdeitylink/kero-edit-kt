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

package io.fdeitylink.kero.file

import io.fdeitylink.kero.KERO_CHARSET

import io.fdeitylink.kero.field.PxPack
import io.fdeitylink.kero.field.Head
import io.fdeitylink.kero.field.toByte
import io.fdeitylink.kero.field.TileLayer
import io.fdeitylink.kero.field.isEmpty
import io.fdeitylink.kero.field.PxUnit

/**
 * Converts a [PxPack] to a [ByteArray] that can be directly written to a PxPack file.
 *
 * This comprises a fully valid PxPack file, so a fully valid PxPack file would be formed
 * by writing solely the result of this method to a file.
 */
internal fun PxPack.toBytes() =
        head.toBytes() +
        layers.toSortedMap().values.map(TileLayer::toBytes).reduce(ByteArray::plus) +
        units.size.toShort().toBytes() +
        units.map(PxUnit::toBytes).reduce(ByteArray::plus)

/**
 * Converts a [Head] to a [ByteArray] that can be directly written to a PxPack file.
 *
 * This does not comprise a fully valid PxPack file, just part of it.
 */
private fun Head.toBytes(): ByteArray {
    val tilesetBytes = tilesets.values.map { it.value.toKeroBytes() }

    val visibilityTypeBytes = visibilityTypes.values

    val scrollTypeBytes = scrollTypes.values.map { it.value.toByte() }

    val metadata =
            tilesetBytes
                    .zip(visibilityTypeBytes)
                    .zip(scrollTypeBytes) { (t, v), s -> t + v + s }
                    .reduce(ByteArray::plus)

    return Head.HEADER_STRING.toByteArray() +

           description.toKeroBytes() +

           fields.map { it.value.toKeroBytes() }.reduce(ByteArray::plus) +

           spritesheet.toKeroBytes() +

           unknownBytes +

           bgColor.red +
           bgColor.green +
           bgColor.blue +

           metadata
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
            width.toShort().toBytes() +
            height.toShort().toBytes() +
            0 + // TODO: Verify that this byte is always 0
            this.map(Int::toByte)
        }

/**
 * Converts a [PxUnit] into a [ByteArray] that can be directly written to a PxPack file.
 *
 * This does not comprise a fully valid PxPack file, just part of it.
 */
private fun PxUnit.toBytes() =
        byteArrayOf(
                flags,
                type.toByte(),
                unknownByte
        ) +
        x.toShort().toBytes() +
        y.toShort().toBytes() +
        unknownBytes.first +
        unknownBytes.second +
        name.toKeroBytes()

/**
 * Converts a [String] into an SJIS-encoded [ByteArray], with its length in bytes placed at the head of the array
 */
private fun String.toKeroBytes() = toByteArray(KERO_CHARSET).let { byteArrayOf(it.size.toByte(), *it) }