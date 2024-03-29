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

import java.util.Objects

import tornadofx.getValue
import tornadofx.setValue

import io.fdeitylink.util.validate

import io.fdeitylink.util.validatedIntegerProperty
import io.fdeitylink.util.validatedStringProperty

import io.fdeitylink.kero.validateName

/**
 * Represents an individual unit in a PxPack field
 *
 * @constructor
 * Constructs a new [PxUnit] object, using each argument to initialize the corresponding properties
 *
 * @throws [IllegalArgumentException] if an argument has an invalid value as per its corresponding property's documentation
 */
internal class PxUnit(
        flags: Byte,
        type: Int,
        unknownByte: Byte,
        x: Int,
        y: Int,
        unknownBytes: Pair<Byte, Byte>,
        name: String
) {
    init {
        validateType(type)
        validateCoordinate(x)
        validateCoordinate(y)
        validateName(name, "unit")
    }

    /**
     * Potentially represents a set of flags for this unit
     */
    @Suppress("CanBePrimaryConstructorProperty")
    var flags: Byte = flags

    val typeProperty = validatedIntegerProperty(type) { validateType(it) }

    // TODO: Change this from Int to Type after determining how many unit types exist and finishing the Type enum class
    /**
     * Represents the specific type of this unit
     *
     * Serves as a zero-based index into the unittype.txt file, which provides the actual type
     *
     * @throws [IllegalArgumentException] if an attempt is made to set it to an invalid value as per [isValidType]
     */
    var type: Int by typeProperty

    /**
     * A byte whose purpose is unknown
     */
    @Suppress("CanBePrimaryConstructorProperty")
    var unknownByte: Byte = unknownByte

    val xProperty = validatedIntegerProperty(x) { validateCoordinate(it) }

    /**
     * The x-coordinate of this unit in a PxPack field
     *
     * @throws [IllegalArgumentException] if an attempt is made to set it to an invalid value as per [isValidCoordinate]
     */
    var x: Int by xProperty

    val yProperty = validatedIntegerProperty(y) { validateCoordinate(it) }

    /**
     * The y-coordinate of this unit in a PxPack field
     *
     * @throws [IllegalArgumentException] if an attempt is made to set it to an invalid value as per [isValidCoordinate]
     */
    var y: Int by yProperty

    /**
     * A set of two bytes whose purpose is unknown
     */
    @Suppress("CanBePrimaryConstructorProperty")
    var unknownBytes: Pair<Byte, Byte> = unknownBytes

    val nameProperty = validatedStringProperty(name) { validateName(it, "unit") }

    /**
     * The name of this unit, for use in scripts
     *
     * @throws [IllegalArgumentException] if an attempt is made to set it to an invalid value as per
     * [isValidName][io.fdeitylink.kero.isValidName]
     */
    var name: String by nameProperty

    override fun equals(other: Any?) =
            (this === other) ||
            (other is PxUnit &&
             flags == other.flags &&
             type == other.type &&
             unknownByte == other.unknownByte &&
             x == other.x &&
             y == other.y &&
             unknownBytes == other.unknownBytes &&
             name == other.name)

    override fun hashCode() = Objects.hash(flags, type, unknownByte, x, y, unknownBytes, name)

    override fun toString() =
            "PxUnit(" +
            "flags=$flags," +
            "type=$type," +
            "unknownByte=$unknownByte," +
            "x=$x," +
            "y=$y," +
            "unknownBytes=$unknownBytes," +
            "name='$name'" +
            ")"

    companion object {
        // TODO: Consider moving this constant to PxPack
        /**
         * The maximum number of units in a PxPack field
         */
        const val MAXIMUM_NUMBER_OF_UNITS = 0xFFFF

        // Currently investigating valid range - unittype.txt lists 175 types, but testing suggests at least 250 exist
        /**
         * The valid range for any [PxUnit's][PxUnit] [type] to occupy
         */
        val UNIT_TYPE_RANGE = 0..0xFF //0..174

        /**
         * The valid range for the [x] and [y] coordinates of any [PxUnit] to occupy
         */
        val COORDINATE_RANGE = TileLayer.DIMENSION_RANGE.first until TileLayer.DIMENSION_RANGE.endInclusive

        /**
         * Returns `true` if `this` type is in the range [UNIT_TYPE_RANGE], `false` otherwise
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun Int.isValidType() = this in UNIT_TYPE_RANGE

        /**
         * Constructs and throws an exception (using [exceptCtor]) if [type] is outside the range [UNIT_TYPE_RANGE]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateType(type: Int, exceptCtor: (String) -> Exception = ::IllegalArgumentException) =
                validate(type in UNIT_TYPE_RANGE, exceptCtor) { "type must be in range $UNIT_TYPE_RANGE (type: $type)" }

        /**
         * Returns `true` if `this` coordinate is in the range [COORDINATE_RANGE], `false` otherwise
         */
        @Suppress("NOTHING_TO_INLINE")
        inline fun Int.isValidCoordinate() = this in COORDINATE_RANGE

        /**
         * Constructs and throws an exception (using [exceptCtor] if [coordinate] is outside the range [COORDINATE_RANGE]
         *
         * @param exceptCtor Defaults to the [IllegalArgumentException] constructor
         */
        fun validateCoordinate(coordinate: Int, exceptCtor: (String) -> Exception = ::IllegalArgumentException) =
                validate(coordinate in COORDINATE_RANGE, exceptCtor)
                { "coordinate must be in range $COORDINATE_RANGE (coordinate: $coordinate)" }
    }

    /**
     * Enum class representing all unit types listed in unittype.txt
     */
    enum class Type {
        DASH,

        EMPTY,

        TRASH_BOX,

        /**
         * p2: 0=stop 1=mv-h 2=mv-v 3=mv-H 4=mv-V 10=mem block
         */
        EVENT,

        ANCHOR,

        /**
         * p2: parts / Flag: On=Close / Name: L=left or U,R,D
         */
        SHUTTER,

        LONG_WEED,

        /**
         * Ventilation fan 1
         */
        KANKISEN_1,

        TRUNK,

        TEST,

        BLOCK_GENERATOR,

        KUROBO_DASH,

        SNOW_TRUNK,

        CAT_AND_FROG_STEP,

        GEARSHIFT,

        TIMER_KUN,

        /**
         * Blacksmith
         */
        KAJIYA,

        ENDLESS_ICE,

        MINI_SPIRAL,

        ELEVATOR_DOOR,

        BIG_PRESS,

        TRAIN,

        DROP,

        GREEN_FISH,

        SHIELD_PLANT,

        /**
         * Mini crab
         */
        KANI_MINI,

        RELAY,

        TO_L2,

        TO_U2,

        TO_R2,

        TO_D2,

        FIRE_PLANT,

        LADDER_BOX,

        DEBUG_LINK,

        COMBO_CUE,

        KINKO,

        THE_TIRE,

        DORO_HOLE,

        HEART,

        DORO_JUMP,

        LIFT_V3,

        ICE_SPIKE,

        CAMP_GATE,

        /**
         * Little uzo
         */
        CHIBI_UZO,

        UZO_VOID_LAYER_0,

        TAR,

        BLOCK_EATER,

        UZO_VOID_LAYER_1,

        HIDDEN_CHEST,

        CALL_MISSILE_BIRD_7,

        ROUGE,

        /**
         * Crab 7
         */
        KANI_7,

        /**
         * Little crab 7
         */
        KANI_CHIBI_7,

        PHONE,

        NURSE,

        FIRE_BUBBLE_SET,

        MOLE,

        UZO_VOID_LAYER_2,

        COMIC_BALLOON,

        ROLLING_FIBER,

        BARBED_WIRE,

        DOMINO,

        EXPLAIN,

        DOOR_A,

        WALKING_BLACK,

        /**
         * 0=Nrml V 1=UnderMPC
         */
        FOCUS_CONTROL,

        /**
         * prm2: 0=左 ("left") 1=上 ("up") 2=右 ("right") 3=下 ("down")
         */
        UZO_FLAME,

        CREEPER,

        SHACHO_ACT_STAGE_7_OFFICE,

        ROOM_LOCKER,

        BENT_PLATE,

        BOSS_DESK,

        BARRICADE,

        UZO_TANK,

        ELEVATOR_00,

        /**
         * Crab
         */
        KANI,

        /**
         * Sea snail
         */
        TOKOBUSHI,

        ANEMONE,

        TO_BACK,

        POSITION_HALF_UP,

        DOOR_VERTICAL,

        LINE_CENSOR,

        ROUTE_ARROW,

        GRAY_ENEMY,

        SPIN,

        ALTAR,

        CALL_SMALL,

        SNOW_SETTER,

        ICICLE,

        UZO_SLIME,

        GOLDRUSH,

        /**
         * Shrimp flower 7
         */
        EBI_FLOWER_7,

        /**
         * Flesh fly
         */
        NIKUBAE,

        UZO_GRAY_LAYER_0,

        BLOCK_2X2,

        QUAKER,

        FLAG_MINE,

        UZO_RED,

        SAUCER,

        UZO_OVER_FENCE,

        FLUORESCENT_LAMP,

        GRAVITY_BLOCK_3,

        /**
         * Little red uzo
         */
        UZO_CHIBI_RED,

        UZO_GRAY_LAYER_2,

        SPARKER,

        BACTERIA,

        DIGESTY,

        ROOF_WORM,

        BIG_WORM,

        SILHOUETTE,

        ZOMBIE,

        LEATHER,

        WHITE_SETTER,

        /**
         * prm2: 0=左上 ("upper left") 1=右上 ("upper right") 2=左下 ("bottom left") 3=右下 ("bottom right")
         */
        EGG_WORM,

        /**
         * swt 1:key coming, 2:keep close, 3:to open 4:key short
         */
        STONE_GATE,

        FROG_PLANT,

        WEAK_WEED,

        /**
         * Shrimp flower
         */
        EBI_FLOWER,

        BUBBLE_1UP,

        SHACHO_A,

        FROZEN_FISH,

        UZO_ELECTRIC,

        FLOWER_VASE,

        GRAY_SPIRAL,

        TIME_ATTACK_DISPLAY,

        /**
         * Metal insect
         */
        METALMUSHI,

        THE_BAR,

        GRAVITY_ICE,

        EARTH_FLY,

        WALKING_MOSS,

        MOB_STAND,

        SPIKE_PLANT,

        SIDEWINDER,

        /**
         * Swordsmith
         */
        KATANAKAJI,

        CHEST_2,

        WALK_PLANT,

        BLUE_FISH,

        DRAGON_FLOWER,

        /**
         * 0:room 1:tree 2:two birds
         */
        SET_BIRDS,

        FLY_EVADE,

        GRAVITY_BLOCK_2,

        DORONKO,

        TUMBLEWEED,

        NEW_ARMS,

        BAT_CEILING,

        STRONG_BOX,

        CHANDELIER,

        BUBBLE_LIFT,

        CALL_BATS,

        BOOSTER,

        BAT_PASSING,

        ORANGE_FISH,

        /**
         * Sea urchin
         */
        ECHINUS,

        KILLER_SENSOR,

        /**
         * prm=2 Stop
         */
        MOVING_BOX,

        BOM_BOX,

        /**
         * s
         */
        EDGE_COIN,

        EDGE_MEDAL,

        FALL_LIGHT,

        INTRO,

        UZOUZO,

        OFFICE_FLY,

        SISTER,

        SASUKE,

        HANGER,

        ROLLING_SNOW,

        /**
         * prm2: 動きをバラつかせる ("Variation of movement")
         */
        BALL_FLYER,

        ROLLING_BIG,

        REFRIGERATOR,

        ICE_HILLOCK,

        BEAR_CORE,

        ANIMAL_BONE,

        /**
         * Ventilation fan 2
         */
        KANKISEN_2,

        CARTON_BOX_L,

        CARTON_BOX_S;

        companion object {
            // TODO: From testing, it would appear at least 250 types exist
            /**
             * The number of unit types that exist
             */
            const val NUMBER_OF_UNIT_TYPES = 175
        }
    }
}