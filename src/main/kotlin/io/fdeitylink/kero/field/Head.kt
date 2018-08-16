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
import java.util.Arrays

import javafx.collections.ObservableList

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper

import tornadofx.observable

import io.fdeitylink.util.enumMapOf

import io.fdeitylink.util.observable

import io.fdeitylink.kero.CHARSET

import io.fdeitylink.kero.isValidName
import io.fdeitylink.kero.validateName

/**
 * Represents the head of a PxPack field
 *
 * @constructor
 * Constructs a new [Head] object
 *
 * @param description Defaults to `""`
 * @param fields All values are defaulted to `""`
 * @param spritesheet Defaults to `""`
 * @param unknownBytes All values are defaulted to `0`
 * @param bgColor Defaults to `BackgroundColor(0, 0, 0)` (black)
 * @param layerMetadata Defaults to: [FOREGROUND][TileLayer.Type.FOREGROUND] `--> LayerMetaData()`,
 * [MIDDLEGROUND][TileLayer.Type.MIDDLEGROUND] `--> LayerMetadata(tileset = "")`,
 * [BACKGROUND][TileLayer.Type.BACKGROUND] `--> LayerMetadata(tileset = "", scrollType = ScrollType.THREE_FOURTHS)`
 *
 * @throws [IllegalArgumentException] if [fields] has a size other than [NUMBER_OF_REFERENCED_FIELDS],
 * [layerMetadata] does not have a key-value pair for every member of [TileLayer.Type], or if an argument
 * has an invalid value as per its corresponding property's documentation
 */
internal class Head(
        description: String = "",
        fields: Array<String> = Array(NUMBER_OF_REFERENCED_FIELDS) { "" },
        spritesheet: String = "",
        unknownBytes: ByteArray = ByteArray(NUMBER_OF_UNKNOWN_BYTES) { 0 },
        bgColor: BackgroundColor = BackgroundColor(0, 0, 0),
        layerMetadata: Map<TileLayer.Type, LayerMetadata> = enumMapOf(
                TileLayer.Type.FOREGROUND to LayerMetadata(),
                TileLayer.Type.MIDDLEGROUND to LayerMetadata(tileset = ""),
                TileLayer.Type.BACKGROUND to LayerMetadata(tileset = "", scrollType = ScrollType.THREE_FOURTHS)
        )
) {
    /**
     * A string used by the developer as a description of this PxPack field
     *
     * Non-essential and not used by the game
     *
     * @throws [IllegalArgumentException] if set to a value whose length in bytes when using the SJIS charset
     * is greater than [MAXIMUM_DESCRIPTION_BYTE_LENGTH]
     */
    var description: String = description
        set(value) {
            validateDescription(value)
            field = value
        }

    val descriptionProperty = observable(Head::description)

    /**
     * A set of four fields referenced by this PxPack field
     *
     * Field names can be empty (i.e. `""`)
     *
     * @throws [UnsupportedOperationException] if any attempts to change the size of this list are made
     */
    val fields: ObservableList<String> = Arrays.asList(*fields).observable()

    val fieldsProperty: ReadOnlyListProperty<String> = ReadOnlyListWrapper(this, "fields", this.fields)

    /**
     * The spritesheet used for rendering the [units][PxUnit] of this PxPack field
     *
     * @throws [IllegalArgumentException] if set to an invalid name (as per [validateName])
     */
    var spritesheet: String = spritesheet
        set(value) {
            validateName(value, "spritesheet")
            field = value
        }

    val spritesheetProperty = observable(Head::spritesheet)

    /**
     * A set of five bytes whose purpose is unknown
     *
     * @throws [IllegalArgumentException] if set to a [ByteArray] whose size is not equal to [NUMBER_OF_UNKNOWN_BYTES]
     */
    var unknownBytes: ByteArray = unknownBytes
        set(value) {
            validateUnknownBytes(value)
            field = value
        }

    // TODO: Consider changing type to JavaFX Color
    /**
     * The background color of this PxPack field
     */
    @Suppress("CanBePrimaryConstructorProperty")
    var bgColor: BackgroundColor = bgColor

    val bgColorProperty = observable(Head::bgColor)

    // TODO: Consider replacing with 3 ObservableMaps, one for each component of LayerMetadata
    /**
     * A set of three tile layer metadata sets, where each corresponds to tile layer in this PxPack field
     */
    @Suppress("CanBePrimaryConstructorProperty")
    val layerMetadata: Map<TileLayer.Type, LayerMetadata> = layerMetadata

    init {
        validateDescription(description)
        validateFields(fields)
        validateName(spritesheet, "spritesheet")
        validateUnknownBytes(unknownBytes)
        validateLayerMetadata(layerMetadata)
    }

    override fun equals(other: Any?) =
            (this === other) ||
            (other is Head &&
             description == other.description &&
             fields == other.fields &&
             spritesheet == other.spritesheet &&
             unknownBytes.contentEquals(other.unknownBytes) &&
             bgColor == other.bgColor &&
             layerMetadata == other.layerMetadata)

    override fun hashCode() = Objects.hash(description, fields, spritesheet, unknownBytes, bgColor, layerMetadata)

    override fun toString() =
            "Head(" +
            "description='$description'," +
            "fields=$fields," +
            "spritesheet='$spritesheet'," +
            "unknownBytes=${unknownBytes.contentToString()}," +
            "bgColor=$bgColor," +
            "layerMetadata=$layerMetadata" +
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

        fun String.isValidDescription() = this.toByteArray(CHARSET).size <= MAXIMUM_DESCRIPTION_BYTE_LENGTH

        fun validateDescription(description: String) =
                require(description.toByteArray(CHARSET).size <= MAXIMUM_DESCRIPTION_BYTE_LENGTH)
                { "description bytes length must be <= $MAXIMUM_DESCRIPTION_BYTE_LENGTH (description: $description)" }

        fun Array<String>.isValidFields() = this.size == NUMBER_OF_REFERENCED_FIELDS && this.all(String::isValidName)

        fun validateFields(fields: Array<String>) {
            require(fields.size == NUMBER_OF_REFERENCED_FIELDS)
            { "fields.size != $NUMBER_OF_REFERENCED_FIELDS (size: ${fields.size})" }

            fields.forEach { validateName(it, "field") }
        }

        fun ByteArray.isValidUnknownBytes() = this.size == NUMBER_OF_UNKNOWN_BYTES

        fun validateUnknownBytes(unknownBytes: ByteArray) =
                require(unknownBytes.size == NUMBER_OF_UNKNOWN_BYTES)
                { "unknownBytes.size != $NUMBER_OF_UNKNOWN_BYTES (size: ${unknownBytes.size})" }

        fun Map<TileLayer.Type, LayerMetadata>.isValidLayerMetadata() =
                this.size == TileLayer.NUMBER_OF_TILE_LAYERS &&
                this[TileLayer.Type.FOREGROUND]!!.tileset.isNotEmpty()

        fun validateLayerMetadata(layerMetadata: Map<TileLayer.Type, LayerMetadata>) {
            require(layerMetadata.size == TileLayer.NUMBER_OF_TILE_LAYERS)
            { "layerMetadata.size != ${TileLayer.NUMBER_OF_TILE_LAYERS} (size: ${layerMetadata.size})" }

            require(layerMetadata[TileLayer.Type.FOREGROUND]!!.tileset.isNotEmpty())
            { "foreground tileset name may not be empty" }
        }
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
 * Represents the metadata for a [tile layer][TileLayer] in a PxPack field
 *
 * @constructor
 * Constructs a new [LayerMetadata] object
 *
 * @param tileset Defaults to `"mpt00"`
 * @param visibilityType Defaults to `2`
 * @param scrollType Defaults to [ScrollType.NORMAL]
 *
 * @throws [IllegalArgumentException] if an argument has an invalid value as per its corresponding property's documentation
 */
internal class LayerMetadata(
        tileset: String = "mpt00",
        visibilityType: Byte = 2,
        scrollType: ScrollType = ScrollType.NORMAL
) {
    /**
     * The name of the tileset used to display a tile layer in a PxPack field
     *
     * @throws [IllegalArgumentException] if set to an invalid name (as per [validateName])
     */
    var tileset: String = tileset
        set(value) {
            validateName(value)
            field = value
        }

    val tilesetProperty = observable(LayerMetadata::tileset)

    /**
     * Potentially represents some kind of visibility setting used to display a tile layer in a PxPack field
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
     */
    @Suppress("CanBePrimaryConstructorProperty")
    var visibilityType: Byte = visibilityType
    /*set(value) {
        validateVisibilityType(value)
        field = value
    }*/

    /**
     * The type of scrolling used to display a tile layer in a PxPack field
     */
    @Suppress("CanBePrimaryConstructorProperty")
    var scrollType: ScrollType = scrollType

    val scrollTypeProperty = observable(LayerMetadata::scrollType)

    init {
        validateName(tileset)
        //validateVisibilityType(visibilityType)
    }

    override fun equals(other: Any?) =
            (this === other) ||
            (other is LayerMetadata &&
             tileset == other.tileset &&
             visibilityType == other.visibilityType &&
             scrollType == other.scrollType)

    override fun hashCode() = Objects.hash(tileset, visibilityType, scrollType)

    override fun toString() =
            "LayerMetadata(" +
            "tileset='$tileset'," +
            "visibilityType=$visibilityType," +
            "scrollType=$scrollType" +
            ")"

    companion object {
        /*
         * Since I'm not really sure what this byte is for, it doesn't seem right to validate it quite yet
         * This program doesn't give any means to modify it anyway, so it can't be made invalid unless already
         * invalid in the source file
         */
        /**
         * The supposed valid range for [visibilityType] to occupy
         */
        val VISIBILITY_TYPE_RANGE = 0..0xFF //0..32

        fun Byte.isValidVisibilityType() = this in VISIBILITY_TYPE_RANGE

        fun validateVisibilityType(type: Byte) =
                require(type in VISIBILITY_TYPE_RANGE)
                { "visibility type must be in range $VISIBILITY_TYPE_RANGE (type: $type)" }
    }
}

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