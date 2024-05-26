package com.bayocode.kphonenumber.utils

fun resource(path: String) = readBytes()

private suspend fun readBytes(): ByteArray {
    val classLoader = Thread.currentThread().contextClassLoader ?: (::DesktopResourceImpl.javaClass.classLoader)
    val resource = classLoader.getResourceAsStream(path)
    if (resource != null) {
        return resource.readBytes()
    } else {
        throw MissingResourceException(path)
    }
}