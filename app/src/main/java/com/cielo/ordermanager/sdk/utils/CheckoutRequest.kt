package com.cielo.ordermanager.sdk.utils

import cielo.orders.domain.SubAcquirer

data class CheckoutRequest(
    val clientID: String,
    val accessToken: String,
    val value: Long,
    val paymentCode: String?,
    val installments: Int,
    val email: String,
    val merchantCode: String?,
    val reference: String,
    val items: MutableList<Item>,
    val subAcquirer: SubAcquirer? = null
)
