package com.cielo.ordermanager.sdk.utils

class PrintRequest(
    val operation: String,
    val value: Array<String>,
    val styles: List<Map<String, Int>>
)
