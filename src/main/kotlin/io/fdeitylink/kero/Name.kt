package io.fdeitylink.kero

const val MAXIMUM_NAME_LENGTH = 15

/**
 * Returns `true` if a filename contains no spaces and
 * its length as a byte array does not exceed [MAXIMUM_NAME_LENGTH], `false` otherwise
 */
internal fun String.isValidName() = toByteArray(CHARSET).size <= MAXIMUM_NAME_LENGTH && ' ' !in this

/**
 * Throws an [IllegalArgumentException] if `this` name is invalid (as per [isValidName])
 */
internal fun String.validateName(type: String = "") {
    require(toByteArray(CHARSET).size <= MAXIMUM_NAME_LENGTH) { "length of $type name must be <= $MAXIMUM_NAME_LENGTH" }
    require(' ' !in this) { "$type name may not contain spaces" }
}