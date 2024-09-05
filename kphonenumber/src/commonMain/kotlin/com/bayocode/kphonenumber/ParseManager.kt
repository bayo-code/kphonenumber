package com.bayocode.kphonenumber

import kotlinx.coroutines.runBlocking

class ParseManager(
    private val metadataManager: MetadataManager,
    private val regexManager: RegexManager,
) {
    private val phoneNumberParser: PhoneNumberParser = PhoneNumberParser(
        regex = regexManager,
        metadata = metadataManager
    )

    @Throws(PhoneNumberException::class, Exception::class)
    fun parse(numberString: String, region: String, ignoreType: Boolean): PhoneNumber = runBlocking {
        val region = region.uppercase()
        var nationalNumber = numberString
        val match = regexManager.phoneDataDetectorMatch(numberString)
        val matchedNumber = nationalNumber.substring(match.range)
        nationalNumber = regexManager.stringByReplacingOccurrences(matchedNumber, PhoneNumberPatterns.allNormalizationMappings, true)
        var numberExtension: String? = null
        val (tmp, rawExtension) = phoneNumberParser.stripExtension(nationalNumber)
        nationalNumber = tmp
        rawExtension?.let { numberExtension = phoneNumberParser.normalizePhoneNumber(it) }
        
        var regionMetadata = metadataManager.filterTerritories(region) ?: throw InvalidCountryCodeException()
        var countryCode: Int
        try {
            val nationalNumberBuilder = StringBuilder().also { it.append(nationalNumber) }
            countryCode = phoneNumberParser.extractCountryCode(nationalNumber, nationalNumberBuilder, regionMetadata)
            nationalNumber = nationalNumberBuilder.toString()
        } catch(e: Exception) {
            e.printStackTrace()
            val nationalNumberBuilder = StringBuilder().also { it.append(nationalNumber) }
            val plusRemovedNumberString = regexManager.replaceStringByRegex(PhoneNumberPatterns.leadingPlusCharsPattern, nationalNumber)
            countryCode = phoneNumberParser.extractCountryCode(plusRemovedNumberString, nationalNumberBuilder, regionMetadata)
            nationalNumber = nationalNumberBuilder.toString()
        }

        nationalNumber = phoneNumberParser.normalizePhoneNumber(nationalNumber)
        if (countryCode == 0) {
            if (nationalNumber.startsWith(regionMetadata.countryCode.toString())) {
                val potentialNationalNumber = nationalNumber.drop(regionMetadata.countryCode.toString().length)
                val result = runCatching { parse(potentialNationalNumber, regionMetadata.codeID, ignoreType) }.getOrNull()
                if (result != null) {
                    return@runBlocking result
                }
            }

            val result = runCatching { validPhoneNumber(nationalNumber, regionMetadata, regionMetadata.countryCode, ignoreType, numberString, numberExtension) }
                .getOrNull()
            if (result == null) throw InvalidNumberException("$numberString, $nationalNumber")
            return@runBlocking result
        }
        
        if (countryCode != regionMetadata.countryCode) {
            metadataManager.mainTerritory(countryCode)?.let {
                regionMetadata = it
            }
        }

        val result = runCatching { validPhoneNumber(nationalNumber, regionMetadata, countryCode, ignoreType, numberString, numberExtension) }.getOrNull()
        if (result != null) return@runBlocking result
        
        val possibleResults: MutableSet<PhoneNumber> = mutableSetOf()
        metadataManager.filterTerritories(countryCode)?.let { metadataList ->
            val filteredMetadataList = metadataList.filter { regionMetadata.codeID != it.codeID }
            for (metadata in filteredMetadataList) {
                runCatching { validPhoneNumber(nationalNumber, metadata, countryCode, ignoreType, numberString, numberExtension) }.getOrNull()?.let { possibleResults.add(it) }
            }
        }

        return@runBlocking when (possibleResults.size) {
            0 -> throw InvalidNumberException(numberString)
            1 -> possibleResults.first()
            else -> throw AmbiguousNumberException(possibleResults)
        }
    }
    
    fun getRegionCode(nationalNumber: Long, countryCode: Int, leadingZero: Boolean): String? {
        val regions = metadataManager.filterTerritories(countryCode) ?: return null
        
        if (regions.size == 1) return regions[0].codeID
        
        val nationalNumberString = nationalNumber.toString()
        for (region in regions) {
            region.leadingDigits?.let { leadingDigits ->
                if (regexManager.matchesAtStart(leadingDigits, nationalNumberString)) {
                    return region.codeID
                }
            }
            
            if (leadingZero && phoneNumberParser.checkNumberType("0$nationalNumberString", region) != PhoneNumberType.Unknown) {
                return region.codeID
            }
            
            if (phoneNumberParser.checkNumberType(nationalNumberString, region) != PhoneNumberType.Unknown) {
                return region.codeID
            }
        }
        
        return null
    }
    
    @Throws(PhoneNumberException::class, Exception::class)
    private fun validPhoneNumber(nationalNumber: String, regionMetadata: MetadataTerritory, countryCode: Int, ignoreType: Boolean, numberString: String, numberExtension: String?): PhoneNumber? {
        val nationalNumber = StringBuilder().also { it.append(nationalNumber) }
        var regionMetadata = regionMetadata
        
        phoneNumberParser.stripNationalPrefix(nationalNumber, regionMetadata)
        
        regionMetadata.generalDesc?.let { generalNumberDesc ->
            if (!regexManager.hasValue(generalNumberDesc.nationalNumberPattern) || !phoneNumberParser.isNumberMatchingDesc(nationalNumber.toString(), generalNumberDesc)) {
                return null
            }
        }
        
        val leadingZero = nationalNumber.startsWith("0")
        val finalNationalNumber = nationalNumber.toString().toLongOrNull() ?: throw InvalidNumberException(numberString)
        var type: PhoneNumberType = PhoneNumberType.Unknown
        if (!ignoreType) {
            getRegionCode(finalNationalNumber, countryCode, leadingZero)?.let { regionCode ->
                metadataManager.filterTerritories(regionCode)?.let { foundMetadata ->
                    regionMetadata = foundMetadata
                }
            }
            type = phoneNumberParser.checkNumberType(nationalNumber.toString(), regionMetadata, leadingZero)
            if (type == PhoneNumberType.Unknown) throw InvalidNumberException(numberString)
        }
        
        return PhoneNumber(
            numberString = numberString,
            countryCode = countryCode,
            leadingZero = leadingZero,
            nationalNumber = finalNationalNumber,
            numberExtension = numberExtension,
            type = type,
            regionID = regionMetadata.codeID
        )
    }
}
