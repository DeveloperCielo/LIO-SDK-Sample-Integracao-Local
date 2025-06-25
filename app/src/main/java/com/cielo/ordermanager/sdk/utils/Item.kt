package com.cielo.ordermanager.sdk.utils

data class Item(
    val sku: String,
    val name: String,
    val unitPrice: Long,
    val quantity: Int,
    val unitOfMeasure: String
)