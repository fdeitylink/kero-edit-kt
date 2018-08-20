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

import javafx.collections.ObservableList

import kotlinx.collections.immutable.toImmutableMap

import tornadofx.observable

import io.fdeitylink.util.validate

/**
 * Represents a PxPack field
 *
 * @constructor
 * Constructs a new [PxPack] object, using each argument to initialize the corresponding properties
 *
 * @throws [IllegalArgumentException] if [layers] is invalid as per [isValidLayers],
 * or [units] is invalid as per [isValidUnits]
 */
internal class PxPack(
        head: Head = Head(),
        layers: Map<TileLayer.Type, TileLayer> = TileLayer.Type.values().associate { it to TileLayer() },
        units: List<PxUnit> = listOf()
) {
    init {
        validateLayers(layers)
        validateUnits(units)
    }

    /**
     * Represents the [head][Head] of this PxPack field
     */
    @Suppress("CanBePrimaryConstructorProperty")
    val head: Head = head

    // TODO: Consider changing to SortedMap since order matters in the file and this would make iteration consistent
    /**
     * Represents the [tile layers][TileLayer] of this PxPack field
     *
     * @throws [UnsupportedOperationException] if an attempt is made to change the contents of this map
     */
    val layers: Map<TileLayer.Type, TileLayer> = layers.toImmutableMap()

    /**
     * Represents the [units][PxUnit] of this PxPack field
     */
    val units: ObservableList<PxUnit> = units.toMutableList().observable()

    override fun equals(other: Any?) =
            (this === other) ||
            (other is PxPack &&
             head == other.head &&
             layers == other.layers &&
             units == other.units)

    override fun hashCode() = Objects.hash(head, layers, units)

    override fun toString() =
            "PxPack(" +
            "head=$head," +
            "layers=$layers," +
            "units=$units" +
            ")"

    companion object {
        /**
         * Returns `true` if the [size][Map.size] of `this` set of [TileLayer] objects contains one instance for every
         * layer in a PxPack field, `false` otherwise
         */
        fun Map<TileLayer.Type, TileLayer>.isValidLayers() = this.size == TileLayer.NUMBER_OF_TILE_LAYERS

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [layers] does not contain an object for every
         * tile layer in a PxPack field
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateLayers(
                layers: Map<TileLayer.Type, TileLayer>,
                exceptCtor: (String) -> Exception = ::IllegalArgumentException
        ) =
                validate(layers.size == TileLayer.NUMBER_OF_TILE_LAYERS, exceptCtor)
                { "layers.size != ${TileLayer.NUMBER_OF_TILE_LAYERS} (size: ${layers.size})" }

        /**
         * Returns `true` if the [size][List.size] of `this` list of [PxUnit] objects does not exceed
         * [PxUnit.MAXIMUM_NUMBER_OF_UNITS], `false` otherwise
         */
        fun List<PxUnit>.isValidUnits() = this.size <= PxUnit.MAXIMUM_NUMBER_OF_UNITS

        /**
         * Constructs and throws an exception (using [exceptCtor]) if the [size][List.size] of [units] exceeds
         * [PxUnit.MAXIMUM_NUMBER_OF_UNITS]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateUnits(units: List<PxUnit>, exceptCtor: (String) -> Exception = ::IllegalArgumentException) =
                validate(units.size <= PxUnit.MAXIMUM_NUMBER_OF_UNITS, exceptCtor)
                { "units.size must be <= ${PxUnit.MAXIMUM_NUMBER_OF_UNITS} (size: ${units.size})" }
    }
}