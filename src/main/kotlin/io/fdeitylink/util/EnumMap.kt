package io.fdeitylink.util

import java.util.EnumMap

/**
 * Returns an empty new [EnumMap]
 */
inline fun <reified K : Enum<K>, V> enumMapOf() = EnumMap<K, V>(K::class.java)

/**
 * Returns a new [EnumMap] with the specified contents, given as a list of pairs
 * where the first component is the key and the second is the value
 */
inline fun <reified K : Enum<K>, V> enumMapOf(vararg pairs: Pair<K, V>) =
        EnumMap<K, V>(K::class.java).also { it.putAll(pairs) }

/**
 * Returns a new [EnumMap] with the contents of `this` [Map]
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <K : Enum<K>, V> Map<K, V>.toEnumMap() = EnumMap(this)