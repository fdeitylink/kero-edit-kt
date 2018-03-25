package io.fdeitylink.kero.tile

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.toImmutableList

// TODO: Consider defaulting attributes to list of 0
/**
 * Represents a set of tile attributes for a tileset
 *
 * @constructor
 * Constructs a new [PxAttr] with the given `attributes`
 *
 * If no argument is passed for `attributes`, all attributes will be initialized to `0`
 *
 * @throws IllegalArgumentException if the size of attributes is not equal to the product of [WIDTH] and [HEIGHT],
 *                                  or if any attributes are not in the range [ATTRIBUTE_RANGE]
 */
internal class PxAttr(attributes: List<TileAttribute> = immutableListOf(*Array(WIDTH * HEIGHT) { 0 })) {
    private val _attributes: ImmutableList<TileAttribute> = attributes.toImmutableList()

    /**
     * The set of attributes for some tileset
     */
    inline val attributes: List<TileAttribute> get() = _attributes

    init {
        require(attributes.size == size)
        { "attributes must represent a 2D matrix with dimensions $WIDTH x $HEIGHT" }

        require(attributes.all { it in ATTRIBUTE_RANGE }) { "all attributes must be in range $ATTRIBUTE_RANGE" }
    }

    /**
     * Returns the attribute in this [PxAttr] with the given coordinates
     */
    operator fun get(x: Int, y: Int) = _attributes[x, y]

    /**
     * Returns a copy of this [PxAttr] with the attribute at the specified coordinates replaced with the given [attribute]
     */
    fun set(x: Int, y: Int, attribute: TileAttribute): PxAttr {
        require(attribute in ATTRIBUTE_RANGE) { "attribute must be in range $ATTRIBUTE_RANGE (attribute: $attribute)" }

        return if (_attributes[x, y] == attribute) {
            this
        }
        else {
            PxAttr(_attributes.set(Pair(x, y).toIndex(), attribute))
        }
    }

    override fun equals(other: Any?) = (this === other) || (other is PxAttr && other._attributes == _attributes)

    override fun hashCode() = _attributes.hashCode()

    override fun toString() =
            _attributes.chunked(WIDTH).joinToString(separator = " ") {
                it.joinToString(separator = " ", postfix = "\n") { String.format("%02X", it) }
            }

    private operator fun ImmutableList<TileAttribute>.get(x: Int, y: Int) = get(Pair(x, y).toIndex())

    private operator fun Int.component1() = this % WIDTH

    private operator fun Int.component2() = this / WIDTH

    private fun Int.toCoordinates(width: Int = PxAttr.WIDTH) = Pair(this % width, this / width)

    private fun Pair<Int, Int>.toIndex(width: Int = PxAttr.WIDTH) = this.let { (x, y) -> x + y * width }

    companion object {
        /**
         * The string that marks the beginning of a PxAttr file
         */
        const val HEADER_STRING = "pxMAP01\u0000"

        /**
         * The width of any tile attributes set
         */
        const val WIDTH = 16

        /**
         * The height of any tile attributes set
         */
        const val HEIGHT = 16

        /**
         * The valid range for [TileAttributes][TileAttribute] to occupy
         *
         * Equivalent to the positive range of a signed byte (`0..0x7F`)
         */
        val ATTRIBUTE_RANGE = 0..0x7F
    }
}

/**
 * Typealias for an [Int] representing an index in the list of tile attributes
 */
internal typealias TileAttribute = Int

/**
 * Alias for [PxAttr.WIDTH]
 *
 * Provided to allow accessing the width via a [PxAttr] instance rather than the class
 */
internal inline val PxAttr.width get() = PxAttr.WIDTH

/**
 * Alias for [PxAttr.HEIGHT]
 *
 * Provided to allow accessing the height via a [PxAttr] instance rather than the class
 */
internal inline val PxAttr.height get() = PxAttr.HEIGHT

/**
 * The product of `this` [PxAttr's][PxAttr] [width] and [height]
 */
internal inline val PxAttr.size get() = width * height