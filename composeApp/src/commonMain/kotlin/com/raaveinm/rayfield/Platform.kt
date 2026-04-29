package com.raaveinm.rayfield

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform