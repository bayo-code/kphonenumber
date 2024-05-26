package com.bayocode.kphonenumber

import com.bayocode.kphonenumber.utils.range
import kotlinx.coroutines.runBlocking

class PhoneNumberParser(
    private val metadata: MetadataManager,
    private val regex: RegexManager
) {
    fun normalizePhoneNumber(number: String): String {
        val normalizedMappings = PhoneNumberPatterns.allNormalizationMappings
        return regex.stringByReplacingOccurrences(number, normalizedMappings)
    }
    
    fun extractCountryCode(number: String, nationalNumber: StringBuilder, metadata: MetadataTerritory): Int {
        var fullNumber = number
        val possibleCountryIddPrefix = metadata.internationalPrefix ?: return 0
        
        val (tmp, countryCodeSource) = stripInternationalPrefixAndNormalize(fullNumber, possibleCountryIddPrefix)
        fullNumber = tmp
        
        if (countryCodeSource != PhoneNumberCountryCodeSource.DefaultCountry) {
            if (fullNumber.length <= PhoneNumberConstants.minLengthForNSN) {
                throw TooShortException()
            }
            
            return extractPotentialCountryCode(fullNumber, nationalNumber) ?: return 0
        } else {
            val defaultCountryCode = metadata.countryCode.toString()
            if (fullNumber.startsWith(defaultCountryCode)) {
                val potentialNationalNumber = StringBuilder().also {
                    it.append(fullNumber.drop(defaultCountryCode.length))
                }
                val validNumberPattern = metadata.generalDesc?.nationalNumberPattern ?: return 0
                val possibleNumberPattern = metadata.generalDesc.possibleNumberPattern ?: return 0

                stripNationalPrefix(potentialNationalNumber, metadata)
                val potentialNationalNumberStr = potentialNationalNumber.toString()
                if (runBlocking { regex.matchesEntirely(validNumberPattern, fullNumber) && regex.matchesEntirely(validNumberPattern, potentialNationalNumberStr) || !regex.testStringLengthAgainstPattern(possibleNumberPattern, fullNumber) }) {
                    nationalNumber.setLength(0)
                    nationalNumber.append(potentialNationalNumberStr)
                    return defaultCountryCode.toIntOrNull() ?: 0
                }
            }
        }
        
        return 0
    }
    
    fun extractPotentialCountryCode(fullNumber: String, nationalNumber: StringBuilder): Int? {
        if (fullNumber.length == 0 || fullNumber.substring(startIndex = 0, endIndex = 1) == "0") {
            return 0
        }
        
        val numberLength = fullNumber.length
        val maxCountryCode = PhoneNumberConstants.maxLengthCountryCode
        var startPosition = 0
        if (fullNumber.startsWith("+")) {
            if (fullNumber.length == 1) {
                return 0
            }
            
            startPosition = 1
        }
        
        for (i in 1..(numberLength - startPosition).coerceAtMost(maxCountryCode)) {
            val stringRange = IntRange(start = startPosition, endInclusive = startPosition + i)
            val subNumber = fullNumber.substring(stringRange)
            val potentialCountryCode = subNumber.toIntOrNull() ?: continue
            if (metadata.filterTerritories(potentialCountryCode) != null) {
                nationalNumber.setLength(0)
                nationalNumber.append(fullNumber.substring(i))
                return potentialCountryCode
            }
        }
        
        return 0
    }
    
    fun checkNumberType(nationalNumber: String, metadata: MetadataTerritory, leadingZero: Boolean = false): PhoneNumberType {
        if (leadingZero) {
            val type = checkNumberType("0$nationalNumber", metadata)
            if (type != PhoneNumberType.Unknown) {
                return type
            }
        }
        
        val generalNumberDesc = metadata.generalDesc ?: return PhoneNumberType.Unknown
        
        if (!regex.hasValue(generalNumberDesc.nationalNumberPattern) || !isNumberMatchingDesc(nationalNumber, generalNumberDesc)) {
            return PhoneNumberType.Unknown
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.pager)) {
            return PhoneNumberType.Pager
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.premiumRate)) {
            return PhoneNumberType.PremiumRate
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.tollFree)) {
            return PhoneNumberType.TollFree
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.sharedCost)) {
            return PhoneNumberType.SharedCost
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.voip)) {
            return PhoneNumberType.Voip
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.personalNumber)) {
            return PhoneNumberType.PersonalNumber
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.uan)) {
            return PhoneNumberType.Uan
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.voicemail)) {
            return PhoneNumberType.Voicemail
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.fixedLine)) {
            if (metadata.fixedLine?.nationalNumberPattern == metadata.mobile?.nationalNumberPattern) {
                return PhoneNumberType.FixedOrMobile
            } else if (isNumberMatchingDesc(nationalNumber, metadata.mobile)) {
                return PhoneNumberType.FixedOrMobile
            } else {
                return PhoneNumberType.FixedLine
            }
        }
        
        if (isNumberMatchingDesc(nationalNumber, metadata.mobile)) {
            return PhoneNumberType.Mobile
        }
        
        return PhoneNumberType.Unknown
    }
    
    fun isNumberMatchingDesc(nationalNumber: String, numberDesc: MetadataPhoneNumberDesc?): Boolean {
        return runBlocking { regex.matchesEntirely(numberDesc?.nationalNumberPattern, nationalNumber) }
    }
    
    fun parsePrefixAsIdd(number: String, iddPattern: String): Pair<String, Boolean> = runBlocking {
        var tempNumber = number
        if (regex.stringPositionByRegex(iddPattern, tempNumber) == 0) {
            runCatching {
                val matched =
                    regex.regexMatches(iddPattern, tempNumber).firstOrNull() ?: return@runBlocking (tempNumber to false)
                val matchedString = matched.value
                val matchEnd = matchedString.length
                val remainString = tempNumber.substring(startIndex = matchEnd)
                val regex = Regex(PhoneNumberPatterns.capturingDigitPattern, RegexOption.IGNORE_CASE)
                val matchedGroups = regex.findAll(remainString).toList()
                matchedGroups.firstOrNull()?.let { firstMatch ->
                    val digitMatched = remainString.substring(firstMatch.range)
                    if (digitMatched.isNotEmpty()) {
                        val normalizedGroup = this@PhoneNumberParser.regex.stringByReplacingOccurrences(
                            digitMatched,
                            PhoneNumberPatterns.allNormalizationMappings
                        )
                        if (normalizedGroup == "0") {
                            return@runBlocking (tempNumber to false)
                        }
                    }
                }
                tempNumber = remainString
                tempNumber to true
            }.getOrNull() ?: (tempNumber to false)
        } else tempNumber to false
    }
    
    fun stripExtension(number: String): Pair<String, String?> = runCatching {
        val matches = runBlocking { regex.regexMatches(PhoneNumberPatterns.extnPattern, number) }
        matches.firstOrNull()?.let {
            val adjustedRange = IntRange(it.range.start + 1, it.range.endInclusive - 1)
            val matchString = number.substring(adjustedRange)
            val stringRange = IntRange(0, it.range.start)
            val tmpNumber = number.substring(stringRange)
            (tmpNumber to matchString)
        } ?: (number to null)
    }.getOrElse { (number to null) }
    
    fun stripInternationalPrefixAndNormalize(number: String, possibleIddPrefix: String?): Pair<String, PhoneNumberCountryCodeSource> {
        var fullNumber = number
        if (regex.matchesAtStart(PhoneNumberPatterns.leadingPlusCharsPattern, fullNumber)) {
            fullNumber = runBlocking { regex.replaceStringByRegex(PhoneNumberPatterns.leadingPlusCharsPattern, fullNumber) }
            return fullNumber to PhoneNumberCountryCodeSource.NumberWithPlusSign
        }
        
        fullNumber = normalizePhoneNumber(fullNumber)
        possibleIddPrefix ?: return fullNumber to PhoneNumberCountryCodeSource.NumberWithoutPlusSign
        val (num, prefixResult) = parsePrefixAsIdd(fullNumber, possibleIddPrefix)
        fullNumber = num
        if (prefixResult) {
            return fullNumber to PhoneNumberCountryCodeSource.NumberWithIDD
        } else {
            return fullNumber to PhoneNumberCountryCodeSource.DefaultCountry
        }
    }
    
    fun stripNationalPrefix(number: StringBuilder, metadata: MetadataTerritory) {
        val possibleNationalPrefix = metadata.nationalPrefixForParsing ?: return

        val prefixPattern = "^(?:${possibleNationalPrefix})"

        try {
            val matches = runBlocking { regex.regexMatches(prefixPattern, number.toString()) }
            val firstMatch = matches.firstOrNull() ?: return

            val nationalNumberRule = metadata.generalDesc?.nationalNumberPattern
            val firstMatchString = number.substring(firstMatch.range.first, firstMatch.range.last + 1)
            val numOfGroups = firstMatch.groupValues.size - 1

            val firstRange = firstMatch.groups[numOfGroups]?.range ?: IntRange.EMPTY
            val firstMatchStringWithGroup = if (firstRange.first >= 0 && firstRange.last < number.length) {
                number.substring(firstRange.first, firstRange.last + 1)
            } else {
                ""
            }

            val firstMatchStringWithGroupHasValue = regex.hasValue(firstMatchStringWithGroup)
            val transformedNumber = if (metadata.nationalPrefixTransformRule != null && firstMatchStringWithGroupHasValue) {
                runBlocking { regex.replaceFirstStringByRegex(prefixPattern, number.toString(), metadata.nationalPrefixTransformRule) }
            } else {
                number.substring(firstMatchString.length)
            }

            if (regex.hasValue(nationalNumberRule)
                && runBlocking { regex.matchesEntirely(nationalNumberRule!!, number.toString()) }
                && !runBlocking { regex.matchesEntirely(nationalNumberRule, transformedNumber) }) {
                return
            }

            number.setLength(0)
            number.append(transformedNumber)

        } catch (e: Exception) {
            return
        }
    }
}
