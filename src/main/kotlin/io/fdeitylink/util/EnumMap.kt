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