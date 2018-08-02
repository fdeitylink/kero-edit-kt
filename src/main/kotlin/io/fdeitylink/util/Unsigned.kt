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

@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "NOTHING_TO_INLINE")

package io.fdeitylink.util

import java.lang.Byte as JByte
import java.lang.Short as JShort
import java.lang.Integer as JInt
import java.lang.Long as JLong

inline infix fun Byte.shl(shift: Int) = toInt() shl shift
inline infix fun Byte.shr(shift: Int) = toInt() shr shift
inline infix fun Byte.ushr(shift: Int) = toInt() ushr shift

infix fun Byte.ucmp(other: Byte) = JByte.compareUnsigned(this, other)
infix fun Byte.ult(other: Byte) = this ucmp other < 0
infix fun Byte.ulte(other: Byte) = this ucmp other <= 0
infix fun Byte.ugt(other: Byte) = this ucmp other > 0
infix fun Byte.ugte(other: Byte) = this ucmp other >= 0

fun Byte.toUInt() = JByte.toUnsignedInt(this)
fun Byte.toULong() = JByte.toUnsignedLong(this)

inline infix fun Short.shl(shift: Int) = toInt() shl shift
inline infix fun Short.shr(shift: Int) = toInt() shr shift
inline infix fun Short.ushr(shift: Int) = toInt() ushr shift

infix fun Short.ucmp(other: Short) = JShort.compareUnsigned(this, other)
infix fun Short.ult(other: Short) = this ucmp other < 0
infix fun Short.ulte(other: Short) = this ucmp other <= 0
infix fun Short.ugt(other: Short) = this ucmp other > 0
infix fun Short.ugte(other: Short) = this ucmp other >= 0

fun Short.toUInt() = JShort.toUnsignedInt(this)
fun Short.toULong() = JShort.toUnsignedLong(this)

infix fun Int.ucmp(other: Int) = JInt.compareUnsigned(this, other)
infix fun Int.ult(other: Int) = this ucmp other < 0
infix fun Int.ulte(other: Int) = this ucmp other <= 0
infix fun Int.ugt(other: Int) = this ucmp other > 0
infix fun Int.ugte(other: Int) = this ucmp other >= 0

infix fun Int.udiv(other: Int) = JInt.divideUnsigned(this, other)
infix fun Int.urem(other: Int) = JInt.remainderUnsigned(this, other)

fun Int.toULong() = JInt.toUnsignedLong(this)

fun Int.toUString(radix: Int = 10) = JInt.toUnsignedString(this, radix)

infix fun Long.ucmp(other: Long) = JLong.compareUnsigned(this, other)
infix fun Long.ult(other: Long) = this ucmp other < 0
infix fun Long.ulte(other: Long) = this ucmp other <= 0
infix fun Long.ugt(other: Long) = this ucmp other > 0
infix fun Long.ugte(other: Long) = this ucmp other >= 0

infix fun Long.udiv(other: Long) = JLong.divideUnsigned(this, other)
infix fun Long.urem(other: Long) = JLong.remainderUnsigned(this, other)

fun Long.toUString(radix: Int = 10) = JLong.toUnsignedString(this, radix)