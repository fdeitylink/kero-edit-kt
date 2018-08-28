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

package io.fdeitylink.kero.field

import java.util.Objects

import java.util.Collections
import java.util.SortedMap

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty

import javafx.beans.property.StringProperty

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

import tornadofx.getValue
import tornadofx.setValue

import tornadofx.toProperty

import io.fdeitylink.util.enumMapOf

import io.fdeitylink.util.validate
import io.fdeitylink.util.validateSize

import io.fdeitylink.util.validatedStringProperty

import io.fdeitylink.kero.KERO_CHARSET

import io.fdeitylink.kero.isValidName
import io.fdeitylink.kero.validateName

/**
 * Represents the head of a PxPack field
 *
 * @constructor
 * Constructs a new [Head] object, using each argument to initialize the corresponding properties
 *
 * @param description Defaults to `""`
 * @param fields All values are defaulted to `""`
 * @param spritesheet Defaults to `""`
 * @param unknownBytes All values are defaulted to `0`
 * @param bgColor Defaults to `BackgroundColor(0, 0, 0)` (black)
 * @param tilesets Defaults to: [FOREGROUND][TileLayer.Type.FOREGROUND] `--> "mpt00"`,
 * [MIDDLEGROUND][TileLayer.Type.MIDDLEGROUND] `--> ""`,
 * [BACKGROUND][TileLayer.Type.BACKGROUND] `--> ""`
 * @param visibilityTypes All values are defaulted to `2`
 * @param scrollTypes Defaults to: [FOREGROUND][TileLayer.Type.FOREGROUND] `-->` [NORMAL][ScrollType.NORMAL],
 * [MIDDLEGROUND][TileLayer.Type.MIDDLEGROUND] `-->` [NORMAL][ScrollType.NORMAL],
 * [BACKGROUND][TileLayer.Type.BACKGROUND] `-->` [THREE_FOURTHS][ScrollType.THREE_FOURTHS]
 *
 * @throws [IllegalArgumentException] if [fields] is invalid as per [isValidFields],
 * [tilesets] is invalid as per [isValidTilesets], [visibilityTypes] is invalid as per [isValidVisibilityTypes],
 * [scrollTypes] is invalid as per [isValidScrollTypes],
 * or if an argument has an invalid value as per its corresponding property's documentation
 */
internal class Head(
        description: String = "",
        fields: List<String> = List(NUMBER_OF_REFERENCED_FIELDS) { "" },
        spritesheet: String = "",
        unknownBytes: ByteArray = ByteArray(NUMBER_OF_UNKNOWN_BYTES) { 0 },
        bgColor: BackgroundColor = BackgroundColor(0, 0, 0),
        tilesets: Map<TileLayer.Type, String> = enumMapOf(
                TileLayer.Type.FOREGROUND to "mpt00",
                TileLayer.Type.MIDDLEGROUND to "",
                TileLayer.Type.BACKGROUND to ""
        ),
        visibilityTypes: Map<TileLayer.Type, Byte> = TileLayer.Type.values().associate { it to 2.toByte() },
        scrollTypes: Map<TileLayer.Type, ScrollType> = enumMapOf(
                TileLayer.Type.FOREGROUND to ScrollType.NORMAL,
                TileLayer.Type.MIDDLEGROUND to ScrollType.NORMAL,
                TileLayer.Type.BACKGROUND to ScrollType.THREE_FOURTHS
        )
) {
    init {
        validateDescription(description)
        validateFields(fields)
        validateName(spritesheet, "spritesheet")
        validateUnknownBytes(unknownBytes)
        validateTilesets(tilesets)
        validateVisibilityTypes(visibilityTypes)
        validateScrollTypes(scrollTypes)
    }

    val descriptionProperty = validatedStringProperty(description) { validateDescription(it) }

    /**
     * A string used by the developer as a description of this PxPack field
     *
     * Non-essential and not used by the game
     *
     * @throws [IllegalArgumentException] if an attempt is made to set it to an invalid value as per [isValidDescription]
     */
    var description: String by descriptionProperty

    // TODO: Consider using Collections.unmodifiableList()
    /**
     * A list of four fields referenced by this PxPack field
     *
     * Field names can be empty (i.e. `""`)
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidName]
     */
    val fields: ImmutableList<StringProperty> =
            fields.map { validatedStringProperty(it) { validateName(it) } }.toImmutableList()

    val spritesheetProperty = validatedStringProperty(spritesheet) { validateName(it, "spritesheet") }

    /**
     * The spritesheet used for rendering the [units][PxUnit] of this PxPack field
     *
     * @throws [IllegalArgumentException] if an attempt is made to set to an invalid value as per [isValidName]
     */
    var spritesheet: String by spritesheetProperty

    /**
     * A set of five bytes whose purpose is unknown
     *
     * @throws [IllegalArgumentException] if an attempt is made to set it to an invalid value as per [isValidUnknownBytes]
     */
    var unknownBytes: ByteArray = unknownBytes
        set(value) {
            validateUnknownBytes(value)
            field = value
        }

    val bgColorProperty: ObjectProperty<BackgroundColor> = SimpleObjectProperty(bgColor)

    // TODO: Consider changing type to JavaFX Color
    /**
     * The background color of this PxPack field
     */
    var bgColor: BackgroundColor by bgColorProperty

    /**
     * A set of names for tilesets used to render each tile layer in a PxPack file
     *
     * The order of this map is derived from the natural ordering of [TileLayer.Type]
     *
     * @throws [IllegalArgumentException] if an attempt is made to set a value to an invalid value as per [isValidName]
     * @throws [UnsupportedOperationException] if an attempt is made to change the contents of this map
     */
    val tilesets: SortedMap<TileLayer.Type, StringProperty> =
            Collections.unmodifiableSortedMap(
                    tilesets.mapValues { (_, name) -> validatedStringProperty(name) { validateName(name) } }.toSortedMap()
            )

    /**
     * A set of bytes, where each potentially represents some kind of visibility setting used to display a tile layer
     * in a PxPack field
     *
     * The order of this map is derived from the natural ordering of [TileLayer.Type]
     *
     * Still not sure whether or not the byte in the file actually represents any kind of visibility toggle
     * or setting, but when modifying the byte in a file the visibility of a particular layer would be changed.
     *
     * When set to:
     *
     * * `0`, the layer becomes invisible
     *
     * * `2`, the layer is visible (the byte usually holds this value)
     *
     * * `1` or `3..32`, the wrong tiles are pulled but from the correct tileset (maybe an offset is applied?)
     *
     * * `33..`, the game crashes
     *
     * This class will probably eventually be replaced with an enum class
     *
     * @throws [UnsupportedOperationException] if an attempt is made to change the contents of this map
     */
    val visibilityTypes: SortedMap<TileLayer.Type, Byte> =
            Collections.unmodifiableSortedMap(visibilityTypes.toSortedMap())

    /**
     * A set of [ScrollTypes][ScrollType] for each tile layer in a PxPack file
     *
     * The order of this map is derived from the natural ordering of [TileLayer.Type]
     *
     * @throws [UnsupportedOperationException] if an attempt is made to change the contents of this map
     */
    val scrollTypes: SortedMap<TileLayer.Type, ObjectProperty<ScrollType>> =
            Collections.unmodifiableSortedMap(scrollTypes.mapValues { (_, type) -> type.toProperty() }.toSortedMap())

    override fun equals(other: Any?) =
            (this === other) ||
            (other is Head &&
             description == other.description &&
             fields == other.fields &&
             spritesheet == other.spritesheet &&
             unknownBytes.contentEquals(other.unknownBytes) &&
             bgColor == other.bgColor &&
             tilesets == other.tilesets &&
             visibilityTypes == other.visibilityTypes &&
             scrollTypes == other.scrollTypes)

    override fun hashCode() =
            Objects.hash(description, fields, spritesheet, unknownBytes, bgColor, tilesets, visibilityTypes, scrollTypes)

    override fun toString() =
            "Head(" +
            "description='$description'," +
            "fields=$fields," +
            "spritesheet='$spritesheet'," +
            "unknownBytes=${unknownBytes.contentToString()}," +
            "bgColor=$bgColor," +
            "tilesets=$tilesets," +
            "visibilityTypes=$visibilityTypes," +
            "scrollTypes=$scrollTypes" +
            ")"

    companion object {
        /**
         * The string that marks the beginning of the head in a PxPack file,
         * or in other words marks the beginning of a PxPack file
         */
        const val HEADER_STRING = "PXPACK121127a**\u0000"

        /**
         * The maximum number of bytes the description string in a PxPack field may use when using the SJIS charset
         */
        const val MAXIMUM_DESCRIPTION_BYTE_LENGTH = 31

        /**
         * The number of fields that are referenced by a PxPack field
         */
        const val NUMBER_OF_REFERENCED_FIELDS = 4

        /**
         * The number of contiguous bytes in the head of a PxPack field whose purpose is currently unknown
         */
        const val NUMBER_OF_UNKNOWN_BYTES = 5

        /**
         * Returns `true` if the length of `this` description as a byte array in the SJIS charset exceeds
         * [MAXIMUM_DESCRIPTION_BYTE_LENGTH], `false` otherwise
         */
        fun String.isValidDescription() = this.toByteArray(KERO_CHARSET).size <= MAXIMUM_DESCRIPTION_BYTE_LENGTH

        /**
         * Constructs and throws an exception (using [exceptCtor]) if the length of [description] as a byte array
         * in the SJIS chasrset exceeds [MAXIMUM_DESCRIPTION_BYTE_LENGTH]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateDescription(
                description: String,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) =
                validate(description.toByteArray(KERO_CHARSET).size <= MAXIMUM_DESCRIPTION_BYTE_LENGTH, exceptCtor)
                { "description bytes length must be <= $MAXIMUM_DESCRIPTION_BYTE_LENGTH (description: $description)" }

        /**
         * Returns `true` if the size of `this` set of fields equals [NUMBER_OF_REFERENCED_FIELDS] and all of its
         * values are valid as per [isValidName], `false` otherwise
         */
        fun List<String>.isValidFields() = this.size == NUMBER_OF_REFERENCED_FIELDS && this.all(String::isValidName)

        /**
         * Constructs and throws an exception (using [exceptCtor]) if the size of [fields] does not equal
         * [NUMBER_OF_REFERENCED_FIELDS] or any of its values are invalid as per [isValidName]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateFields(fields: List<String>, exceptCtor: (String) -> Exception = ::IllegalArgumentException) {
            validateSize(fields, "fields", NUMBER_OF_REFERENCED_FIELDS, exceptCtor)

            fields.forEach { validateName(it, "field", exceptCtor) }
        }

        /**
         * Returns `true` if the size of `this` byte array equals [NUMBER_OF_UNKNOWN_BYTES], `false` otherwise
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun ByteArray.isValidUnknownBytes() = this.size == NUMBER_OF_UNKNOWN_BYTES

        /**
         * Constructs and throws an exception (using [exceptCtor]) if the size of [unknownBytes] does not equal
         * [NUMBER_OF_UNKNOWN_BYTES]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateUnknownBytes(
                unknownBytes: ByteArray,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) = validateSize(unknownBytes, "unknownBytes", NUMBER_OF_UNKNOWN_BYTES, exceptCtor)

        /**
         * Returns `true` if `this` set of tileset names contains once instance for every tile layer in a PxPack field,
         * the tileset for the [foreground][TileLayer.Type.FOREGROUND] is not empty,
         * and all tileset names are valid as per [isValidName], `false` otherwise
         */
        fun Map<TileLayer.Type, String>.isValidTilesets() =
                this.size == TileLayer.NUMBER_OF_TILE_LAYERS &&
                this[TileLayer.Type.FOREGROUND]!!.isNotEmpty() &&
                this.values.all(String::isValidName)

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [tilesets] does not contain an entry for every
         * tile layer in a PxPack field, the tileset for the [foreground][TileLayer.Type.FOREGROUND] is empty,
         * or any of the tileset names are invalid as per [isValidName]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateTilesets(
                tilesets: Map<TileLayer.Type, String>,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) {
            validateSize(tilesets, "tilesets", TileLayer.NUMBER_OF_TILE_LAYERS, exceptCtor)

            validate(tilesets[TileLayer.Type.FOREGROUND]!!.isNotEmpty(), exceptCtor)
            { "foreground tileset name may not be empty" }

            tilesets.values.forEach { validateName(it, "tileset", exceptCtor) }
        }

        /**
         * Returns `true` if `this` set of visibility types contains once instance for every tile layer
         * in a PxPack field, `false` otherwise
         */
        fun Map<TileLayer.Type, Byte>.isValidVisibilityTypes() = this.size == TileLayer.NUMBER_OF_TILE_LAYERS

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [visibilityTypes] does not contain an entry
         * for every tile layer in a PxPack field
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateVisibilityTypes(
                visibilityTypes: Map<TileLayer.Type, Byte>,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) = validateSize(visibilityTypes, "visibilityTypes", TileLayer.NUMBER_OF_TILE_LAYERS, exceptCtor)

        /**
         * Returns `true` if `this` set of [scroll types][ScrollType] contains one instance for everytile layer in a
         * PxPack field, `false` otherwise
         */
        fun Map<TileLayer.Type, ScrollType>.isValidScrollTypes() = this.size == TileLayer.NUMBER_OF_TILE_LAYERS

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [scrollTypes] does not contain an entry
         * for every tile layer in a PxPack field
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateScrollTypes(
                scrollTypes: Map<TileLayer.Type, ScrollType>,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) = validateSize(scrollTypes, "scrollTypes", TileLayer.NUMBER_OF_TILE_LAYERS, exceptCtor)
    }
}

// TODO: Consider storing Int instead of Byte
/**
 * Represents the background color of a PxPack field
 *
 * Only the RGB values of the color are stored, so it must be opaque
 */
internal data class BackgroundColor(
        /**
         * The red component of the background color
         *
         * Though [Byte] is signed, this value is really unsigned, so upcast to an [Int] as necessary
         */
        val red: Byte,

        /**
         * The green component of the background color
         *
         * Though [Byte] is signed, this value is really unsigned, so upcast to an [Int] as necessary
         */
        val green: Byte,

        /**
         * The blue component of the background color
         *
         * Though [Byte] is signed, this value is really unsigned, so upcast to an [Int] as necessary
         */
        val blue: Byte
)

/**
 * Represents the scrolling type of a tile layer in a PxPack field
 */
internal enum class ScrollType {
    NORMAL,

    THREE_FOURTHS,

    HALF,

    QUARTER,

    EIGHTH,

    ZERO,

    H_THREE_FOURTHS,

    H_HALF,

    H_QUARTER,

    V0_HALF;

    companion object {
        /**
         * The number of scroll types that exist
         */
        const val NUMBER_OF_SCROLL_TYPES = 10
    }
}

/**
 * Returns a [Byte] representing `this` [ScrollType's][ScrollType] index in the scroll.txt file
 */
internal fun ScrollType.toByte() = ordinal.toByte()