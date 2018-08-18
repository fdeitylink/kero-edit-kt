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

import javafx.beans.Observable
import javafx.beans.InvalidationListener

import io.fdeitylink.util.validate

// TODO: Consider implementing your own change listeners that give more information
// TODO: Consider changing tiles to Array<ByteArray>
// TODO: Consider implementing Collection instead of Iterable
/**
 * Represents an individual tile layer in a PxPack field
 *
 * @constructor
 * Constructs a new [TileLayer] from [tiles]
 *
 * Modifying [tiles] will have no effect on the created object, as the array will be deep-copied
 *
 * @throws [IllegalArgumentException] if [tiles] is invalid as per [isValidTiles]
 */
internal class TileLayer(tiles: Array<IntArray>) : Iterable<Int>, Observable {
    init {
        validateTiles(tiles)
    }

    // TODO: Consider IntegerProperties for width, height (would need to delegate to Properties, so no custom get/set)

    /**
     * The width of this tile layer
     *
     * @throws [IllegalArgumentException] if set to a value outside [DIMENSION_RANGE]
     */
    var width
        get() = if (tiles.isEmpty()) 0 else tiles.first().size
        set(value) = resize(value, height)

    /**
     * The height of this tile layer
     *
     * @throws [IllegalArgumentException] if set to a value outside [DIMENSION_RANGE]
     */
    var height
        get() = if (width == 0) 0 else tiles.size
        set(value) = resize(width, value)

    // If I did assignment without copying, modifying tiles without the set function would be possible
    private var tiles = tiles.map(IntArray::clone).toTypedArray()

    private val listeners = mutableListOf<InvalidationListener>()

    /**
     * Constructs a new [TileLayer] with [width] and [height] of `0`
     */
    constructor() : this(arrayOf())

    constructor(width: Int, height: Int) : this(Array(height) { IntArray(width) })

    /**
     * Returns the tile in this [TileLayer] with the given coordinates
     */
    operator fun get(x: Int, y: Int) = tiles[y][x]

    /**
     * Replaces the tile in this [TileLayer] at the given coordinates with the given [tile]
     *
     * @throws [IllegalArgumentException] if [tile] is outside the range [TILE_INDEX_RANGE]
     */
    operator fun set(x: Int, y: Int, tile: Int) {
        require(tile in TILE_INDEX_RANGE) { "tile must be in range $TILE_INDEX_RANGE (tile: $tile)" }

        val oldTile = tiles[y][x]

        if (oldTile != tile) {
            tiles[y][x] = tile
            listeners.forEach { it.invalidated(this) }
        }
    }

    /**
     * Resizes the tiles of this [TileLayer]
     *
     * @throws [IllegalArgumentException] if [newWidth] or [newHeight] are outside [DIMENSION_RANGE]
     */
    fun resize(newWidth: Int, newHeight: Int) {
        require(newWidth in DIMENSION_RANGE && newHeight in DIMENSION_RANGE)
        { "dimensions must be in range $DIMENSION_RANGE (newWidth: $newWidth, newHeight: $newHeight)" }

        val oldWidth = width
        val oldHeight = height

        if (oldWidth != newWidth || oldHeight != newHeight) {
            tiles = if (oldWidth * oldHeight == 0 || newWidth * newHeight == 0) {
                Array(newHeight) { IntArray(newWidth) }
            }
            else {
                Array(newHeight) { y ->
                    IntArray(newWidth) { x ->
                        if (y >= oldWidth || x >= oldHeight) 0 else tiles[y][x]
                    }
                }
            }

            listeners.forEach { it.invalidated(this) }
        }
    }

    /**
     * Given an [Int] representing an index, returns a [Pair] of x and y coordinates.
     * Useful for using methods like `forEachIndexed` on this class.
     *
     * ***Because [index] and is corresponding (x, y) coordinates are dependent on the [width] of a [TileLayer],
     * arguments for [index] should only come from iterating over the [TileLayer] on which this method is called.***
     */
    fun indexToCoordinates(index: Int) = Pair(index % width, index / width)

    /**
     * Given a [Pair] representing a set of (x, y) coordinates, returns an [Int] representing an index.
     * Useful for using methods like `elementAt` on this class.
     *
     * ***Because the (x, y) coordinates and their corresponding index are dependent on the [width] of a [TileLayer],
     * the return value should only be used for methods called on the [TileLayer] on which this method is called.***
     */
    fun coordinatesToIndex(coordinates: Pair<Int, Int>) = coordinates.let { (x, y) -> x + y * width }

    override fun iterator() = object : IntIterator() {
        var x = 0
        var y = 0

        override fun hasNext() = y != height

        override fun nextInt(): Int {
            val ret = tiles[y][x++]
            if (x == width) {
                x = 0
                y++
            }
            return ret
        }
    }

    override fun addListener(listener: InvalidationListener) {
        listeners += listener
    }

    override fun removeListener(listener: InvalidationListener) {
        listeners -= listener
    }

    override fun equals(other: Any?) =
            (this === other) ||
            (other is TileLayer &&
             tiles.contentDeepEquals(other.tiles))

    override fun hashCode() = Objects.hash(tiles)

    override fun toString() =
            "TileLayer(" +
            "width=$width," +
            "height=$height" +
            ")"

    companion object {
        /**
         * The string that marks the beginning of a new tile layer in a PxPack file
         */
        const val HEADER_STRING = "pxMAP01\u0000"

        /**
         * The number of tile layers in a PxPack field
         */
        const val NUMBER_OF_TILE_LAYERS = 3

        /**
         * The valid range for [TileIndexes][TileIndex] to occupy
         *
         * Equivalent to the range of an unsigned byte (`0..0xFF`)
         */
        val TILE_INDEX_RANGE = 0..0xFF

        /**
         * The valid range for [width] or [height] to occupy
         *
         * Equivalent to the range of an unsigned short (`0..0xFFFF`)
         */
        val DIMENSION_RANGE = 0..0xFFFF

        /**
         * Returns true if `this` set of tiles is square, has both dimensions in the range [DIMENSION_RANGE],
         * and all of its tiles are in the range [TILE_INDEX_RANGE], `false` otherwise
         */
        fun Array<IntArray>.isValidTiles(): Boolean {
            val width = if (this.isEmpty()) 0 else this.first().size
            val height = if (width == 0) 0 else this.size

            return this.all { it.size == width } &&
                   width in DIMENSION_RANGE &&
                   height in DIMENSION_RANGE &&
                   this.all { it.all { it in TILE_INDEX_RANGE } }
        }

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [tiles] is not square, has one or more dimensions
         * outside the range [DIMENSION_RANGE], or any of its tiles are outside the range [TILE_INDEX_RANGE]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateTiles(
                tiles: Array<IntArray>,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) {
            val width = if (tiles.isEmpty()) 0 else tiles.first().size
            val height = if (width == 0) 0 else tiles.size

            validate(tiles.all { it.size == width }, exceptCtor) { "tiles must be square" }

            validate(width in DIMENSION_RANGE && height in DIMENSION_RANGE, exceptCtor)
            { "dimensions must be in range $DIMENSION_RANGE (width: $width, height; $height)" }

            validate(tiles.all { it.all { it in TILE_INDEX_RANGE } }, exceptCtor)
            { "tile indexes must be in range $TILE_INDEX_RANGE" }
        }
    }

    /**
     * Represents a type of tile layer in a PxPack field
     *
     * The order of the enum constants matches the order in which each layer appears within a PxPack file
     */
    enum class Type {
        FOREGROUND,
        MIDDLEGROUND,
        BACKGROUND
    }
}

/**
 * Returns `true` if `this` [TileLayer's][TileLayer] size is `0`, `false` otherwise
 */
internal fun TileLayer.isEmpty() = this.width * this.height == 0

/**
 * Returns `true` if `this` [TileLayer's][TileLayer] size is greater than `0`, `false` otherwise
 */
internal fun TileLayer.isNotEmpty() = this.width * this.height > 0