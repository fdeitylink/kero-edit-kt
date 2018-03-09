package io.fdeitylink.kero.map

import java.util.EnumMap

import io.fdeitylink.util.Quintuple
import io.fdeitylink.util.Quadruple

import io.fdeitylink.kero.MapName
import io.fdeitylink.kero.SpritesheetName
import io.fdeitylink.kero.TilesetName

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
        val maps: Quadruple<MapName, MapName, MapName, MapName> =
                Quadruple(MapName(""), MapName(""), MapName(""), MapName("")),

        /**
         * The spritesheet used for rendering the [units][PxUnit] of this PxPack map
         */
        val spritesheet: SpritesheetName = SpritesheetName(""),

        /**
         * A set of five bytes whose purpose is unknown
         */
        val unknownBytes: Quintuple<Byte, Byte, Byte, Byte, Byte> = Quintuple(0, 0, 0, 0, 0),

        /**
         * The background color of this PxPack map
         */
        val bgColor: BackgroundColor = BackgroundColor(0, 0, 0),

        /**
         * A set of three tile layer property sets, where each corresponds to tile layer in this PxPack map
         */
        val layerProperties: Map<TileLayer.Type, LayerProperties> =
                EnumMap<TileLayer.Type, LayerProperties>(TileLayer.Type::class.java).also {
                    it[TileLayer.Type.BACKGROUND] = LayerProperties()
                    it[TileLayer.Type.MIDDLEGROUND] = LayerProperties(TilesetName(""))
                    it[TileLayer.Type.FOREGROUND] = LayerProperties(scrollType = ScrollType.THREE_FOURTHS)
                }
) {
    init {
        require(description.length <= MAXIMUM_DESCRIPTION_LENGTH)
        { "description length must be <= $MAXIMUM_DESCRIPTION_LENGTH (description: $description)" }

        require(layerProperties.size == TileLayer.NUMBER_OF_TILE_LAYERS)
        { "layerProperties.size != ${TileLayer.NUMBER_OF_TILE_LAYERS} (size: ${layerProperties.size})" }
        require(layerProperties[TileLayer.Type.BACKGROUND]!!.tileset.name.isNotEmpty())
        { "background tileset name may not be empty" }
    }

    companion object {
        /**
         * The string that marks the beginning of the head in a PxPack file
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
    }
}

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
         */
        val tileset: TilesetName = TilesetName("mpt00"),

        /**
         * Potentially represents some kind of visibility setting used to display a tile layer in a PxPack map
         *
         * See [VisibilityType's][VisibilityType] documentation for more information on this ambiguity
         */
        val visibilityType: VisibilityType = VisibilityType(2),

        /**
         * The type of scrolling used to display a tile layer in a PxPack map
         */
        val scrollType: ScrollType = ScrollType.NORMAL
)

/**
 * Potentially represents some kind of visibility setting for a tile layer in a PxPack map.
 *
 * Though this class is called `VisibilityType`, whether or not the information in a PxPack file
 * that the relevant byte stores actually represents any kind of visibility toggle or setting is
 * unknown. It is named as such because when modifying the relevant byte in a PxPack file, the
 * visibility of a tile layer was altered, although it is ambiguous as to how.
 *
 * Setting the relevant byte to the following values seems to have the corresponding effects:
 *
 * `0`: invisible
 *
 * `2`: visible (the byte also usually seems to hold this value)
 *
 * `1` or `3..32`: pulls the wrong tiles but from the correct/same tileset (potentially applies some kind of offset)
 *
 * `33..`: game crashes
 *
 * This class will likely eventually be replaced with an enum class where each enum represents a visibility type.
 */
internal data class VisibilityType(
        val type: Byte
) {
    init {
        require(type in 0 until NUMBER_OF_VISIBILITY_TYPES)
        { "visibility type must be in range 0 - ${NUMBER_OF_VISIBILITY_TYPES - 1} (type: $type)" }
    }

    companion object {
        /**
         * The supposed number of visibility types that exist
         */
        const val NUMBER_OF_VISIBILITY_TYPES = 33
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

    /**
     * In the original scroll.txt file, "H Quater" is listed, which is likely a typo
     */
    H_QUARTER,

    V0_HALF;

    /**
     * The byte representing a given scroll type's index in the scroll.txt file
     */
    val byte = this.ordinal.toByte()

    companion object {
        /**
         * The number of scroll types that exist
         */
        const val NUMBER_OF_SCROLL_TYPES = 10
    }
}