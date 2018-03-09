package io.fdeitylink.kero

/**
 * Represents a name for anything associated with a Kero Blaster-based game
 *
 * @constructor
 * @throws [IllegalArgumentException] if the length of [name] exceeds [MAXIMUM_NAME_LENGTH] or if it contains spaces
 */
internal sealed class Name(
        /**
         * The name
         */
        val name: String
) : CharSequence by name {
    init {
        require(name.length <= MAXIMUM_NAME_LENGTH) { "name length must be <= $MAXIMUM_NAME_LENGTH (name: $name)" }

        require(' ' !in name) { "name may not contain spaces (name: $name)" }
    }

    companion object {
        /**
         * The maximum length for a name
         */
        const val MAXIMUM_NAME_LENGTH = 15
    }

    final override fun equals(other: Any?) =
            (this === other) ||
            (other is Name &&
             other::class == this::class &&
             other.name == name)

    final override fun hashCode() = name.hashCode()

    final override fun toString() = name
}

/**
 * Represents a map filename
 */
internal class MapName(name: String) : Name(name)

/**
 * Represents a spritesheet filename
 */
internal class SpritesheetName(name: String) : Name(name)

/**
 * Represents a tileset filename
 */
internal class TilesetName(name: String) : Name(name)

/**
 * Represents a script filename
 */
internal class ScriptName(name: String) : Name(name)

/**
 * Represents a unit name
 */
internal class UnitName(name: String) : Name(name)

/**
 * Returns `true` if a filename contains no spaces and
 * its length does not exceed [Name.MAXIMUM_NAME_LENGTH],
 * `false` otherwise
 */
internal fun String.isValidName() = length <= Name.MAXIMUM_NAME_LENGTH && ' ' !in this