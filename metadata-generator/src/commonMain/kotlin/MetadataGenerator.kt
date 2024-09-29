import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.io.File

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
        get() = availableFormats?.numberFormat?.withDefaultNationalPrefixFormattingRule(nationalPrefixFormattingRule)
            ?: emptyList()

    fun generate(): String {
        return """
        |    MetadataTerritory(
        |        codeID = ${codeID.generate()},
        |        countryCode = ${countryCode.generate()},
        |        internationalPrefix = ${internationalPrefix.generate()},
        |        mainCountryForCode = ${mainCountryForCode.generate()},
        |        nationalPrefix = ${nationalPrefix.generate()},
        |        nationalPrefixFormattingRule = ${nationalPrefixFormattingRule.generate()},
        |        nationalPrefixForParsing = ${nationalPrefixForParsing?.generate() ?: nationalPrefix.generate()},
        |        nationalPrefixTransformRule = ${nationalPrefixTransformRule.generate()},
        |        preferredExtnPrefix = ${preferredExtnPrefix.generate()},
        |        emergency = ${emergency?.generate()},
        |        fixedLine = ${fixedLine?.generate()},
        |        generalDesc = ${generalDesc?.generate()},
        |        mobile = ${mobile?.generate()},
        |        pager = ${pager?.generate()},
        |        personalNumber = ${personalNumber?.generate()},
        |        premiumRate = ${premiumRate?.generate()},
        |        sharedCost = ${sharedCost?.generate()},
        |        tollFree = ${tollFree?.generate()},
        |        voicemail = ${voicemail?.generate()},
        |        voip = ${voip?.generate()},
        |        uan = ${uan?.generate()},
        |        availableFormats = ${availableFormats?.generate()},
        |        leadingDigits = ${leadingDigits.generate()}
        |    )
        """.trimMargin()
    }
}

@Serializable
data class MetadataPhoneNumberDesc(
    val exampleNumber: String? = null,
    val nationalNumberPattern: String? = null,
    val possibleNumberPattern: String? = null,
    val possibleLengths: MetadataPossibleLengths? = null,
) {
    fun generate(): String {
        return """
        |MetadataPhoneNumberDesc(
        |    exampleNumber = ${exampleNumber.generate()},
        |    nationalNumberPattern = ${nationalNumberPattern.generate()},
        |    possibleNumberPattern = ${possibleNumberPattern.generate()},
        |    possibleLengths = ${possibleLengths?.generate()},
        |)
        """.trimMargin()
    }
}

@Serializable
data class MetadataPossibleLengths(
    val national: String? = null,
    val localOnly: String? = null
) {
    fun generate(): String {
        return """
        |MetadataPossibleLengths(
        |    national = ${national.generate()},
        |    localOnly = ${localOnly.generate()}
        |)
        """.trimMargin()
    }
}

fun String?.generate(): String? {
    return this?.let { json.encodeToString(this) }
        ?.replace("$", "\\$")
}

fun Int?.generate(): String? {
    return this?.let { json.encodeToString(it) }
}

fun Boolean?.generate(): String? {
    return this?.let { json.encodeToString(it) }
}

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

    fun generate(): String {
        return """
        |MetadataPhoneNumberFormat(
        |    pattern = ${pattern.generate()},
        |    format = ${format.generate()},
        |    intlFormat = ${intlFormat.generate()},
        |    leadingDigitsPatterns = ${if (leadingDigitsPatterns != null) "listOf(${leadingDigitsPatterns?.joinToString { it.generate()!! }})" else "null"},
        |    nationalPrefixFormattingRule = ${nationalPrefixFormattingRule.generate()},
        |    nationalPrefixOptionalWhenFormatting = ${nationalPrefixOptionalWhenFormatting.generate()},
        |    domesticCarrierCodeFormattingRule = ${domesticCarrierCodeFormattingRule.generate()},
        |)
        """.trimMargin()
    }
}

@Serializable
data class PhoneNumberMetadataWrapper(
    val phoneNumberMetadata: PhoneNumberMetadata
) {
    fun generate(): String {
        return """
        |PhoneNumberMetadataWrapper(
        |    phoneNumberMetadata = ${phoneNumberMetadata.generate()}
        |)
        """.trimMargin()
    }
}

@Serializable
data class PhoneNumberMetadata(
    val territories: PhoneNumberMetadataTerritories
) {
    fun generate(): String {
        return """
        |PhoneNumberMetadata(
        |    territories = ${territories.generate()}
        |)""".trimMargin()
    }
}

@Serializable
data class PhoneNumberMetadataTerritories(
    val territory: List<MetadataTerritory>
) {
    fun generate(): String {
        return """
        |PhoneNumberMetadataTerritories(
        |   territory = listOf(${territory.map { it.generate() }.joinToString()})
        |)
        """.trimMargin()
    }
}

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

    fun generate(): String {
        return """
        |MetadataAvailableFormat(
        |    numberFormat = listOf(
        |        ${numberFormat.joinToString { it.generate() }}
        |    )
        |)
        """.trimMargin()
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

val json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
}

fun main() {
    val metadataFile = File("metadata/PhoneNumberMetadata.json")
    val generatedFolder = File("../kphonenumber/src/commonMain/kotlin/com/bayocode/kphonenumber/generated")
    if (!generatedFolder.exists()) {
        if (!generatedFolder.mkdirs()) {
            error("Couldn't create generated directory: ${generatedFolder.absolutePath}")
        }
    }

    val data = json.decodeFromString<PhoneNumberMetadataWrapper>(metadataFile.readText())
    val generatedData = """
    |package com.bayocode.kphonenumber.generated
    |
    |import com.bayocode.kphonenumber.*
    |
    |internal val generatedMetadata by lazy {
    |    ${data.generate()}
    |}
    """.trimMargin()
    val generatedFile = File(generatedFolder, "GeneratedMetadata.kt")
    generatedFile.writeText(generatedData)
    println("Generated file written to: ${generatedFile.absolutePath}")
}
