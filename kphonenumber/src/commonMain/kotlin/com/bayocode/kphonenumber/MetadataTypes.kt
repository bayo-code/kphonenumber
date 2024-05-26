package com.bayocode.kphonenumber

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class MetadataTerritory(
    @SerialName("id")
    val codeID: String,
    val countryCode: Int,
    val internationalPrefix: String? = null,
    val mainCountryForCode: Boolean = false,
    val nationalPrefix: String? = null,
    val nationalPrefixFormattingRule: String? = null,
    val nationalPrefixForParsing: String? = null,
    val nationalPrefixTransformRule: String? = null,
    val preferredExtnPrefix: String? = null,
    val emergency: MetadataPhoneNumberDesc? = null,
    val fixedLine: MetadataPhoneNumberDesc? = null,
    val generalDesc: MetadataPhoneNumberDesc? = null,
    val mobile: MetadataPhoneNumberDesc? = null,
    val pager: MetadataPhoneNumberDesc? = null,
    val personalNumber: MetadataPhoneNumberDesc? = null,
    val premiumRate: MetadataPhoneNumberDesc? = null,
    val sharedCost: MetadataPhoneNumberDesc? = null,
    val tollFree: MetadataPhoneNumberDesc? = null,
    val voicemail: MetadataPhoneNumberDesc? = null,
    val voip: MetadataPhoneNumberDesc? = null,
    val uan: MetadataPhoneNumberDesc? = null,
    val availableFormats: MetadataAvailableFormat? = null,
    val leadingDigits: String? = null,
) {
    val numberFormats: List<MetadataPhoneNumberFormat>
        get() = availableFormats?.numberFormat?.withDefaultNationalPrefixFormattingRule(nationalPrefixFormattingRule) ?: emptyList()
}

@Serializable
data class MetadataPhoneNumberDesc(
    val exampleNumber: String? = null,
    val nationalNumberPattern: String? = null,
    val possibleNumberPattern: String? = null,
    val possibleLengths: MetadataPossibleLengths? = null,
)

@Serializable
data class MetadataPossibleLengths(
    val national: String? = null,
    val localOnly: String? = null
)

@Serializable
data class MetadataPhoneNumberFormat(
    val pattern: String? = null,
    val format: String? = null,
    val intlFormat: String? = null,
    val leadingDigits: JsonElement? = null,
    val nationalPrefixFormattingRule: String? = null,
    val nationalPrefixOptionalWhenFormatting: Boolean? = null,
    @SerialName("carrierCodeFormattingRule")
    val domesticCarrierCodeFormattingRule: String? = null,
) {
    val leadingDigitsPatterns: List<String>?
        get() = when (leadingDigits) {
            is JsonArray -> leadingDigits.jsonArray.map { it.jsonPrimitive.content }
            is JsonPrimitive -> listOf(leadingDigits.content)
            is JsonObject -> error("Unexpected")
            JsonNull -> error("Unexpected")
            null -> null
        }
}

@Serializable
data class PhoneNumberMetadataWrapper(
    val phoneNumberMetadata: PhoneNumberMetadata
)

@Serializable
data class PhoneNumberMetadata(
    val territories: PhoneNumberMetadataTerritories
)

@Serializable
data class PhoneNumberMetadataTerritories(
    val territory: List<MetadataTerritory>
)

@Serializable
data class MetadataAvailableFormat(
    @SerialName("numberFormat")
    val _numberFormat: JsonElement,
) {
    val numberFormat: List<MetadataPhoneNumberFormat>
        get() {
            return when (_numberFormat) {
                is JsonArray -> Json.decodeFromJsonElement(_numberFormat)
                is JsonObject -> listOf(Json.decodeFromJsonElement<MetadataPhoneNumberFormat>(_numberFormat))
                else -> throw IllegalStateException("Invalid number format!")
            }
        }
}

fun List<MetadataPhoneNumberFormat>.withDefaultNationalPrefixFormattingRule(nationalPrefixFormattingRule: String?): List<MetadataPhoneNumberFormat> {
    return map { format ->
        var modifiedFormat = format
        if (modifiedFormat.nationalPrefixFormattingRule == null) {
            modifiedFormat = modifiedFormat.copy(nationalPrefixFormattingRule = nationalPrefixFormattingRule)
        }
        modifiedFormat
    }
}
