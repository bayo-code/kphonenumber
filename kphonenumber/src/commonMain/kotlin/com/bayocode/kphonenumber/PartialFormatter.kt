package com.bayocode.kphonenumber

import kotlinx.coroutines.runBlocking

class PartialFormatter(
    kPhoneNumber: KPhoneNumber,
    defaultRegion: String = kPhoneNumber.defaultRegionCode(),
    private var withPrefix: Boolean = true,
    private var maxDigits: Int? = null,
    private var ignoreIntlNumbers: Boolean = false
) {
    private val metadataManager: MetadataManager = kPhoneNumber.metadataManager
    private val parser: PhoneNumberParser = kPhoneNumber.parseManager.phoneNumberParser
    private val regexManager: RegexManager = kPhoneNumber.regexManager

    var defaultRegion: String = ""
        set(value) {
            field = value
            updateMetadataForDefaultRegion()
        }

    private var defaultMetadata: MetadataTerritory? = null
    private var currentMetadata: MetadataTerritory? = null
    private var prefixBeforeNationalNumber = ""
    private var shouldAddSpaceAfterNationalPrefix = false

    val currentRegion: String
        get() {
            return if (ignoreIntlNumbers && currentMetadata?.codeID == "001") {
                defaultRegion
            } else if (metadataManager.filterTerritories(defaultRegion)?.countryCode != 1) {
                currentMetadata?.codeID ?: "US"
            } else {
                if (currentMetadata?.countryCode == 1) defaultRegion else currentMetadata?.codeID ?: defaultRegion
            }
        }

    init {
        this.defaultRegion = defaultRegion
    }

    private fun updateMetadataForDefaultRegion() {
        defaultMetadata = metadataManager.filterTerritories(defaultRegion)
        println("Setting new metadata: $defaultMetadata")
        currentMetadata = defaultMetadata
    }

    fun nationalNumber(rawNumber: String): String {
        val iddFreeNumber = extractIDD(rawNumber)
        var nationalNumber = parser.normalizePhoneNumber(iddFreeNumber)
        if (prefixBeforeNationalNumber.isNotEmpty()) {
            nationalNumber = extractCountryCallingCode(nationalNumber)
        }

        nationalNumber = extractNationalPrefix(nationalNumber)

        maxDigits?.let {
            val extra = nationalNumber.length - it

            if (extra > 0) {
                nationalNumber = nationalNumber.dropLast(extra)
            }
        }

        return nationalNumber
    }

    fun formatPartial(rawNumber: String): String {
        // Always reset variables with each new raw number
        resetVariables()

        if (!isValidRawNumber(rawNumber)) return rawNumber

        val (number, pausesOrWaits) = splitNumberAndPausesOrWaits(rawNumber)

        var nationalNumber = nationalNumber(number)

        availableFormats(nationalNumber)?.let { formats ->
            applyFormat(nationalNumber, formats)?.let { formattedNumber ->
                nationalNumber = formattedNumber
            } ?: run {
                for (format in formats) {
                    val template = createFormattingTemplate(format, nationalNumber)
                    if (template != null) {
                        nationalNumber = applyFormattingTemplate(template, nationalNumber)
                        break
                    }
                }
            }
        }

        var finalNumber = ""

        if (withPrefix && prefixBeforeNationalNumber.isNotEmpty()) {
            finalNumber += prefixBeforeNationalNumber
        }

        if (
            withPrefix &&
            shouldAddSpaceAfterNationalPrefix &&
            prefixBeforeNationalNumber.isNotEmpty() &&
            prefixBeforeNationalNumber.lastOrNull() != PhoneNumberConstants.separatorBeforeNationalNumber.first()
        ) {
            finalNumber += PhoneNumberConstants.separatorBeforeNationalNumber
        }

        if (nationalNumber.isNotEmpty()) {
            finalNumber += nationalNumber
        }

        if (finalNumber.lastOrNull() == PhoneNumberConstants.separatorBeforeNationalNumber.first()) {
            finalNumber = finalNumber.dropLast(1)
        }

        finalNumber += pausesOrWaits

        return finalNumber
    }

    private fun resetVariables() {
        currentMetadata = defaultMetadata
        prefixBeforeNationalNumber = ""
        shouldAddSpaceAfterNationalPrefix = false
    }

    private fun isValidRawNumber(rawNumber: String): Boolean {
        runCatching {
            val validPartialPattern = "[+＋]?(\\s*[0-9０-９٠-٩۰-۹])+\\s*\$|${PhoneNumberPatterns.validPhoneNumberPattern}"
            val validNumberMatches = runBlocking { regexManager.regexMatches(validPartialPattern, rawNumber) }
            val validStart = regexManager.stringPositionByRegex(PhoneNumberPatterns.validStartPattern, rawNumber)

            if (validNumberMatches.isEmpty() || validStart != 0) return false
        }.onFailure {
            return false
        }
        return true
    }

    fun isNanpaNumberWithNationalPrefix(rawNumber: String): Boolean {
        if (currentMetadata?.countryCode != 1 || rawNumber.length <= 1) return false

        val firstCharacter = rawNumber[0]
        val secondCharacter = rawNumber[1]

        return firstCharacter == '1' && secondCharacter != '0' && secondCharacter != '1'
    }

    fun isFormatEligible(format: MetadataPhoneNumberFormat): Boolean {
        val phoneFormat = format.format ?: return false

        val validRegex = runBlocking { regexManager.regexWithPattern(PhoneNumberPatterns.eligibleAsYouTypePattern) }
        return validRegex.find(phoneFormat) != null
    }

    fun extractNationalPrefix(rawNumber: String): String = runBlocking {
        var processedNumber = rawNumber
        var startOfNationalNumber = 0

        // TODO: Figure out what's going on here
        if (isNanpaNumberWithNationalPrefix(rawNumber) && false) {
            prefixBeforeNationalNumber += "1 "
        } else {
            runCatching {
                currentMetadata?.nationalPrefixForParsing?.let { nationalPrefix ->
                    val escapedNationalPrefix = nationalPrefix.replace("\\d", "\\\\d")
                    val nationalPrefixPattern = PhoneNumberPatterns.nationalPrefixParsingPattern.replace(
                        "%@", escapedNationalPrefix
                    )
                    val matches = regexManager.matchedStringByRegex(nationalPrefixPattern, rawNumber)
                    matches.firstOrNull()?.let { m ->
                        startOfNationalNumber = m.length
                    }
                }
            }.onFailure {
                return@runBlocking processedNumber
            }
        }

        processedNumber = rawNumber.substring(startOfNationalNumber)
        prefixBeforeNationalNumber += rawNumber.substring(0, startOfNationalNumber)

        processedNumber
    }

    fun extractCountryCallingCode(rawNumber: String): String {
        var processedNumber = rawNumber
        if (rawNumber.isEmpty()) return rawNumber

        val numberWithoutCallingCode = StringBuilder()
        if (prefixBeforeNationalNumber.isNotEmpty() && prefixBeforeNationalNumber.first() != '+') {
            prefixBeforeNationalNumber += PhoneNumberConstants.separatorBeforeNationalNumber
        }

        parser.extractPotentialCountryCode(rawNumber, numberWithoutCallingCode)?.takeIf {
            it != 0
        }?.let { potentialCountryCode ->
            processedNumber = numberWithoutCallingCode.toString()
            currentMetadata = metadataManager.mainTerritory(potentialCountryCode)
            val potentialCountryCodeString = potentialCountryCode.toString()
            prefixBeforeNationalNumber += potentialCountryCodeString
            prefixBeforeNationalNumber += " "
        } ?: run {
            if (!withPrefix && prefixBeforeNationalNumber.isEmpty()) {
                val potentialCountryCodeString = currentMetadata?.countryCode.toString()
                prefixBeforeNationalNumber += potentialCountryCodeString
                prefixBeforeNationalNumber += " "
            }
        }
        return processedNumber
    }

    fun splitNumberAndPausesOrWaits(rawNumber: String): Pair<String, String> {
        if (rawNumber.isEmpty()) return Pair(rawNumber, "")

        val splitByComma = rawNumber.split(",", limit = 2)
        val splitBySemicolon = rawNumber.split(";", limit = 2)

        if (splitByComma[0].length != splitBySemicolon[0].length) {
            val foundCommasFirst = splitByComma[0].length < splitBySemicolon[0].length

            return if (foundCommasFirst) {
                Pair(splitByComma[0], "," + splitByComma[1])
            } else {
                Pair(splitBySemicolon[0], ";" + splitBySemicolon[1])
            }
        }

        return rawNumber to ""
    }

    fun availableFormats(rawNumber: String): List<MetadataPhoneNumberFormat>? {
        val tempPossibleFormats = mutableListOf<MetadataPhoneNumberFormat>()
        val possibleFormats = mutableListOf<MetadataPhoneNumberFormat>()

        val metadata = currentMetadata ?: return null
        var formatList = metadata.numberFormats

        if (formatList.isEmpty()) {
            formatList = metadataManager.mainTerritory(metadata.countryCode)?.numberFormats ?: listOf()
        }

        for (format in formatList) {
            if (isFormatEligible(format)) {
                tempPossibleFormats.add(format)
                format.leadingDigitsPatterns?.lastOrNull()?.let { leadingDigitPattern ->
                    if (regexManager.stringPositionByRegex(leadingDigitPattern, rawNumber) == 0) {
                        possibleFormats.add(format)
                    }
                } ?: run {
                    if (regexManager.matchesEntirely(format.pattern, rawNumber)) {
                        possibleFormats.add(format)
                    }
                }
            }
        }

        if (possibleFormats.isEmpty()) {
            possibleFormats.addAll(tempPossibleFormats)
        }

        return possibleFormats
    }

    fun applyFormat(rawNumber: String, formats: List<MetadataPhoneNumberFormat>): String? = runBlocking {
        for (format in formats) {
            val pattern = format.pattern ?: continue
            val formatTemplate = format.format ?: continue
            val patternRegExp = PhoneNumberPatterns.formatPattern.replace("%@", pattern)
            val matches = runCatching { regexManager.regexMatches(patternRegExp, rawNumber) }.getOrNull() ?: continue
            if (matches.isEmpty()) continue

            format.nationalPrefixFormattingRule?.let { nationalPrefixFormattingRule ->
                val separatorRegex = regexManager.regexWithPattern(PhoneNumberPatterns.prefixSeparatorPattern)
                val nationalPrefixMatches = separatorRegex.matches(
                    nationalPrefixFormattingRule
                )

                if (nationalPrefixMatches) {
                    shouldAddSpaceAfterNationalPrefix = true
                }
            }

            val formattedNumber = regexManager.replaceStringByRegex(
                pattern,
                rawNumber,
                formatTemplate
            )

            return@runBlocking formattedNumber
        }

        null
    }

    fun createFormattingTemplate(format: MetadataPhoneNumberFormat, rawNumber: String): String? = runBlocking {
        var numberPattern = format.pattern ?: return@runBlocking null
        val numberFormat = format.format ?: return@runBlocking null
        if (numberPattern.indexOf("|") != -1) return@runBlocking null
        val characterClassRegex = runCatching {
            regexManager.regexWithPattern(PhoneNumberPatterns.characterClassPattern)
        }.getOrElse { return@runBlocking null }
        val standaloneDigitRegex = runCatching {
            regexManager.regexWithPattern(PhoneNumberPatterns.standaloneDigitPattern)
        }.getOrElse { return@runBlocking null }

        numberPattern = characterClassRegex.replace(numberPattern, "\\\\d")
        numberPattern = standaloneDigitRegex.replace(numberPattern, "\\\\d")
        getFormattingTemplate(numberPattern, numberFormat, rawNumber)?.let { tempTemplate ->
            format.nationalPrefixFormattingRule?.let { nationalPrefixFormattingRule ->
                runCatching {
                    regexManager.regexWithPattern(PhoneNumberPatterns.prefixSeparatorPattern)
                }.getOrNull()?.let { separatorRegex ->
                    if (separatorRegex.containsMatchIn(nationalPrefixFormattingRule)) {
                        shouldAddSpaceAfterNationalPrefix = true
                    }
                }
            }

            return@runBlocking tempTemplate
        }

        null
    }

    fun getFormattingTemplate(numberPattern: String, numberFormat: String, rawNumber: String): String? = runBlocking {
        val matches = regexManager.matchedStringByRegex(numberPattern, PhoneNumberConstants.longPhoneNumber)
        val match = matches.firstOrNull() ?: return@runBlocking null

        if (match.length < rawNumber.length) {
            return@runBlocking null
        }

        var template = regexManager.replaceStringByRegex(numberPattern, match, numberFormat)
        template = regexManager.replaceStringByRegex("9", template, PhoneNumberConstants.digitPlaceholder)

        template
    }

    fun applyFormattingTemplate(template: String, rawNumber: String): String {
        if (rawNumber.length <= PhoneNumberConstants.minLengthForNSN) {
            return rawNumber
        }

        var rebuiltString = ""
        var rebuiltIndex = 0

        for (character in template) {
            if (character == PhoneNumberConstants.digitPlaceholder.first()) {
                if (rebuiltIndex < rawNumber.length) {
                    rebuiltString += rawNumber[rebuiltIndex]
                    rebuiltIndex += 1
                }
            } else {
                if (rebuiltIndex < rawNumber.length) {
                    rebuiltString += character
                }
            }
        }

        if (rebuiltIndex < rawNumber.length) {
            val remainingNationalNumber = rawNumber.substring(rebuiltIndex)
            rebuiltString += remainingNationalNumber
        }
        rebuiltString = rebuiltString.trim { it.isWhitespace() }

        return rebuiltString
    }

    private fun extractIDD(rawNumber: String): String {
        var processedNumber = rawNumber
        runCatching {
            val internationalPrefix = currentMetadata?.internationalPrefix ?: return processedNumber
            val prefixPattern = PhoneNumberPatterns.iddPattern.replace("%@", internationalPrefix)
            val matches = runBlocking { regexManager.matchedStringByRegex(prefixPattern, rawNumber) }
            matches.firstOrNull()?.let { match ->
                processedNumber = rawNumber.substringAfter(match)
                prefixBeforeNationalNumber = match
            }
        }
        return processedNumber
    }

}
