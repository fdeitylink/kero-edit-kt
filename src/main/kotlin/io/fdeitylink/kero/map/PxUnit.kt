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

package io.fdeitylink.kero.map

import java.util.Objects

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString

import tornadofx.*

// TODO: Consider exposing SimpleIntegerProperty, SimpleStringProperty for coordinates, name
/**
 * Represents an individual unit in a PxPack map
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
    /**
     * Potentially represents a set of flags for this unit
     */
    var flags: Byte by property(flags)

    //fun flagsProperty() = getProperty(PxUnit::flags)

    // TODO: Change this from Int to Type after determining how many unit types exist and finishing the Type enum class
    /**
     * Represents the specific type of this unit
     *
     * Serves as a zero-based index into the unittype.txt file, which provides the actual type
     */
    var type: Int by property(type)

    fun typeProperty() = getProperty(PxUnit::type)

    /**
     * A byte whose purpose is unknown
     */
    var unknownByte: Byte by property(unknownByte)

    //fun unknownByteProperty() = getProperty(PxUnit::unknownByte)

    /**
     * The x-coordinate of this unit in a PxPack map
     */
    var x: Int by property(x)

    fun xProperty() = getProperty(PxUnit::x)

    /**
     * The y-coordinate of this unit in a PxPack map
     */
    var y: Int by property(y)

    fun yProperty() = getProperty(PxUnit::y)

    /**
     * A set of two bytes whose purpose is unknown
     */
    var unknownBytes: Pair<Byte, Byte> by property(unknownBytes)

    //fun unknownBytesProperty() = getProperty(PxUnit::unknownBytes)

    /**
     * The name of this unit, for use in scripts
     */
    var name: String by property(name)

    fun nameProperty() = getProperty(PxUnit::name)

    override fun equals(other: Any?) = kotlinEquals(other, properties)

    override fun hashCode() = Objects.hash(flags, type, unknownByte, x, y, unknownBytes, name)

    override fun toString() = kotlinToString(properties)

    companion object {
        // TODO: Consider moving this constant to PxPack
        /**
         * The maximum number of units in a PxPack map
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
         * Used for [equals][PxUnit.equals] and [hashCode][PxUnit.hashCode] methods
         */
        private val properties = arrayOf(
                PxUnit::flags,
                PxUnit::type,
                PxUnit::unknownByte,
                PxUnit::x,
                PxUnit::y,
                PxUnit::unknownBytes,
                PxUnit::name
        )

        fun Int.isValidForType() = this in UNIT_TYPE_RANGE

        fun Int.validateForType() =
                require(this in UNIT_TYPE_RANGE) { "type must be in range $UNIT_TYPE_RANGE (type: $this)" }

        fun Int.isValidForCoordinate() = this in COORDINATE_RANGE

        fun Int.validateForCoordiante() =
                require(this in COORDINATE_RANGE) { "coordinate must be in range $COORDINATE_RANGE (coordinate: $this)" }
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

/**
 * The [x][PxUnit.x] and [y][PxUnit.y] coordinates of `this` [PxUnit] as a [Pair],
 * where `x` is the first component and `y` is the second
 */
internal val PxUnit.coordinates get() = Pair(x, y)

/**
 * Returns a [Byte] representing `this` [Type's][PxUnit.Type] index in the unittype.txt file
 */
internal fun PxUnit.Type.toByte() = ordinal.toByte()