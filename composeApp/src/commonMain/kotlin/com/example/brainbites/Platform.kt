package com.example.brainbites

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform