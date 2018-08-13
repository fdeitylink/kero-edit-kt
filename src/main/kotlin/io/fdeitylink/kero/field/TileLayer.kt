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

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.plus

/**
 * Represents an individual tile layer in a PxPack field
 *
 * @constructor
 * Constructs a new [TileLayer] with the given [width], [height], and `tiles`
 *
 * If no argument is passed for `tiles`, all tiles will be initialized to `0`
 *
 * @throws [IllegalArgumentException] if [width] or [height] are not in the range [DIMENSION_RANGE],
 *                                    their product is not equal to the size of [tiles],
 *                                    or if any tiles are not in the range [TILE_INDEX_RANGE]
 */
internal class TileLayer(
        width: Int,
        height: Int,
        tiles: List<TileIndex> = immutableListOf(*Array(width * height) { 0 })
) {
    private val _tiles: ImmutableList<TileIndex> = tiles.toImmutableList()

    /**
     * The tiles of this tile layer
     */
    inline val tiles: List<TileIndex> get() = _tiles

    // TODO: Consider adding setters for width and height that delegate to resized

    // Size is checked against 0 so neither dimension holds a nonsensical value (e.g. width = 3 when size = 0)

    /**
     * The width of this tile layer
     */
    val width = if (width * height == 0) 0 else width

    /**
     * The height of this tile layer
     */
    val height = if (width * height == 0) 0 else height

    init {
        require(width in DIMENSION_RANGE && height in DIMENSION_RANGE)
        { "layer dimensions must be in range $DIMENSION_RANGE (width: $width, height: $height)" }

        require(size == tiles.size) { "width * height != tiles.size (expected: $size, actual: ${tiles.size})" }

        require(tiles.all { it in TILE_INDEX_RANGE }) { "all tiles must be in range $TILE_INDEX_RANGE" }
    }

    /**
     * Constructs a new [TileLayer] with a [width] and [height] of `0`
     */
    constructor() : this(0, 0, immutableListOf())

    /**
     * Returns the tile in this [TileLayer] with the given coordinates
     */
    operator fun get(x: Int, y: Int) = _tiles[x, y]

    /**
     * Returns a copy of this [TileLayer] with the tile at the specified coordinates replaced with the given [tile]
     *
     * @throws IllegalArgumentException if tile is not in the range [TILE_INDEX_RANGE]
     */
    fun set(x: Int, y: Int, tile: TileIndex): TileLayer {
        require(tile in TILE_INDEX_RANGE) { "tile must be in range $TILE_INDEX_RANGE (tile: $tile)" }

        return if (_tiles[x, y] == tile) {
            this
        }
        else {
            TileLayer(width, height, _tiles.set(Pair(x, y).toIndex(), tile))
        }
    }

    /**
     * Returns a copy of this [TileLayer] that has been resized to the given [newWidth] and [newHeight]
     *
     * @throws IllegalArgumentException if [newWidth] or [newHeight] are not in the range [DIMENSION_RANGE]
     */
    fun resized(newWidth: Int, newHeight: Int): TileLayer {
        require(newWidth in DIMENSION_RANGE && newHeight in DIMENSION_RANGE)
        { "layer dimensions must be in range $DIMENSION_RANGE (newWidth: $newWidth, newHeight: $newHeight)" }

        return if (isEmpty() || newWidth * newHeight == 0) {
            TileLayer(newWidth, newHeight)
        }
        else if (this.width == newWidth && this.height == newHeight) {
            this
        }
        else {
            val wDiff = newWidth - this.width
            val hDiff = newHeight - this.height

            val newTiles = when {
                wDiff > 0 -> {
                    // By iterating backwards, we needn't worry about tile indexes being moved as the rows are expanded
                    (1..this.height).toList().foldRight(_tiles) { row, tiles ->
                        // Wherever the end of a row used to be, insert enough null tiles to reach the new newWidth
                        tiles.addAll(this.width * row, List(wDiff) { 0 })
                    }
                }
                wDiff < 0 -> {
                    (0 until this.height)
                            //toIndex() uses this.width, which is valid for use here
                            .map { row -> _tiles.subList(Pair(0, row).toIndex(), Pair(newWidth, row).toIndex()) }
                            .reduce { tiles, row -> tiles + row }
                }
                else -> _tiles
            }.let { tiles ->
                when {
                // Add enough null tiles to the end of the list to create the additional rows
                    hDiff > 0 -> tiles.addAll(tiles.lastIndex + 1, List(hDiff * newWidth) { 0 })
                // Remove from the end of the list to remove rows (hDiff < 0 so it's really subtraction here)
                    hDiff < 0 -> tiles.subList(0, (tiles.lastIndex + 1) + hDiff * newWidth)
                    else -> tiles
                }
            }

            TileLayer(newWidth, newHeight, newTiles)
        }
    }

    override fun equals(other: Any?) =
            (this === other) ||
            (other is TileLayer &&
             other.width == width &&
             other.height == height &&
             other._tiles == _tiles)

    override fun hashCode(): Int {
        var result = _tiles.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    override fun toString() =
            tiles.chunked(width).joinToString(separator = " ", prefix = "$width x $height") {
                it.joinToString(separator = " ", prefix = "\n") { String.format("%02X", it) }
            }

    private operator fun ImmutableList<TileIndex>.get(x: Int, y: Int) = get(Pair(x, y).toIndex())

    private operator fun Int.component1() = this % width

    private operator fun Int.component2() = this / width

    private fun Int.toCoordinates(width: Int = this@TileLayer.width) = Pair(this % width, this / width)

    private fun Pair<Int, Int>.toIndex(width: Int = this@TileLayer.width) = this.let { (x, y) -> x + y * width }

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
    }

    /**
     * Represents a type of tile layer in a PxPack field
     *
     * The order of the enum constants matches the order in which each layer appears within a PxPack file
     */
    internal enum class Type {
        FOREGROUND,
        MIDDLEGROUND,
        BACKGROUND
    }
}

/**
 * Typealias for an [Int] representing a tile index in a tileset
 */
internal typealias TileIndex = Int

/**
 * The product of `this` [TileLayer's][TileLayer] [width][TileLayer.width] and [height][TileLayer.height]
 */
internal val TileLayer.size get() = width * height

/**
 * Returns `true` if `this` [TileLayer's][TileLayer] [size][TileLayer.size] is `0`, `false` otherwise
 */
internal fun TileLayer.isEmpty() = size == 0

/**
 * Returns `true` if `this` [TileLayer's][TileLayer] [size][TileLayer.size] is greater than `0`, `false` otherwise
 */
internal fun TileLayer.isNotEmpty() = size > 0