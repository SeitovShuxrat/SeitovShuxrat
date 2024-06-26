package com.example.landtech.data.remote.dto

data class OrderDto(
    val guid: String,
    val number: String,
    val date: String,
    val status: String,
    val partner: String,
    val workType: String,
    val machinery: String,
    val driveTime: Double = 0.0,
    val driveTimeEnd: Double = 0.0,
    val sn: String,
    val ln: String,
    val en: String,
    val driveStartDate: String,
    val driveEndDate: String,
    val workStartDate: String,
    val workEndDate: String,
    val tripEndDate: String,
    val locationStartLat: Double,
    val locationStartLng: Double,
    val locationLat: Double,
    val locationLng: Double,
    val tripEndLat: Double,
    val tripEndLng: Double,
    val problemDescription: String,
    val workDescription: String,
    val quickReport: String,
    val engineerId: String,
    val engineerName: String,
    val services: List<ServiceItemDto?>,
    val usedParts: List<UsedPartsItemDto?> = listOf(),
    val receivedParts: List<ReceivedPartsItemDto?> = listOf(),
    val returnedParts: List<ReturnedPartsItemDto?> = listOf(),
    val newSpareParts: List<NewSparePartDto?> = listOf(),
    val engineerItems: List<EngineerItemDto?> = listOf(),
    var images: List<String> = listOf(),
    var needToCreateGuaranteeOrder: Boolean = false,
    var workHasNoGuarantee: Boolean = false,
    var clientRejectedToSign: Boolean = false,
    var partsAreReceived: Boolean = false,
    var isMainUser: Boolean = false,
    var isInEngineersList: Boolean = false,

    )