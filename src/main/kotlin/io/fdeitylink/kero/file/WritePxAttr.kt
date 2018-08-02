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

package io.fdeitylink.kero.file

import io.fdeitylink.kero.tile.PxAttr
import io.fdeitylink.kero.tile.TileAttribute
import io.fdeitylink.kero.tile.width
import io.fdeitylink.kero.tile.height

internal fun PxAttr.toBytes() =
        PxAttr.HEADER_STRING.toByteArray() +
        width.toShort().toBytes() +
        height.toShort().toBytes() +
        0 + // TODO: Verify that this byte is always 0
        attributes.map(TileAttribute::toByte)