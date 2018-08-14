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

package io.fdeitylink.util

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty

import javafx.beans.property.StringProperty
import javafx.beans.property.SimpleStringProperty

import kotlin.reflect.KMutableProperty1

/*
 * All of these methods are copy/pasted from tornadofx's Properties.kt file, but the declarations and/or implementations
 * are slightly different
 */

/**
 * Convert an owner instance and a corresponding [Int] property reference into an observable
 */
fun <S> S.observable(prop: KMutableProperty1<S, Int>) = observable(this, prop)

/**
 * Convert an owner instance and a corresponding [Int] property reference into an observable
 */
@JvmName("observableFromMutableProperty")
fun <S> observable(owner: S, prop: KMutableProperty1<S, Int>): IntegerProperty {
    return object : SimpleIntegerProperty(owner, prop.name) {
        override fun get() = prop.get(owner)
        override fun set(v: Int) = prop.set(owner, v)
    }
}

/**
 * Convert an owner instance and a corresponding [String] property reference into an observable
 */
fun <S> S.observable(prop: KMutableProperty1<S, String>) = observable(this, prop)

/**
 * Convert an owner instance and a corresponding [String] property reference into an observable
 */
@JvmName("observableFromMutableProperty")
fun <S> observable(owner: S, prop: KMutableProperty1<S, String>): StringProperty {
    return object : SimpleStringProperty(owner, prop.name) {
        override fun get() = prop.get(owner)
        override fun set(v: String) = prop.set(owner, v)
    }
}