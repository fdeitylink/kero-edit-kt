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

package io.fdeitylink.kero.map

import io.fdeitylink.util.enumMapOf

import io.fdeitylink.kero.validateName

/**
 * Represents the head of a PxPack map
 */
internal data class Head(
        /**
         * A string used by the developer as a description of this PxPack map
         *
         * Non-essential and not used by the game
         */
        val description: String = "",

        /**
         * A set of up to four maps referenced by this PxPack map
         */
        val maps: List<String> = List(NUMBER_OF_REFERENCED_MAPS) { "" },

        /**
         * The spritesheet used for rendering the [units][PxUnit] of this PxPack map
         */
        val spritesheet: String = "",

        /**
         * A set of five bytes whose purpose is unknown
         */
        val unknownBytes: List<Byte> = List(NUMBER_OF_UNKNOWN_BYTES) { 0.toByte() },

        /**
         * The background color of this PxPack map
         */
        val bgColor: BackgroundColor = BackgroundColor(0, 0, 0),

        /**
         * A set of three tile layer property sets, where each corresponds to tile layer in this PxPack map
         */
        val layerProperties: Map<TileLayer.Type, LayerProperties> = enumMapOf(
                TileLayer.Type.FOREGROUND to LayerProperties(),
                TileLayer.Type.MIDDLEGROUND to LayerProperties(tileset = ""),
                TileLayer.Type.BACKGROUND to LayerProperties(tileset = "", scrollType = ScrollType.THREE_FOURTHS)
        )
) {
    init {
        require(description.length <= MAXIMUM_DESCRIPTION_LENGTH)
        { "description length must be <= $MAXIMUM_DESCRIPTION_LENGTH (description: $description)" }

        require(maps.size == NUMBER_OF_REFERENCED_MAPS)
        { "maps.size != $NUMBER_OF_REFERENCED_MAPS (size :${maps.size})" }
        maps.forEach { it.validateName("map") }

        spritesheet.validateName("spritesheet")

        require(unknownBytes.size == NUMBER_OF_UNKNOWN_BYTES)
        { "unknownBytes.size != $NUMBER_OF_UNKNOWN_BYTES (size: ${unknownBytes.size})" }

        require(layerProperties.size == TileLayer.NUMBER_OF_TILE_LAYERS)
        { "layerProperties.size != ${TileLayer.NUMBER_OF_TILE_LAYERS} (size: ${layerProperties.size})" }
        require(layerProperties[TileLayer.Type.FOREGROUND]!!.tileset.isNotEmpty())
        { "foreground tileset name may not be empty" }
    }

    companion object {
        /**
         * The string that marks the beginning of the head in a PxPack file,
         * or in other words marks the beginning of a PxPack file
         */
        const val HEADER_STRING = "PXPACK121127a**\u0000"

        /**
         * The maximum length of the description string in a PxPack map
         */
        const val MAXIMUM_DESCRIPTION_LENGTH = 31

        /**
         * The number of maps that are referenced by a PxPack map
         */
        const val NUMBER_OF_REFERENCED_MAPS = 4

        /**
         * The number of contiguous bytes in the head of a PxPack map whose purpose is currently unknown
         */
        const val NUMBER_OF_UNKNOWN_BYTES = 5
    }
}

// TODO: Consider storing Int instead of Byte
/**
 * Represents the background color of a PxPack map
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
 * Represents a set of properties for a tile layer in a PxPack map
 */
internal data class LayerProperties(
        /**
         * The name of the tileset used to display a tile layer in a PxPack map
         *
         * Defaults to `"mpt00`
         */
        val tileset: String = "mpt00",

        /**
         * Potentially represents some kind of visibility setting used to display a tile layer in a PxPack map
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
        val visibilityType: Byte = 2,

        /**
         * The type of scrolling used to display a tile layer in a PxPack map
         *
         * Defaults to [ScrollType.NORMAL]
         */
        val scrollType: ScrollType = ScrollType.NORMAL
) {
    init {
        tileset.validateName("tileset")

        require(visibilityType in VISIBILITY_TYPE_RANGE)
        { "visibility type must be in range $VISIBILITY_TYPE_RANGE (type: $visibilityType)" }
    }

    companion object {
        /**
         * The supposed valid range for [visibilityType] to occupy
         */
        val VISIBILITY_TYPE_RANGE = 0..32
    }
}

/**
 * Represents the scrolling type of a tile layer in a PxPack map
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