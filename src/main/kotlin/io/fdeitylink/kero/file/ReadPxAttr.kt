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