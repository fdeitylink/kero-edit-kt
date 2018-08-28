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

package io.fdeitylink.kero

import java.nio.file.Path
import java.nio.file.Files

import java.io.File

import javafx.collections.ObservableSet

import kotlin.properties.ReadOnlyProperty

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet

import io.fdeitylink.util.lowerCaseName
import io.fdeitylink.util.lowerCaseExtension
import io.fdeitylink.util.nameSansExtension

import io.fdeitylink.util.validatedSortedObservableSet

/**
 * Stores and groups all of the [Paths][Path] and properties relevant to a Kero Blaster-based game
 *
 * @constructor
 * Constructs a new [GameData] object from `exe` and the resource [Paths][Path] that are then located
 *
 * @param exe The game executable
 *
 * @throws [IllegalArgumentException] if `exe` does not point to an existing file,
 * it does not point to an executable file, or the resource directory cannot be located
 */
internal class GameData(exe: Path) {
    /**
     * The [Path] to the game executable
     */
    val executable: Path = exe.toAbsolutePath()

    /**
     * The specific variant of the Kero Blaster engine being used for this game
     */
    val gameType: GameType

    /**
     * The [Path] to the resource directory for the game
     */
    val resourceDirectory: Path

    init {
        require(Files.isRegularFile(executable)) { "executable is not a file (exe: $executable)" }
        require(executable.lowerCaseExtension == "exe") { "executable is not an exe file (exe: $executable)" }

        val dirTypeMap = GameType.values().associateBy(GameType::RESOURCES_DIRECTORY)

        this.gameType =
                executable.parent.toFile()
                        .walk()
                        .map(File::toPath)
                        .filter { Files.isDirectory(it) }
                        .find { it.lowerCaseName in dirTypeMap.keys }
                        ?.let { dirTypeMap[it.lowerCaseName] }
                ?: throw IllegalArgumentException("could not locate resources directory (exe: $executable)")

        this.resourceDirectory = executable.resolveSibling(gameType.RESOURCES_DIRECTORY)
    }

    /**
     * Stores all of the [Paths][Path] to the music files for the game
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val music: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    /**
     * Stores all of the [Paths][Path] to the field files for the game
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val fields: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    /**
     * Stores all of the [Paths][Path] to the image files for the game that are not spritesheets nor tilesets
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val images: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    /**
     * Stores all of the [Paths][Path] to the spritesheet files for the game
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val spritesheets: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    /**
     * Stores all of the [Paths][Path] to the tileset files for the game
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val tilesets: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    /**
     * Stores all of the [Paths][Path] to the tileset attribute files for the game
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val tileAttributes: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    /**
     * Stores all of the [Paths][Path] to the sound effect files for the game
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val sfx: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    /**
     * Stores all of the [Paths][Path] to the script files for the game
     *
     * Sorted by ascending alphabetical order
     *
     * @throws [IllegalArgumentException] if an attempt is made to set an element to an invalid value as per [isValidForSet]
     */
    val scripts: ObservableSet<Path> by ValidatedSortedObservablePathSetDelegate

    private object ValidatedSortedObservablePathSetDelegate {
        operator fun provideDelegate(thisRef: GameData, property: KProperty<*>) =
                object : ReadOnlyProperty<GameData, ObservableSet<Path>> {
                    private val set: ObservableSet<Path>

                    init {
                        val setProp = propTypeMap.keys.first { it.name == property.name }
                        set = validatedSortedObservableSet { validateForSet(it, setProp) }
                    }

                    override fun getValue(thisRef: GameData, property: KProperty<*>) = set
                }
    }

    init {
        // In the code below, six variables are destructured but component6() isn't defined in the standard library
        operator fun <T> List<T>.component6() = get(5)

        val types = arrayOf(
                MUSIC_DIRECTORY to MUSIC_EXTENSION,
                FIELD_DIRECTORY to FIELD_EXTENSION,
                IMAGE_DIRECTORY to IMAGE_EXTENSION,
                TILE_ATTRIBUTE_DIRECTORY to TILE_ATTRIBUTE_EXTENSION,
                SFX_DIRECTORY to SFX_EXTENSION,
                SCRIPT_DIRECTORY to SCRIPT_EXTENSION
        )

        val (music, fields, allImages, tileAttributes, sfx, scripts) =
                resourceDirectory.toFile()
                        .walk()
                        .map(File::toPath)
                        // These are the directories for each resource type (e.g. the music directory)
                        .filter { Files.isDirectory(it) }
                        // These are all of the individual resource files
                        .flatMap {
                            it.toFile()
                                    .walk()
                                    .map(File::toPath)
                                    .filter { Files.isRegularFile(it) && it.nameSansExtension.isValidName() }
                        }
                        // Group each resource file by their specific type
                        .groupBy { it.parent.lowerCaseName to it.lowerCaseExtension }
                        // Filter out files of types we are not concerned with
                        .filterKeys(types::contains)
                        // Turn Lists into Sets
                        .mapValues { (_, ls) -> ls.toSet() }
                        // Add empty sets for those types for which no files were found
                        .let { it + types.filterNot(it::containsKey).associate { it to setOf<Path>() } }
                        /*
                         * Ensure yielded lists match order of types array
                         * (e.g. first type is for music so first yielded list is for music)
                         */
                        .toSortedMap(Comparator { a, b -> types.indexOf(a) - types.indexOf(b) })
                        .values
                        .toList()

        this.music += music
        this.fields += fields

        this.spritesheets += allImages.filter { it.lowerCaseName.startsWith(SPRITESHEET_PREFIX) }
        this.tilesets += allImages.filter { it.lowerCaseName.startsWith(TILESET_PREFIX) }
        this.images += (allImages - tilesets - spritesheets)

        this.tileAttributes += tileAttributes
        this.sfx += sfx
        this.scripts += scripts
    }

    val localizeData: ImmutableSet<LocalizeData> =
            resourceDirectory.resolve(LocalizeData.LOCALIZE_DIRECTORY).toFile()
                    .walk()
                    .map(File::toPath)
                    // These are the individual "lproj" directories
                    .filter { Files.isDirectory(it) && it.lowerCaseExtension == LocalizeData.LOCALIZE_PROJECT_EXTENSION }
                    .map(::LocalizeData)
                    .toSet()
                    .toImmutableSet()

    // TODO: Make inner class?
    /**
     * Stores all of the [Paths][Path] relevant to a localization of the game
     */
    class LocalizeData(languageProject: Path) {
        /**
         * The root [Path] for the localization
         */
        val root: Path = languageProject.toAbsolutePath()

        /**
         * All of the localized credit text file [Paths][Path]
         */
        val credits: ImmutableSet<Path>

        /**
         * All of the localized episode script file [Paths][Path]
         */
        val episodes: ImmutableSet<Path>

        /**
         * The explain script [Path]
         */
        val explain: Path?

        /**
         * The localize image [Path]
         */
        val localizeImage: Path?

        /**
         * The logo image [Path]
         */
        val logoImage: Path?

        /**
         * The manual text file [Path]
         */
        val manual: Path?

        /**
         * The words text file [Path]
         */
        val words: Path?

        init {
            require(root.lowerCaseExtension == LOCALIZE_PROJECT_EXTENSION)
            { "language project does not end with extension $LOCALIZE_PROJECT_EXTENSION (project: $root)" }

            val files = root.toAbsolutePath().toFile().walk().map(File::toPath).toList()

            // TODO: Consider: If mod is OLD Pink Hour, look for credit.txt
            credits = files.filter { it.lowerCaseName.matches(LocalizeData.CREDITS_FILENAME_REGEX) }.toImmutableSet()

            episodes = files.filter {
                it.lowerCaseName.matches(LocalizeData.EPISODE_SCRIPT_FILENAME_REGEX)
            }.toImmutableSet()

            explain = files.firstOrNull { it.lowerCaseName == LocalizeData.EXPLAIN_SCRIPT_FILENAME }

            localizeImage = files.firstOrNull { it.lowerCaseName == LocalizeData.LOCALIZE_IMAGE_FILENAME }

            logoImage = files.firstOrNull { it.lowerCaseName == LocalizeData.LOGO_IMAGE_FILENAME }

            // TODO: If mod is Pink Heaven, look for manual_hvn.txt
            manual = files.firstOrNull { it.lowerCaseName == LocalizeData.MANUAL_FILENAME }

            words = files.firstOrNull { it.lowerCaseName == LocalizeData.WORDS_FILENAME }
        }

        companion object {
            const val LOCALIZE_DIRECTORY = "localize"

            const val LOCALIZE_PROJECT_EXTENSION = "lproj"

            val CREDITS_FILENAME_REGEX = """credits[01]0\.txt""".toRegex()

            // const val PINK_HOUR_OLD_CREDITS_FILENAME = "credits.txt"

            val EPISODE_SCRIPT_FILENAME_REGEX = """episode[01]0\.$SCRIPT_EXTENSION""".toRegex()

            const val EXPLAIN_SCRIPT_FILENAME = "explain.$SCRIPT_EXTENSION"

            const val LOCALIZE_IMAGE_FILENAME = "localize.$IMAGE_EXTENSION"

            const val LOGO_IMAGE_FILENAME = "logo.$IMAGE_EXTENSION"

            const val MANUAL_FILENAME = "manual.txt"

            // const val PINK_HEAVEN_FILENAME = "manual_hvn.txt"

            const val WORDS_FILENAME = "words.txt"
        }
    }

    companion object {
        /**
         * If the given set is *not* [spritesheets] or [tilesets],
         * returns `true` if the name, sans extension, of `this` [Path] is valid as per [isValidName] and the
         * [parent directory][Path.getParent] and extension of `this` [Path]
         * match the intended values for files in the given set, `false` otherwise
         *
         * Otherwise, returns `true` if the above is true *and* the name of `this` [Path] starts with the
         * correct prefix for files in the given set, `false` otherwise
         */
        fun Path.isValidForSet(setProp: KProperty1<GameData, ObservableSet<Path>>): Boolean {
            val (dir, ext) = propTypeMap[setProp]!!

            return this.nameSansExtension.isValidName() &&
                   this.parent.lowerCaseName == dir &&
                   this.lowerCaseExtension == ext &&
                   when (setProp) {
                       // TODO: Consider adding branch for images (return true if name does not have prefix)
                       GameData::spritesheets -> this.lowerCaseName.startsWith(SPRITESHEET_PREFIX)
                       GameData::tilesets -> this.lowerCaseName.startsWith(TILESET_EXTENSION)
                       else -> true
                   }
        }

        // TODO: Consider using validate method and allowing diff exception types
        /**
         * Throws an [IllegalArgumentException] if the name, sans extension, of [path] is invalid as per [isValidName],
         * or the [parent directory][Path.getParent] and extension of [path]
         * do not match the intended values for files in the given set
         *
         * If the given set is [spritesheets] or [tilesets], an [IllegalArgumentException] will *also* be thrown if the
         * name of [path] does not start with the correct prefix for files in the given set
         */
        fun validateForSet(path: Path, setProp: KProperty1<GameData, ObservableSet<Path>>) {
            val (dir, ext, type) = propTypeMap[setProp]!!

            validateName(path.nameSansExtension)
            require(path.parent.lowerCaseName == dir) { "$type file is not in directory $dir (path: $path)" }
            require(path.lowerCaseExtension == ext) { "$type file is not of type ${ext.toUpperCase()} (path: $path)" }

            when (setProp) {
                // TODO: Consider adding branch for images (require that name does not have prefix)
                GameData::spritesheets ->
                    require(path.lowerCaseName.startsWith(SPRITESHEET_PREFIX))
                    { "spritesheet filename does not start with $SPRITESHEET_PREFIX (path: $path)" }

                GameData::tilesets ->
                    require(path.lowerCaseName.startsWith(TILESET_PREFIX))
                    { "tileset filename does not start with $TILESET_PREFIX (path: $path)" }
            }
        }

        const val MUSIC_DIRECTORY = "bgm"
        const val MUSIC_EXTENSION = "ptcop"

        const val FIELD_DIRECTORY = "field"
        const val FIELD_EXTENSION = "pxpack"

        const val IMAGE_DIRECTORY = "img"
        const val IMAGE_EXTENSION = "png"

        const val SPRITESHEET_DIRECTORY = IMAGE_DIRECTORY
        const val SPRITESHEET_PREFIX = "fu"
        const val SPRITESHEET_EXTENSION = IMAGE_EXTENSION

        const val TILESET_DIRECTORY = IMAGE_DIRECTORY
        const val TILESET_PREFIX = "mpt"
        const val TILESET_EXTENSION = IMAGE_EXTENSION

        const val TILE_ATTRIBUTE_DIRECTORY = IMAGE_DIRECTORY
        const val TILE_ATTRIBUTE_EXTENSION = "pxattr"

        const val SFX_DIRECTORY = "se"
        const val SFX_EXTENSION = "ptnoise"

        const val SCRIPT_DIRECTORY = "text"
        const val SCRIPT_EXTENSION = "pxeve"

        // See Path.isValidForSet and Path.validateForSet methods above
        private val propTypeMap = mapOf(
                GameData::music to Triple(MUSIC_DIRECTORY, MUSIC_EXTENSION, "music"),
                GameData::fields to Triple(FIELD_DIRECTORY, FIELD_EXTENSION, "field"),
                GameData::images to Triple(IMAGE_DIRECTORY, IMAGE_EXTENSION, "image"),
                GameData::spritesheets to Triple(SPRITESHEET_DIRECTORY, SPRITESHEET_EXTENSION, "spritesheet"),
                GameData::tilesets to Triple(TILESET_DIRECTORY, TILESET_EXTENSION, "tileset"),
                GameData::tileAttributes to Triple(TILE_ATTRIBUTE_DIRECTORY, TILE_ATTRIBUTE_EXTENSION, "tile attribute"),
                GameData::sfx to Triple(SFX_DIRECTORY, SFX_EXTENSION, "sfx"),
                GameData::scripts to Triple(SCRIPT_DIRECTORY, SCRIPT_EXTENSION, "script")
        )
    }
}

// TODO: Find a way to differentiate between Pink Hour and Pink Heaven
/**
 * Represents a different type of Kero Blaster-based game
 *
 * Although both game types are nearly the same in terms of the engine and capabilities,
 * there are some discrepancies that must be dealt with
 */
internal enum class GameType(
        /**
         * The name of the directory used for the game's resources
         */
        val RESOURCES_DIRECTORY: String
) {
    KERO_BLASTER("rsc_k"),

    PINK_HOUR("rsc_p")

    // PINK_HEAVEN("rsc_p")
}