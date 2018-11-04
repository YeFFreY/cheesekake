package org.yeffrey.core.utils

fun String.isMaxLength(maxLength: Int): Boolean = this.length <= maxLength

fun String.isMinLength(minLength: Int): Boolean = this.length >= minLength

fun String.removeAny(char: Char) = this.filter { it != char }
