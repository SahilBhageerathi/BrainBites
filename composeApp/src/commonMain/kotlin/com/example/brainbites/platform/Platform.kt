package com.example.brainbites.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform