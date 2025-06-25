package com.cielo.ordermanager.sdk.utils

enum class Status {
    DRAFT, ENTERED, CANCELED, PAID,
    APPROVED, REJECTED, RE_ENTERED, CLOSED;

    private fun allowTransactionEdition(): Boolean = this == ENTERED || this == RE_ENTERED

    fun allowMarkAsPaid(): Boolean = allowTransactionEdition()

    fun allowItemEdition(): Boolean = this == DRAFT

    fun allowEntering(): Boolean = this == DRAFT

    fun allowCancellation(): Boolean {
        return this == DRAFT ||
                this == ENTERED ||
                this == PAID ||
                this == APPROVED
    }

    fun allowApproval(): Boolean = this == PAID

    fun allowRejection(): Boolean = this == PAID

    fun allowReEntering(): Boolean = this == REJECTED || this == CLOSED

    fun allowClosing(): Boolean {
        return this == APPROVED ||
                this == REJECTED ||
                this == RE_ENTERED ||
                this == PAID
    }
}