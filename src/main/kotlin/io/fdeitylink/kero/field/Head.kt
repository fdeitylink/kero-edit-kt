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

import javafx.collections.ObservableList

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyListWrapper

import javafx.beans.property.StringProperty

import tornadofx.observable

import io.fdeitylink.util.enumMapOf

import io.fdeitylink.util.observable

/**
 * Represents the head of a PxPack field
 */
internal data class Head(
        /**
         * A string used by the developer as a description of this PxPack field
         *
         * Non-essential and not used by the game
         *
         * Defaults to `""`
         */
        var description: String = "",

        /**
         * A set of up to four fields referenced by this PxPack field
         *
         * All are defaulted to `""`
         */
        val fields: ObservableList<String> = MutableList(NUMBER_OF_REFERENCED_FIELDS) { "" }.observable(),

        /**
         * The spritesheet used for rendering the [units][PxUnit] of this PxPack field
         *
         * Defaults to `""`
         */
        var spritesheet: String = "",

        /**
         * A set of five bytes whose purpose is unknown
         *
         * All are defaulted to `0`
         */
        var unknownBytes: MutableList<Byte> = MutableList(NUMBER_OF_UNKNOWN_BYTES) { 0.toByte() },

        // TODO: Consider changing type to JavaFX Color
        /**
         * The background color of this PxPack field
         *
         * Defaults to `BackgroundColor(0, 0, 0)`
         */
        var bgColor: BackgroundColor = BackgroundColor(0, 0, 0),

        // TODO: Consider replacing with 3 ObservableMaps, one for each component of LayerMetadata
        /**
         * A set of three tile layer property sets, where each corresponds to tile layer in this PxPack field
         *
         * Foreground defaults to `LayerMetaData()`,
         * middleground defaults to `LayerMetadata(tileset = "")`,
         * background defaults to `LayerMetadata(tileset = "", scrollType = ScrollType.THREE_FOURTHS)`
         */
        val layerMetadata: Map<TileLayer.Type, LayerMetadata> = enumMapOf(
                TileLayer.Type.FOREGROUND to LayerMetadata(),
                TileLayer.Type.MIDDLEGROUND to LayerMetadata(tileset = ""),
                TileLayer.Type.BACKGROUND to LayerMetadata(tileset = "", scrollType = ScrollType.THREE_FOURTHS)
        )
) {
    private val descriptionProperty = observable(Head::description)

    fun descriptionProperty(): StringProperty = descriptionProperty

    private val fieldsProperty = ReadOnlyListWrapper(this, "fields", fields)

    fun fieldsProperty(): ReadOnlyListProperty<String> = fieldsProperty

    private val spritesheetProperty = observable(Head::spritesheet)

    fun spritesheetProperty(): StringProperty = spritesheetProperty

    private val bgColorProperty = observable(Head::bgColor)

    fun bgColorProperty() = bgColorProperty

    companion object {
        /**
         * The string that marks the beginning of the head in a PxPack file,
         * or in other words marks the beginning of a PxPack file
         */
        const val HEADER_STRING = "PXPACK121127a**\u0000"

        /**
         * The maximum length of the description string in a PxPack field
         */
        const val MAXIMUM_DESCRIPTION_LENGTH = 31

        /**
         * The number of fields that are referenced by a PxPack field
         */
        const val NUMBER_OF_REFERENCED_FIELDS = 4

        /**
         * The number of contiguous bytes in the head of a PxPack field whose purpose is currently unknown
         */
        const val NUMBER_OF_UNKNOWN_BYTES = 5

        /*
         * Making an invoke function on the companion object rather than a secondary constructor makes the expression
         * `Head()` call the primary constructor of the class and eliminates an ambiguity error between two constructors.
         * There is no real difference between the primary constructor and this function other than some of the parameter
         * types being more general for this function, so even if this function were chosen over the primary constructor,
         * it would make no difference.
         */
        operator fun invoke(
                description: String = "",
                fields: List<String> = List(NUMBER_OF_REFERENCED_FIELDS) { "" },
                spritesheet: String = "",
                unknownBytes: List<Byte> = List(NUMBER_OF_UNKNOWN_BYTES) { 0.toByte() },
                bgColor: BackgroundColor = BackgroundColor(0, 0, 0),
                layerMetadata: Map<TileLayer.Type, LayerMetadata> = enumMapOf(
                        TileLayer.Type.FOREGROUND to LayerMetadata(),
                        TileLayer.Type.MIDDLEGROUND to LayerMetadata(tileset = ""),
                        TileLayer.Type.BACKGROUND to LayerMetadata(tileset = "", scrollType = ScrollType.THREE_FOURTHS)
                )
        ) = Head(
                description,
                fields.toMutableList().observable(),
                spritesheet,
                unknownBytes.toMutableList(),
                bgColor,
                layerMetadata
        )

        fun String.isValidDescription() = this.length <= MAXIMUM_DESCRIPTION_LENGTH

        fun String.validateDescription() =
                require(this.length <= MAXIMUM_DESCRIPTION_LENGTH)
                { "description length must be <= $MAXIMUM_DESCRIPTION_LENGTH (description: $this)" }

        fun List<String>.isValidFields() = this.size == NUMBER_OF_REFERENCED_FIELDS

        fun List<String>.validateFields() =
                require(this.size == NUMBER_OF_REFERENCED_FIELDS)
                { "fields.size != $NUMBER_OF_REFERENCED_FIELDS (size: ${this.size})" }

        fun List<Byte>.isValidUnknownBytes() = this.size == NUMBER_OF_UNKNOWN_BYTES

        fun List<Byte>.validateUnknownBytes() =
                require(this.size == NUMBER_OF_UNKNOWN_BYTES)
                { "unknownBytes.size != $NUMBER_OF_UNKNOWN_BYTES (size: ${this.size})" }

        fun Map<TileLayer.Type, LayerMetadata>.isValidLayerMetadata() =
                this.size == TileLayer.NUMBER_OF_TILE_LAYERS &&
                this[TileLayer.Type.FOREGROUND]!!.tileset.isNotEmpty()

        fun Map<TileLayer.Type, LayerMetadata>.validateLayerMetadata() {
            require(this.size == TileLayer.NUMBER_OF_TILE_LAYERS)
            { "layerMetadata.size != ${TileLayer.NUMBER_OF_TILE_LAYERS} (size: ${this.size})" }
            require(this[TileLayer.Type.FOREGROUND]!!.tileset.isNotEmpty())
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
 */
internal data class LayerMetadata(
        /**
         * The name of the tileset used to display a tile layer in a PxPack field
         *
         * Defaults to `"mpt00"`
         */
        var tileset: String = "mpt00",

        /**
         * Potentially represents some kind of visibility setting used to display a tile layer in a PxPack field
         *
         * Defaults to `2`
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
        var visibilityType: Byte = 2,

        /**
         * The type of scrolling used to display a tile layer in a PxPack field
         *
         * Defaults to [ScrollType.NORMAL]
         */
        var scrollType: ScrollType = ScrollType.NORMAL
) {
    private val tilesetProperty = observable(LayerMetadata::tileset)

    fun tilesetProperty() = tilesetProperty

    private val scrollTypeProperty = observable(LayerMetadata::scrollType)

    fun scrollTypeProperty() = scrollTypeProperty

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

        fun Byte.validateVisibilityType() =
                require(this in VISIBILITY_TYPE_RANGE)
                { "visibility type must be in range $VISIBILITY_TYPE_RANGE (type: $this)" }
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