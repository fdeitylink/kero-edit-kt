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

import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty

import javafx.beans.property.StringProperty
import javafx.beans.property.SimpleStringProperty

/**
 * Returns an [ObjectProperty] that will invoke [validator] whenever an attempt is made to set the value of the
 * returned property
 *
 * @param initialValue The initial value for the returned property
 * @param validator Should throw an exception if the given argument is invalid
 */
inline fun <T> validatedProperty(initialValue: T, crossinline validator: (T) -> Unit): ObjectProperty<T> =
        object : SimpleObjectProperty<T>(initialValue) {
            override fun set(newValue: T) {
                validator(newValue)
                super.set(newValue)
            }
        }

/**
 * Returns an [IntegerProperty] that will invoke [validator] whenever an attempt is made to set the value of the
 * returned property
 *
 * @param initialValue The initial value for the returned property
 * @param validator Should throw an exception if the given argument is invalid
 */
inline fun validatedIntegerProperty(initialValue: Int, crossinline validator: (Int) -> Unit): IntegerProperty =
        object : SimpleIntegerProperty(initialValue) {
            override fun set(newValue: Int) {
                validator(newValue)
                super.set(newValue)
            }
        }

/**
 * Returns a [StringProperty] that will invoke [validator] whenever an attempt is made to set the value of the
 * returned property
 *
 * @param initialValue The initial value for the returned property
 * @param validator Should throw an exception if the given argument is invalid
 */
inline fun validatedStringProperty(initialValue: String, crossinline validator: (String) -> Unit): StringProperty =
        object : SimpleStringProperty(initialValue) {
            override fun set(newValue: String) {
                validator(newValue)
                super.set(newValue)
            }
        }