package io.fdeitylink.kero.map

import io.fdeitylink.kero.validateName

/**
 * Represents an individual unit in a PxPack map
 */
internal data class PxUnit(
        /**
         * Potentially represents a set of flags for this unit
         */
        val flags: Byte,

        // TODO: Change this from Int to Type after determining how many unit types there are and finishing the Type enum class
        /**
         * Represents the specific type of this unit
         *
         * Serves as a zero-based index into the unittype.txt file, which provides the actual type
         */
        val type: Int,

        /**
         * A byte whose purpose is unknown
         */
        val unknownByte: Byte,

        /**
         * The x-coordinate of this unit in a PxPack map
         */
        val x: Int,

        /**
         * The y-coordinate of this unit in a PxPack map
         */
        val y: Int,

        /**
         * A set of two bytes whose purpose is unknown
         */
        val unknownBytes: Pair<Byte, Byte>,

        /**
         * The name of this unit, for use in scripts
         */
        val name: String
) {
    init {
        require(x in COORDINATE_RANGE && y in COORDINATE_RANGE)
        { "x & y must be in range $COORDINATE_RANGE (x: $x, y: $y)" }

        name.validateName("unit")

        require(type in UNIT_TYPE_RANGE) { "type must be in range $UNIT_TYPE_RANGE (type: $type)" }
    }

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
        val COORDINATE_RANGE = 0 until 0xFFFF
    }

    // TODO: Consider overriding toString to display values in hexadecimal notation

    /**
     * Enum class representing all unit types listed in unittype.txt
     */
    enum class Type {
        DASH,

        TRUSH_BOX,

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

        KANKISEN_1,

        TRUNK,

        TEST,

        BLOCK_GENERATOR,

        KUROBO_DASH,

        SNOW_TRUNK,

        CAT_AND_FROG_STEP,

        GEARSHIFT,

        TIMER_KUN,

        KAJIYA,

        ENDLESS_ICE,

        MINI_SPIRAL,

        ELEVATOR_DOOR,

        BIG_PRESS,

        TRAIN,

        DROP,

        GREEN_FISH,

        SHIELD_PLANT,

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

        CHIBI_UZO,

        UZO_VOID_LAYER_0,

        TAR,

        BLOCK_EATER,

        UZO_VOID_LAYER_1,

        HIDDEN_CHEST,

        CALL_MISSILE_BIRD_7,

        ROUGE,

        KANI_7,

        KANI_CHIBI_7,

        PHONE,

        NURSE,

        FIRE_BUBBLE_SET,

        MOLE,

        UZO_VOID_LAYER_2,

        COMIC_BALLOON,

        ROLLING_FIBER,

        BARBWIRE,

        DOMINO,

        EXPLAIN,

        DOOR_A,

        WALKING_BLACK,

        /**
         * 0=Nrml V 1=UnderMPC
         */
        FOCUS_CONTROL,

        /**
         * prm2: 0=左 1=上 2=右 3=下
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

        KANI,

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

        EBI_FLOWER_7,

        NIKUBAE,

        /**
         * layer0
         */
        UZO_GRAY,

        BLOCK_2X2,

        QUAKER_,

        FLAG_MINE,

        UZO_RED,

        SAUCER,

        UZO_OVER_FENCE,

        FLUO_LAMP,

        GRAVITY_BLOCK_3,

        UZO_CHIBI_RED;

        //UZO_GRAY

        /**
         * Represents a given unit type's zero-based index into the unittype.txt file
         */
        val byte = ordinal.toByte()
    }
}

/**
 * The [x][PxUnit.x] and [y][PxUnit.y] coordinates of `this` [PxUnit] as a [Pair],
 * where `x` is the first component and `y` is the second
 */
internal val PxUnit.coordinates get() = Pair(x, y)