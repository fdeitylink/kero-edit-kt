package io.fdeitylink.kero.map

import java.util.EnumMap

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf

/**
 * Represents a PxPack map
 */
internal data class PxPack(
        /**
         * Represents the [head][Head] of this PxPack map
         */
        val head: Head = Head(),

        /**
         * Represents the [tile layers][TileLayer] of this PxPack map
         */
        val layers: Map<TileLayer.Type, TileLayer> =
                EnumMap<TileLayer.Type, TileLayer>(TileLayer.Type::class.java).also { map ->
                    TileLayer.Type.values().forEach { map[it] = TileLayer() }
                },

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

    companion object
}