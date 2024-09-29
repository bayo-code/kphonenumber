package com.bayocode.kphonenumber

enum class PhoneNumberCountryCodeSource {
    NumberWithPlusSign,
    NumberWithIDD,
    NumberWithoutPlusSign,
    DefaultCountry
}

open class PhoneNumberException(message: String): Exception(message)

open class GeneralPhoneNumberException: PhoneNumberException("An error occured whilst validating the phone number.")
open class InvalidCountryCodeException: PhoneNumberException("The country code is invalid.")
open class InvalidNumberException(numberString: String): PhoneNumberException("The number provided is invalid: $numberString.")
open class TooLongException: PhoneNumberException("The number provided is too long.")
open class TooShortException: PhoneNumberException("The number provided is too short.")
open class DeprecatedException: PhoneNumberException("This function is deprecated.")
open class MetadataNotFoundException: PhoneNumberException("Valid metadata is missing.")
open class AmbiguousNumberException(phoneNumbers: Set<PhoneNumber>): PhoneNumberException("Phone number is ambiguous.")

enum class PhoneNumberFormat {
    E164, // +33689123456
    International, // +33 6 89 12 34 56
    National // 06 89 12 34 56
}

/// Phone number type enumeration
/// - fixedLine: Fixed line numbers
/// - mobile: Mobile numbers
/// - fixedOrMobile: Either fixed or mobile numbers if we can't tell conclusively.
/// - pager: Pager numbers
/// - personalNumber: Personal number numbers
/// - premiumRate: Premium rate numbers
/// - sharedCost: Shared cost numbers
/// - tollFree: Toll free numbers
/// - voicemail: Voice mail numbers
/// - vOIP: Voip numbers
/// - uan: UAN numbers
/// - unknown: Unknown number type
enum class PhoneNumberType {
    FixedLine,
    Mobile,
    FixedOrMobile,
    Pager,
    PersonalNumber,
    PremiumRate,
    SharedCost,
    TollFree,
    Voicemail,
    Voip,
    Uan,
    Unknown,
    NotParsed
}

enum class PossibleLengthType {
    National,
    LocalOnly
}

object PhoneNumberConstants {
    const val defaultCountry = "US"
    const val defaultExtnPrefix = " ext. "
    const val longPhoneNumber = "999999999999999"
    const val minLengthForNSN = 2
    const val maxInputStringLength = 250
    const val maxLengthCountryCode = 3
    const val maxLengthForNSN = 16
    const val nonBreakingSpace = "\u00a0"
    const val plusChars = "+＋"
    const val pausesAndWaitsChars = ",;"
    const val operatorChars = "*#"
    const val validDigitsString = "0-9０-９٠-٩۰-۹"
    const val digitPlaceholder = "\u2008"
    const val separatorBeforeNationalNumber = " "
}

object PhoneNumberPatterns {
    const val firstGroupPattern = "(\\$\\d)"
    const val fgPattern = "\\\$FG"
    const val npPattern = "\\\$NP"

    val allNormalizationMappings = mapOf("0" to "0", "1" to "1", "2" to "2", "3" to "3", "4" to "4", "5" to "5", "6" to "6", "7" to "7", "8" to "8", "9" to "9", "٠" to "0", "١" to "1", "٢" to "2", "٣" to "3", "٤" to "4", "٥" to "5", "٦" to "6", "٧" to "7", "٨" to "8", "٩" to "9", "۰" to "0", "۱" to "1", "۲" to "2", "۳" to "3", "۴" to "4", "۵" to "5", "۶" to "6", "۷" to "7", "۸" to "8", "۹" to "9", "*" to "*", "#" to "#", "," to ",", ";" to ";")
    const val capturingDigitPattern = "([0-9０-９٠-٩۰-۹])"

    const val extnPattern = "(?:;ext=([0-9０-９٠-٩۰-۹]{1,7})|[  \\t,]*(?:e?xt(?:ensi(?:ó?|ó))?n?|ｅ?ｘｔｎ?|[,xｘX#＃~～;]|int|anexo|ｉｎｔ)[:\\.．]?[  \\t,-]*([0-9０-９٠-٩۰-۹]{1,7})#?|[- ]+([0-9０-９٠-٩۰-۹]{1,5})#)$"

    const val iddPattern = "^(?:\\+|%@)"

    const val formatPattern = "^(?:%@)$"

    const val characterClassPattern = "\\[([^\\[\\]])*\\]"

    const val standaloneDigitPattern = "\\d(?=[^,}][^,}])"

    const val nationalPrefixParsingPattern = "^(?:%@)"

    const val prefixSeparatorPattern = "[- ]"

    const val eligibleAsYouTypePattern = "^[-x‐-―−ー－-／ ­​⁠　()（）［］.\\[\\]/~⁓∼～]*(\\$\\d[-x‐-―−ー－-／ ­​⁠　()（）［］.\\[\\]/~⁓∼～]*)+$"

    const val leadingPlusCharsPattern = "^[+＋]+"

    const val secondNumberStartPattern = "[\\\\\\/] *x"

    const val unwantedEndPattern = "[^0-9０-９٠-٩۰-۹A-Za-z#]+$"

    const val validStartPattern = "[+＋0-9０-９٠-٩۰-۹]"

    const val validPhoneNumberPattern = "^[0-9０-９٠-٩۰-۹]{2}$|^[+＋]*(?:[-x\u2010-\u2015\u2212\u30FC\uFF0D-\uFF0F \u00A0\u00AD\u200B\u2060\u3000()\uFF08\uFF09\uFF3B\uFF3D.\\[\\]/~\u2053\u223C\uFF5E*]*[0-9\uFF10-\uFF19\u0660-\u0669\u06F0-\u06F9]){3,}[-x\u2010-\u2015\u2212\u30FC\uFF0D-\uFF0F \u00A0\u00AD\u200B\u2060\u3000()\uFF08\uFF09\uFF3B\uFF3D.\\[\\]/~\u2053\u223C\uFF5E*A-Za-z0-9\uFF10-\uFF19\u0660-\u0669\u06F0-\u06F9]*(?:(?:;ext=([0-9０-９٠-٩۰-۹]{1,7})|[  \\t,]*(?:e?xt(?:ensi(?:ó?|ó))?n?|ｅ?ｘｔｎ?|[,xｘX#＃~～;]|int|anexo|ｉｎｔ)[:\\.．]?[  \\t,-]*([0-9０-９٠-٩۰-۹]{1,7})#?|[- ]+([0-9０-９٠-٩۰-۹]{1,5})#)?$)?[,;]*$"

    const val countryCodePattern = "^[a-zA-Z]{2}$"
}
