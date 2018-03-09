package io.fdeitylink.kero.map

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.plus

import io.fdeitylink.util.toUInt

/**
 * Represents an individual tile layer in a PxPack map
 *
 * @property tiles The tiles of this tile layer
 *
 * @constructor
 * Constructs a new [TileLayer] with the given [width] and [height] and [tiles]
 *
 * If no argument is passed for [tiles], all tiles will be initialized to `0`
 *
 * @throws [IllegalArgumentException] if [width] or [height] exceed [MAXIMUM_SIZE_OF_DIMENSION]
 */
internal class TileLayer(
        width: Int,
        height: Int,
        val tiles: ImmutableList<TileIndex> = immutableListOf(*Array(width * height) { 0.toByte() })
) {
    // TODO: Consider adding setters for width and height that delegate to resize()

    // Size is checked against 0 so neither dimension holds a nonsensical value (e.g. width = 3 when size = 0)

    /**
     * The width of this tile layer
     *
     * Though [Short] is signed, this value is really unsigned, so upcast to an [Int] as necessary
     */
    val width = if (width * height == 0) 0 else width.toShort()

    /**
     * The height of this tile layer
     *
     * Though [Short] is signed, this value is really unsigned, so upcast to an [Int] as necessary
     */
    val height = if (width * height == 0) 0 else height.toShort()

    init {
        require(width <= MAXIMUM_SIZE_OF_DIMENSION && height <= MAXIMUM_SIZE_OF_DIMENSION)
        { "layer dimensions must be <= $MAXIMUM_SIZE_OF_DIMENSION (width: $width, height: $height)" }

        require(size == tiles.size) { "width * height != tiles.size (expected: $size, actual: ${tiles.size})" }
    }

    /**
     * Constructs a new [TileLayer] with the given [width] and [height]
     *
     * If no argument is passed for [tiles], all tiles will be initialized to `0`
     */
    constructor(
            width: Short,
            height: Short,
            tiles: ImmutableList<TileIndex> = immutableListOf(*Array(width * height) { 0.toByte() })
    ) : this(width.toInt(), height.toInt(), tiles)

    /**
     * Constructs a new [TileLayer] with a [width] and [height] of `0`
     */
    constructor() : this(0, 0, immutableListOf())

    /**
     * Returns the tile in this [TileLayer] with the given coordinates, which are treated as unsigned integers
     */
    operator fun get(x: Short, y: Short) = get(x.toUInt(), y.toUInt())

    /**
     * Returns the tile in this [TileLayer] with the given coordinates
     */
    operator fun get(x: Int, y: Int) = tiles[x, y]

    /**
     * Returns a copy of this [TileLayer] with the tile at the specified coordinates,
     * which are treated as unsigned integers, replaced with the given [tile]
     */
    fun set(x: Short, y: Short, tile: TileIndex) = set(x.toUInt(), y.toUInt(), tile)

    /**
     * Returns a copy of this [TileLayer] with the tile at the specified coordinates replaced with the given [tile]
     */
    fun set(x: Int, y: Int, tile: TileIndex) =
            if (tiles[x, y] == tile) {
                this
            }
            else {
                TileLayer(
                        width,
                        height,
                        tiles.set(Pair(x, y).toIndex(), tile)
                )
            }

    /**
     * Returns a copy of this [TileLayer] that has been resized to the given [width] and [height],
     * which are treated as unsigned integers
     */
    fun resize(width: Short, height: Short) = resize(width.toUInt(), height.toUInt())

    /**
     * Returns a copy of this [TileLayer] that has been resized to the given [width] and [height]
     */
    fun resize(width: Int, height: Int) =
            if (isEmpty() || width * height == 0) {
                TileLayer(width, height)
            }
            else if (this.width.toUInt() == width && this.height.toUInt() == height) {
                this
            }
            else {
                val wDiff = width - this.width
                val hDiff = height - this.height

                val newTiles = when {
                    wDiff > 0 -> {
                        // By iterating backwards, we needn't worry about tile indexes being moved as the rows are expanded
                        (1..this.height).toList().foldRight(tiles) { row, tiles ->
                            // Wherever the end of a row used to be, insert enough null tiles to reach the new width
                            tiles.addAll(this.width * row, List(wDiff) { 0.toByte() })
                        }
                    }
                    wDiff < 0 -> {
                        (0 until this.height)
                                //toIndex() uses this.width, which is valid for use here
                                .map { row -> tiles.subList(Pair(0, row).toIndex(), Pair(width, row).toIndex()) }
                                .reduce { tiles, row -> tiles + row }
                    }
                    else -> tiles
                }.let { tiles ->
                    when {
                    // Add enough null tiles to the end of the list to create the additional rows
                        hDiff > 0 -> tiles.addAll(tiles.lastIndex + 1, List(hDiff * width) { 0.toByte() })
                    // Remove from the end of the list to remove rows (hDiff < 0 so it's really subtraction here)
                        hDiff < 0 -> tiles.subList(0, (tiles.lastIndex + 1) + hDiff * width)
                        else -> tiles
                    }
                }

                TileLayer(width, height, newTiles)
            }

    override fun equals(other: Any?) =
            (this === other) ||
            (other is TileLayer &&
             other.width == width &&
             other.height == height &&
             other.tiles == tiles)

    override fun hashCode(): Int {
        var result = tiles.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }

    override fun toString() =
            tiles.chunked(width.toUInt()).joinToString(separator = " ", prefix = "$width x $height") {
                it.joinToString(separator = " ", prefix = "\n") { String.format("%02X", it) }
            }

    private operator fun ImmutableList<TileIndex>.get(x: Int, y: Int) = get(Pair(x, y).toIndex())

    private operator fun Int.component1() = this % width.toUInt()

    private operator fun Int.component2() = this / width.toUInt()

    private fun Int.toCoordinates(width: Int = this@TileLayer.width.toUInt()) = this.let { (x, y) -> Pair(x, y) }

    private fun Pair<Int, Int>.toIndex(width: Int = this@TileLayer.width.toUInt()) =
            this.let { (x, y) -> x + y * width }

    companion object {
        /**
         * The string that marks the beginning of a new tile layer in a PxPack file
         */
        const val HEADER_STRING ="pxMAP01\u0000"

        /**
         * The number of tile layers in a PxPack map
         */
        const val NUMBER_OF_TILE_LAYERS = 3

        /**
         * The maximum width or height for a tile layer in a PxPack map
         */
        const val MAXIMUM_SIZE_OF_DIMENSION = 0xFFFF
    }

    /**
     * Represents a type of tile layer in a PxPack map
     */
    internal enum class Type {
        FOREGROUND,
        MIDDLEGROUND,
        BACKGROUND;
    }
}

/**
 * Typealias for a byte representing a tile index in a tileset
 *
 * Though [Byte] is signed, values of this type are really unsigned, so upcast to an [Int] as necessary
 */
internal typealias TileIndex = Byte

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