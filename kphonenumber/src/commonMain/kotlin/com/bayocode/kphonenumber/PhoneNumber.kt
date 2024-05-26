package com.bayocode.kphonenumber

import kotlinx.serialization.Serializable

@Serializable
data class PhoneNumber(
    val numberString: String,
    val countryCode: Int,
    val leadingZero: Boolean,
    val nationalNumber: Long,
    val numberExtension: String? = null,
    val type: PhoneNumberType,
    val regionID: String? = null
) {
    companion object {
        fun notPhoneNumber(): PhoneNumber {
            return PhoneNumber(
                numberString = "",
                countryCode = 0,
                leadingZero = false,
                nationalNumber = 0,
                numberExtension = null,
                type = PhoneNumberType.NotParsed,
                regionID = null
            )
        }
    }
    
    fun notParsed(): Boolean {
        return type == PhoneNumberType.NotParsed
    }
    
    val url: String = "tel://$numberString"
    
    fun adjustedNationalNumber(): String {
        return if (leadingZero) {
            "0$nationalNumber"
        } else {
            nationalNumber.toString()
        }
    }
}
