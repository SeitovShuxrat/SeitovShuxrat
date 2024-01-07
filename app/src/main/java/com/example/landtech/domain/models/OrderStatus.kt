package com.example.landtech.domain.models

enum class OrderStatus(val value: String) {
    NEW("Новый"),
    IN_WORK("В работе"),
    ENDED("Завершена"),
    CLOSED("Закрыта")
}