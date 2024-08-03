package com.example.gpstrackerapp

data class motorbikesData(
    val phoneNumber: String? = null,
    val vehicleNumber: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val tenant: tenant? = null
) {
    constructor() : this(null, null, null, null, null)
}

data class tenant(
    val Duration: Int? = null,
    val NIK: String? = null,
    val Name: String? = null,
    val Tenantphone: String? = null
) {
    constructor() : this(null, null, null, null)
}
