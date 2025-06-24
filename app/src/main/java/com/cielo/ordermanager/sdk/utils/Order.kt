package com.cielo.ordermanager.sdk.utils

import java.util.Date

class Order(var id: String, var price: Long, var paidAmount: Long, var pendingAmount: Long,
                 var reference: String, var number: String, var notes: String, var status: Status,
                 var items: MutableList<Item>, var payments: MutableList<Payment>, var createdAt: Date,
                 var updatedAt: Date, var releaseDate: Date, var type: OrderType)
