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

import java.nio.file.Path

/**
 * The [filename][Path.getFileName] of `this` [Path] as a [String]
 */
val Path.name get() = this.fileName.toString()

/**
 * The [name][Path.getFileName] of `this` [Path] in all lower case
 */
val Path.lowerCaseName get() = name.toLowerCase()

/**
 * The extension of the [name][Path.getFileName] of `this` [Path], or `""` if there is none
 */
val Path.extension get() = this.fileName.toString().substringAfterLast('.', "")

/**
 * The extension of the [name][Path.getFileName] of `this` [Path] in all lower case, or `""` if there is none
 */
val Path.lowerCaseExtension get() = extension.toLowerCase()

/**
 * The [name][Path.getFileName] of `this` [Path] minus its [extension][Path.extension]
 */
val Path.nameSansExtension get() = name.removeSuffix(extension)

/**
 * The [name][Path.getFileName] of `this` [Path] minus its [extension][Path.extension] in all lowe case
 */
val Path.lowerCaseNameSansExtension get() = nameSansExtension.toLowerCase()