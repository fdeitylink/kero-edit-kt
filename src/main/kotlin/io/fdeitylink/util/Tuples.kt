package io.fdeitylink.util

/**
 * Represents a tetrad of values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quadruple exhibits value semantics, i.e. two quadruples are equal if all four components are equal.
 *
 * @param A type of the first value
 * @param B type of the second value
 * @param C type of the third value
 * @param D type of the fourth value

 * @property first First value
 * @property second Second value
 * @property third Third value
 * @property fourth Fourth value
 */
data class Quadruple<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
) {
    /**
     * Returns string representation of the [Quadruple] including its [first], [second], [third], and [fourth] values
     */
    override fun toString() = "($first, $second, $third, $fourth)"
}

/**
 * Represents a pentad of values
 *
 * There is no meaning attached to values in this class, it can be used for any purpose.
 * Quadruple exhibits value semantics, i.e. two quadruples are equal if all four components are equal.
 *
 * @param A type of the first value
 * @param B type of the second value
 * @param C type of the third value
 * @param D type of the fourth value
 * @param E type of the fifth value

 * @property first First value
 * @property second Second value
 * @property third Third value
 * @property fourth Fourth value
 * @property fifth Fifth value
 */
data class Quintuple<out A, out B, out C, out D, out E>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E
) {
    /**
     * Returns string representation of the [Quintuple] including its
     * [first], [second], [third], [fourth], and [fifth] values
     */
    override fun toString() = "($first, $second, $third, $fourth, $fifth)"
}