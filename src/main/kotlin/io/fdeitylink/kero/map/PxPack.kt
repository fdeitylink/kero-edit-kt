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

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.toImmutableList

import io.fdeitylink.util.toEnumMap

/**
 * Represents a PxPack map
 */
internal data class PxPack(
        /**
         * Represents the [head][Head] of this PxPack map
         */
        val head: Head = Head(),

        // TODO: Consider changing to SortedMap since order matters in the file and this would make iteration consistent
        /**
         * Represents the [tile layers][TileLayer] of this PxPack map
         */
        val layers: Map<TileLayer.Type, TileLayer> = TileLayer.Type.values().associate { it to TileLayer() }.toEnumMap(),

        /**
         * Represents the [units][PxUnit] of this PxPack map
         */
        val units: ImmutableList<PxUnit> = immutableListOf()
) {
    init {
        require(layers.size == TileLayer.NUMBER_OF_TILE_LAYERS)
        { "layers.size != ${TileLayer.NUMBER_OF_TILE_LAYERS} (size: ${layers.size})" }

        require(units.size <= PxUnit.MAXIMUM_NUMBER_OF_UNITS)
        { "number of units cannot exceed ${PxUnit.MAXIMUM_NUMBER_OF_UNITS} (size: ${units.size})" }
    }

    constructor(
            head: Head = Head(),
            layers: Map<TileLayer.Type, TileLayer> = TileLayer.Type.values().associate { it to TileLayer() }.toEnumMap(),
            units: List<PxUnit> = immutableListOf()
    ) : this(head, layers, units.toImmutableList())

    companion object
}