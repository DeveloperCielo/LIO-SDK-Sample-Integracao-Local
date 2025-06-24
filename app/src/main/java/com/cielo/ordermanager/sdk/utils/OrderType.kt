package com.cielo.ordermanager.sdk.utils

enum class OrderType(internal var value: String) {
    PAYMENT("PAYMENT"),
    AUTHORIZATION("AUTHORIZATION");

    fun identifier(): String = value

    companion object {
        fun from(type: String?): OrderType =
            if (type != null && AUTHORIZATION.identifier() == type) AUTHORIZATION else PAYMENT
    }
}
