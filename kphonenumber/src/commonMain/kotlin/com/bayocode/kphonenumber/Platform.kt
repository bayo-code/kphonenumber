package com.bayocode.kphonenumber

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform