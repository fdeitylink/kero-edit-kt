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

import java.nio.channels.ReadableByteChannel
import java.nio.ByteBuffer

import io.fdeitylink.util.validate as genericValidate

import io.fdeitylink.kero.KERO_CHARSET

internal fun validateHeader(chan: ReadableByteChannel, header: String, type: String) {
    ByteBuffer.allocate(header.toByteArray().size).let {
        chan.read(it)
        val readHeader = String.fromKeroBytes(it.array())
        validate(readHeader == header) { "$type header does not match $header (header: $readHeader)" }
    }
}

internal inline fun validate(value: Boolean, lazyMessage: () -> Any) =
        genericValidate(value, ::ParseException, lazyMessage)

internal fun String.Companion.fromKeroBytes(bytes: ByteArray) = String(bytes, KERO_CHARSET)