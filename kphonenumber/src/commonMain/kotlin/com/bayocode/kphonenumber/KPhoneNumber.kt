package com.bayocode.kphonenumber

class KPhoneNumber {
    internal val metadataManager: MetadataManager = MetadataManager()
    internal val parseManager: ParseManager
    val regexManager = RegexManager()
    
    init {
        parseManager = ParseManager(metadataManager, regexManager)
    }
    
    @Throws(PhoneNumberException::class, Exception::class)
    fun parse(numberString: String, region: String, ignoreType: Boolean = false): PhoneNumber {
        return parseManager.parse(numberString, region, ignoreType)
    }
    
    @Throws(PhoneNumberException::class, Exception::class)
    fun isValidPhoneNumber(numberString: String, region: String, ignoreType: Boolean = false): Boolean {
        return runCatching { parse(numberString, region, ignoreType) }.getOrNull() != null
    }
    
    fun format(phoneNumber: PhoneNumber, formatType: PhoneNumberFormat, prefix: Boolean = true): String {
        when (formatType) {
            PhoneNumberFormat.E164 -> {
                val formattedNationalNumber = phoneNumber.adjustedNationalNumber()
                if (!prefix) {
                    return formattedNationalNumber
                }
                return "+${phoneNumber.countryCode}${formattedNationalNumber}"
            }
            else -> {
                val formatter = Formatter(this)
                val regionMetadata = metadataManager.mainTerritory(phoneNumber.countryCode)
                val formattedNationalNumber = formatter.format(phoneNumber, formatType, regionMetadata)
                return if (formatType == PhoneNumberFormat.International && prefix) {
                    "+${phoneNumber.countryCode} $formattedNationalNumber"
                } else {
                    formattedNationalNumber
                }
            }
        }
    }
    
    fun allCountries(): List<String> {
        return metadataManager.territories.map { it.codeID }
    }
    
    fun countries(countryCode: Int): List<String>? {
        return metadataManager.filterTerritories(countryCode)?.map { it.codeID }
    }
    
    fun mainCountry(countryCode: Int): String? {
        return metadataManager.mainTerritory(countryCode)?.codeID
    }
    
    fun countryCode(country: String): Int? {
        return metadataManager.filterTerritories(country)?.countryCode
    }
    
    fun leadingDigits(country: String): String? {
        return metadataManager.filterTerritories(country)?.leadingDigits
    }
    
    fun getRegionCode(phoneNumber: PhoneNumber): String? {
        return parseManager.getRegionCode(phoneNumber.nationalNumber, phoneNumber.countryCode, phoneNumber.leadingZero)
    }
    
    fun getExampleNumber(countryCode: String, type: PhoneNumberType): PhoneNumber? {
        val metadata = metadata(countryCode)
        val example: String = when (type) {
            PhoneNumberType.FixedLine -> metadata?.fixedLine?.exampleNumber
            PhoneNumberType.Mobile -> metadata?.mobile?.exampleNumber
            PhoneNumberType.FixedOrMobile -> metadata?.mobile?.exampleNumber
            PhoneNumberType.Pager -> metadata?.pager?.exampleNumber
            PhoneNumberType.PersonalNumber -> metadata?.personalNumber?.exampleNumber
            PhoneNumberType.PremiumRate -> metadata?.premiumRate?.exampleNumber
            PhoneNumberType.SharedCost -> metadata?.sharedCost?.exampleNumber
            PhoneNumberType.TollFree -> metadata?.tollFree?.exampleNumber
            PhoneNumberType.Voicemail -> metadata?.voicemail?.exampleNumber
            PhoneNumberType.Voip -> metadata?.voip?.exampleNumber
            PhoneNumberType.Uan -> metadata?.uan?.exampleNumber
            PhoneNumberType.Unknown -> return null
            PhoneNumberType.NotParsed -> return null
        } ?: return null
        
        try {
            return parse(example, countryCode, false)
        } catch (e: Exception) {
            e.printStackTrace()
            println("[KPhoneNumber] Failed to parse example number for $countryCode region")
            return null
        }
    }
    
    fun getFormattedExampleNumber(countryCode: String, type: PhoneNumberType = PhoneNumberType.Mobile, format: PhoneNumberFormat = PhoneNumberFormat.International, prefix: Boolean = true): String? {
        return getExampleNumber(countryCode, type)?.let { format(it, format, prefix) }
    }
    
    fun metadata(country: String): MetadataTerritory? {
        return metadataManager.filterTerritories(country)
    }
    
    fun metadata(countryCode: Int): List<MetadataTerritory>? {
        return metadataManager.filterTerritories(countryCode)
    }
    
    fun possiblePhoneNumberLengths(country: String, phoneNumberType: PhoneNumberType, lengthType: PossibleLengthType): List<Int> {
        val territory = metadataManager.filterTerritories(country) ?: return emptyList()
        val possibleLengths = possiblePhoneNumberLengths(territory, phoneNumberType)
        
        return when (lengthType) {
            PossibleLengthType.National -> possibleLengths?.national?.let { parsePossibleLengths(it) } ?: emptyList()
            PossibleLengthType.LocalOnly -> possibleLengths?.localOnly?.let { parsePossibleLengths(it) } ?: emptyList()
        }
    }
    
    fun possiblePhoneNumberLengths(territory: MetadataTerritory, phoneNumberType: PhoneNumberType): MetadataPossibleLengths? {
        return when (phoneNumberType) {
            PhoneNumberType.FixedLine -> territory.fixedLine?.possibleLengths
            PhoneNumberType.Mobile -> territory.mobile?.possibleLengths
            PhoneNumberType.FixedOrMobile -> null
            PhoneNumberType.Pager -> territory.pager?.possibleLengths
            PhoneNumberType.PersonalNumber -> territory.personalNumber?.possibleLengths
            PhoneNumberType.PremiumRate -> territory.premiumRate?.possibleLengths
            PhoneNumberType.SharedCost -> territory.sharedCost?.possibleLengths
            PhoneNumberType.TollFree -> territory.tollFree?.possibleLengths
            PhoneNumberType.Voicemail -> territory.voicemail?.possibleLengths
            PhoneNumberType.Voip -> territory.voip?.possibleLengths
            PhoneNumberType.Uan -> territory.uan?.possibleLengths
            PhoneNumberType.Unknown -> null
            PhoneNumberType.NotParsed -> null
        }
    }
    
    private fun parsePossibleLengths(lengths: String): List<Int> {
        val components = lengths.split(",")
        val results = components.fold(mutableListOf<Int>()) { result, component ->
            val newComponents = parseLengthComponent(component)
            return result + newComponents
        }
        
        return results
    }
    
    private fun parseLengthComponent(component: String): List<Int> {
        val int = component.toIntOrNull()
        if (int != null) return listOf(int)
        
        val trimmedComponent = component.trim { it in "[]" }
        val rangeLimits = trimmedComponent.split("-").mapNotNull { it.toIntOrNull() }
        if (rangeLimits.size != 2) return emptyList()
        val rangeStart = rangeLimits.first()
        val rangeEnd = rangeLimits.last()
        
        return (rangeStart..rangeEnd).toList()
    }
    
    fun defaultRegionCode(): String {
        return PhoneNumberConstants.defaultCountry
    }

    fun partialFormatter(
        defaultRegion: String = defaultRegionCode(),
        withPrefix: Boolean = true,
        maxDigits: Int? = null,
        ignoreIntlNumbers: Boolean = false
    ): PartialFormatter {
        return PartialFormatter(
            kPhoneNumber = this,
            defaultRegion = defaultRegion,
            withPrefix = withPrefix,
            maxDigits = maxDigits,
            ignoreIntlNumbers = ignoreIntlNumbers
        )
    }
}
