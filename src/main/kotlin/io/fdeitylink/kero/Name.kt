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

package io.fdeitylink.kero

import io.fdeitylink.util.validate

/**
 * The maximum number of bytes a name may use when using the SJIS charset
 */
const val MAXIMUM_NAME_BYTE_LENGTH = 15

/**
 * Returns `true` if `this` name contains no spaces and
 * its length as a byte array in the SJIS charset does not exceed [MAXIMUM_NAME_BYTE_LENGTH], `false` otherwise
 */
internal fun String.isValidName() = this.toByteArray(CHARSET).size <= MAXIMUM_NAME_BYTE_LENGTH && ' ' !in this

/**
 * Constructs and throws an exception (using [exceptCtor]) if [name] contains spaces or
 * its length as a byte array in the SJIS charset exceeds [MAXIMUM_NAME_BYTE_LENGTH]
 *
 * @param type What this name is used for (used for exception message)
 *
 * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
 */
internal fun validateName(
        name: String,
        type: String = "",
        exceptCtor: (String) -> Exception = ::IllegalArgumentException
) {
    validate(name.toByteArray(CHARSET).size <= MAXIMUM_NAME_BYTE_LENGTH, exceptCtor)
    { "$type name length must be <= $MAXIMUM_NAME_BYTE_LENGTH (name: $name)" }

    validate(' ' !in name, exceptCtor) { "$type name may not contain spaces (name: $name)" }
}