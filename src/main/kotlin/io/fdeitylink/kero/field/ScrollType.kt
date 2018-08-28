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

/**
 * Represents the scrolling type of a tile layer in a PxPack field
 */
internal enum class ScrollType {
    NORMAL,

    THREE_FOURTHS,

    HALF,

    QUARTER,

    EIGHTH,

    ZERO,

    H_THREE_FOURTHS,

    H_HALF,

    H_QUARTER,

    V0_HALF;

    companion object {
        /**
         * The number of scroll types that exist
         */
        const val NUMBER_OF_SCROLL_TYPES = 10
    }
}