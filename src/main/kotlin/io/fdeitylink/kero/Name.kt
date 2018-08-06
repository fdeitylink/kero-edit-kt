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

/**
 * The maximum number of bytes a name may use when using the SJIS charset
 */
const val MAXIMUM_NAME_BYTE_LENGTH = 15

/**
 * Returns `true` if a filename contains no spaces and
 * its length as a byte array does not exceed [MAXIMUM_NAME_BYTE_LENGTH], `false` otherwise
 */
internal fun String.isValidName() = toByteArray(CHARSET).size <= MAXIMUM_NAME_BYTE_LENGTH && ' ' !in this

/**
 * Throws an [IllegalArgumentException] if `this` name is invalid (as per [isValidName])
 *
 * @param type What this name is used for (used for exception message)
 */
internal fun String.validateName(type: String = "") {
    require(toByteArray(CHARSET).size <= MAXIMUM_NAME_BYTE_LENGTH)
    { "length of $type name must be <= $MAXIMUM_NAME_BYTE_LENGTH" }
    require(' ' !in this) { "$type name may not contain spaces" }
}