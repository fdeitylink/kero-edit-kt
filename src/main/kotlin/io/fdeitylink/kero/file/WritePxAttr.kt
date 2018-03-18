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