package com.example.landtech.domain.models

enum class OrderStatus(val value: String) {
    PENDING_APPROVAL("На согласовании"),
    IN_RESERVE("К выполнению / В резерве"),
    TO_SHIP("К отгрузке"),
    CLOSED("Закрыт")
}