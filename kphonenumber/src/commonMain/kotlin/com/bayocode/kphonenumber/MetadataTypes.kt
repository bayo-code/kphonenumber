package com.bayocode.kphonenumber

data class MetadataTerritory(
    val codeID: String,
    val countryCode: Int,
    val internationalPrefix: String? = null,
    val mainCountryForCode: Boolean = false,
    val nationalPrefix: String? = null,
    val nationalPrefixFormattingRule: String? = null,
    val nationalPrefixForParsing: String?,
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

data class MetadataPhoneNumberDesc(
    val exampleNumber: String? = null,
    val nationalNumberPattern: String? = null,
    val possibleNumberPattern: String? = null,
    val possibleLengths: MetadataPossibleLengths? = null,
)

data class MetadataPossibleLengths(
    val national: String? = null,
    val localOnly: String? = null
)

data class MetadataPhoneNumberFormat(
    val pattern: String? = null,
    val format: String? = null,
    val intlFormat: String? = null,
    val leadingDigitsPatterns: List<String>? = null,
    val nationalPrefixFormattingRule: String? = null,
    val nationalPrefixOptionalWhenFormatting: Boolean? = null,
    val domesticCarrierCodeFormattingRule: String? = null,
)

data class PhoneNumberMetadataWrapper(
    val phoneNumberMetadata: PhoneNumberMetadata
)

data class PhoneNumberMetadata(
    val territories: PhoneNumberMetadataTerritories
)

data class PhoneNumberMetadataTerritories(
    val territory: List<MetadataTerritory>
)

data class MetadataAvailableFormat(
    val numberFormat: List<MetadataPhoneNumberFormat>,
)

fun List<MetadataPhoneNumberFormat>.withDefaultNationalPrefixFormattingRule(nationalPrefixFormattingRule: String?): List<MetadataPhoneNumberFormat> {
    return map { format ->
        var modifiedFormat = format
        if (modifiedFormat.nationalPrefixFormattingRule == null) {
            modifiedFormat = modifiedFormat.copy(nationalPrefixFormattingRule = nationalPrefixFormattingRule)
        }
        modifiedFormat
    }
}
