package com.cielo.ordermanager.sdk.utils

class Payment  {

    var id: String = ""
    var externalId: String = ""
    var description: String = ""
    var cieloCode: String = ""
    var authCode: String = ""
    var brand: String = ""
    var mask: String = ""
    var terminal: String = ""
    var amount: Long = 0
    var primaryCode: String = ""
    var secondaryCode: String = ""
    var applicationName: String = ""
    var requestDate: String = ""
    var merchantCode: String = ""
    var discountedAmount: Long = 0
    var installments: Long = 0
    var paymentFields: HashMap<String, String> = HashMap()
    var accessKey: String = ""

}