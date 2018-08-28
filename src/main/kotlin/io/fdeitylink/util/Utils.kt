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

/**
 * Constructs and throws an exception (using [exceptCtor]) with the result of calling [lazyMessage] if [value] is `false`
 *
 * Similar to [require] but the type of thrown exception can be chosen
 *
 * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
 */
inline fun validate(
        value: Boolean,
        exceptCtor: (String) -> Exception = ::IllegalArgumentException,
        lazyMessage: () -> Any
) {
    if (!value) {
        throw exceptCtor(lazyMessage().toString())
    }
}

/**
 * Constructs and throws an exception (using [exceptCtor]) if `collection.size` is not equal to [expectedSize]
 *
 * @param collectionName The name of the parameter that [collection] was an argument for
 * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
 */
inline fun validateSize(
        collection: Collection<*>,
        collectionName: String,
        expectedSize: Int,
        exceptCtor: (String) -> Exception = ::IllegalArgumentException
) =
        validate(collection.size == expectedSize, exceptCtor)
        { "$collectionName.size != $expectedSize (size: ${collection.size})" }

/**
 * Constructs and throws an exception (using [exceptCtor]) if `map.size` is not equal to [expectedSize]
 *
 * @param mapName The name of the parameter that [map] was an argument for
 * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
 */
inline fun validateSize(
        map: Map<*, *>,
        mapName: String,
        expectedSize: Int,
        exceptCtor: (String) -> Exception = ::IllegalArgumentException
) = validate(map.size == expectedSize, exceptCtor) { "$mapName.size != $expectedSize (size: ${map.size})" }

/**
 * Constructs and throws an exception (using [exceptCtor]) if `map.size` is not equal to [expectedSize]
 *
 * @param arrayName The name of the parameter that [array] was an argument for
 * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
 */
inline fun validateSize(
        array: ByteArray,
        arrayName: String,
        expectedSize: Int,
        exceptCtor: (String) -> Exception = ::IllegalArgumentException
) = validate(array.size == expectedSize, exceptCtor) { "$arrayName.size != $expectedSize (size: ${array.size})" }