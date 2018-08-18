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

package io.fdeitylink.kero.tile

import java.util.Objects

import javafx.beans.Observable
import javafx.beans.InvalidationListener

import io.fdeitylink.util.validate

// TODO: Consider implementing your own change listeners that give more information
// TODO: Consider defaulting attributes to those in mpt00 file if possible
// TODO: Consider changing tiles to Array<ByteArray>
// TODO: Consider implementing Collection instead of Iterable
/**
 * Represents a set of tile attributes for a tileset
 *
 * @constructor
 * Constructs a new [PxAttr] with the given [attributes]
 *
 * @param attributes All values are defaulted to `0`
 *
 * @throws [IllegalArgumentException] if [attributes] is invalid as per [isValidAttributes]
 */
internal class PxAttr(
        attributes: Array<IntArray> = Array(HEIGHT) { IntArray(WIDTH) }
) : Iterable<Int>, Observable {
    init {
        validateAttributes(attributes)
    }

    // If I did assignment without copying, modifying attributes without the set function would be possible
    private val attributes = attributes.map(IntArray::clone).toTypedArray()

    private val listeners = mutableListOf<InvalidationListener>()

    /**
     * Returns the tile attribute in this [PxAttr] with the given coordinates
     */
    operator fun get(x: Int, y: Int) = attributes[y][x]

    /**
     * Replaces the tile attribute in this [PxAttr] at the given coordinates with the given [attribute]
     *
     * @throws [IllegalArgumentException] if [attribute] is outside the range [TILE_ATTRIBUTE_RANGE]
     */
    operator fun set(x: Int, y: Int, attribute: Int) {
        validateAttribute(attribute)

        val oldAttribute = attributes[y][x]

        if (oldAttribute != attribute) {
            attributes[y][x] = attribute
            listeners.forEach { it.invalidated(this) }
        }
    }

    override fun addListener(listener: InvalidationListener) {
        listeners += listener
    }

    override fun removeListener(listener: InvalidationListener) {
        listeners -= listener
    }

    override fun iterator() = object : IntIterator() {
        var x = 0
        var y = 0

        override fun hasNext() = y != HEIGHT

        override fun nextInt(): Int {
            val ret = attributes[y][x++]
            if (x == WIDTH) {
                x = 0
                y++
            }
            return ret
        }
    }

    override fun equals(other: Any?) =
            (this === other) ||
            (other is PxAttr &&
             attributes.contentDeepEquals(other.attributes))

    override fun hashCode() = Objects.hash(attributes)

    // TODO: Consider overriding toString() (don't want to display attributes & width + height are constant)

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
         * The valid range for tile attributes to occupy
         *
         * Equivalent to the positive range of a signed byte (`0..0x7F`)
         */
        val TILE_ATTRIBUTE_RANGE = 0..0x7F

        /**
         * Given an [Int] representing an index, returns a [Pair] of x and y coordinates.
         * Useful for using methods like `forEachIndexed` on this class.
         */
        fun indexToCoordinates(index: Int) = Pair(index % WIDTH, index / WIDTH)

        /**
         * Given a [Pair] representing a set of (x, y) coordinates, returns an [Int] representing an index.
         * Useful for using methods like `elementAt` on this class.
         */
        fun coordinatesToIndex(coordinates: Pair<Int, Int>) = coordinates.let { (x, y) -> x + y * WIDTH }

        // TODO: Unify the *Width() & *Height() methods

        /**
         * Returns `true` if `this` equals [WIDTH], `false` otherwise
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun Int.isValidWidth() = this == WIDTH

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [width] does not equal [WIDTH]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateWidth(width: Int, exceptCtor: (String) -> Exception = ::IllegalArgumentException) =
                validate(width == WIDTH, exceptCtor) { "width != $WIDTH (width: $width)" }

        /**
         * Returns `true` if `this` equals [HEIGHT], `false` otherwise
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun Int.isValidHeight() = this == HEIGHT

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [height] does not equal [HEIGHT]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateHeight(height: Int, exceptCtor: (String) -> Exception = ::IllegalArgumentException) =
                validate(height == HEIGHT, exceptCtor) { "height != $HEIGHT (height: $height)" }

        /**
         * Returns `true` if `this` attribute is in the range [TILE_ATTRIBUTE_RANGE], `false` otherwise
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun Int.isValidAttribute() = this in TILE_ATTRIBUTE_RANGE

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [attribute] is outside the range
         * [TILE_ATTRIBUTE_RANGE]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateAttribute(attribute: Int, exceptCtor: (String) -> Exception = ::IllegalArgumentException) {
            validate(attribute in TILE_ATTRIBUTE_RANGE, exceptCtor)
            { "tile attribute must be in range $TILE_ATTRIBUTE_RANGE (attribute: $attribute)" }
        }

        /**
         * Returns `true` if `this` set of attributes has dimensions [WIDTH] by [HEIGHT] and all of its attributes
         * are in the range [TILE_ATTRIBUTE_RANGE], `false` ottherwise
         */
        fun Array<IntArray>.isValidAttributes() =
                this.size == HEIGHT &&
                this.all { it.size == WIDTH } &&
                this.all { it.all { it in TILE_ATTRIBUTE_RANGE } }

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [attributes] does not have the dimensions
         * [WIDTH] by [HEIGHT] or any of its attributes are outside the range [TILE_ATTRIBUTE_RANGE]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateAttributes(
                attributes: Array<IntArray>,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) {
            validate(attributes.size == HEIGHT, exceptCtor)
            { "height of attributes != $HEIGHT (height: ${attributes.size})" }

            validate(attributes.all { it.size == WIDTH }, exceptCtor)
            { "attributes must be square and have width $WIDTH" }

            validate(attributes.all { it.all { it in TILE_ATTRIBUTE_RANGE } }, exceptCtor)
            { "tile attributes must be in range $TILE_ATTRIBUTE_RANGE" }
        }
    }
}