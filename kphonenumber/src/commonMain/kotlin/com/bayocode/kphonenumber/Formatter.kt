package com.bayocode.kphonenumber

class Formatter(
    val regexManager: RegexManager
) {
    constructor(kPhoneNumber: KPhoneNumber): this(kPhoneNumber.regexManager)
    
    fun format(phoneNumber: PhoneNumber, formatType: PhoneNumberFormat, regionMetadata: MetadataTerritory?): String {
        var formattedNationalNumber = phoneNumber.adjustedNationalNumber()
        regionMetadata?.let { regionMetadata ->
            formattedNationalNumber = formatNationalNumber(formattedNationalNumber, regionMetadata, formatType)
            formatExtension(phoneNumber.numberExtension, regionMetadata)?.let { formattedExtension ->
                formattedNationalNumber += formattedExtension
            }
        }
        
        return formattedNationalNumber
    }
    
    fun formatExtension(numberExtension: String?, regionMetadata: MetadataTerritory): String? {
        return numberExtension?.let { extns ->
            regionMetadata.preferredExtnPrefix?.let { preferredExtnPrefix ->
                "$preferredExtnPrefix$extns"
            } ?: "${PhoneNumberConstants.defaultExtnPrefix}${extns}"
        }
    }
    
    fun formatNationalNumber(nationalNumber: String, regionMetadata: MetadataTerritory, formatType: PhoneNumberFormat): String {
        val formats = regionMetadata.numberFormats
        var selectedFormat: MetadataPhoneNumberFormat? = null
        for (format in formats) {
            var breakIt: Boolean = false
            format.leadingDigitsPatterns?.lastOrNull()?.let { leadingDigitPattern ->
                if (regexManager.stringPositionByRegex(leadingDigitPattern, nationalNumber) == 0) {
                    if (regexManager.matchesEntirely(format.pattern, nationalNumber)) {
                        selectedFormat = format
                        breakIt = true
                    }
                }
            } ?: run {
                if (regexManager.matchesEntirely(format.pattern, nationalNumber)) {
                    selectedFormat = format
                    breakIt = true
                }
            }
            
            if (breakIt) break
        }
        
        return selectedFormat?.let { formatPattern ->
            val numberFormatRule = if (formatType == PhoneNumberFormat.International && formatPattern.intlFormat != null) {
                formatPattern.intlFormat
            } else {
                formatPattern.format
            } ?: return nationalNumber
            val pattern = formatPattern.pattern ?: return nationalNumber
            
            var formattedNationalNumber = ""
            var prefixFormattingRule = ""
            formatPattern.nationalPrefixFormattingRule?.let { nationalPrefixFormattingRule ->
                val nationalPrefix = regionMetadata.nationalPrefix ?: return@let
                prefixFormattingRule = regexManager.replaceStringByRegex(PhoneNumberPatterns.npPattern, nationalPrefixFormattingRule, nationalPrefix)
                prefixFormattingRule = regexManager.replaceStringByRegex(PhoneNumberPatterns.fgPattern, prefixFormattingRule, "\\$1")
            }
            
            if (formatType == PhoneNumberFormat.National && regexManager.hasValue(prefixFormattingRule)) {
                val replacePattern = regexManager.replaceFirstStringByRegex(PhoneNumberPatterns.firstGroupPattern, numberFormatRule, prefixFormattingRule)
                formattedNationalNumber = regexManager.replaceStringByRegex(pattern, nationalNumber, replacePattern)
            } else {
                formattedNationalNumber = regexManager.replaceStringByRegex(pattern, nationalNumber, numberFormatRule)
            }
            
            formattedNationalNumber
        } ?: nationalNumber
    }
}
