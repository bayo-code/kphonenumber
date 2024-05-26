package com.bayocode.kphonenumber

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class RegexManager {
    private val regularExpressionPool = mutableMapOf<String, Regex>()
    private val regularExpressionPoolMutex = Mutex()

    private val spaceCharacterSet = setOf(
        ' ',
        '\n',
        '\t',
        PhoneNumberConstants.nonBreakingSpace
    )

    suspend fun regexWithPattern(pattern: String): Regex {
        return regularExpressionPoolMutex.withLock {
            try {
                regularExpressionPool[pattern] ?: run {
                    val regex = Regex(pattern, RegexOption.IGNORE_CASE)
                    regularExpressionPool[pattern] = regex
                    regex
                }
            } catch (e: Exception) {
                throw GeneralPhoneNumberException()
            }
        }
    }

    suspend fun regexMatches(pattern: String, string: String): List<MatchResult> {
        val regex = regexWithPattern(pattern)
        return regex.findAll(string).toList()
    }

    suspend fun phoneDataDetectorMatch(string: String): MatchResult {
        val fallBackMatches = regexMatches(PhoneNumberPatterns.validPhoneNumberPattern, string)
        return fallBackMatches.firstOrNull() ?: throw InvalidNumberException(string)
    }

    fun matchesAtStart(pattern: String, string: String): Boolean {
        return runBlocking { regexMatches(pattern, string).firstOrNull()?.range?.start == 0 }
    }

    fun stringPositionByRegex(pattern: String, string: String): Int {
        val matches = runBlocking { regexMatches(pattern, string) }
        return matches.firstOrNull()?.range?.start ?: -1
    }

    suspend fun matchesExist(pattern: String?, string: String): Boolean {
        return pattern?.let {
            regexMatches(it, string).isNotEmpty()
        } ?: false
    }

    fun matchesEntirely(pattern: String?, string: String): Boolean {
        pattern?.let {
            val fullPattern = "^($it)$"
            return runBlocking { matchesExist(fullPattern, string) }
        }
        return false
    }

    suspend fun matchedStringByRegex(pattern: String, string: String): List<String> {
        return regexMatches(pattern, string).map { string.substring(it.range) }
    }

    fun replaceStringByRegex(pattern: String, string: String, template: String = ""): String {
        val regex = runBlocking { regexWithPattern(pattern) }
        return regex.replace(string, template)
    }

    fun replaceFirstStringByRegex(pattern: String, string: String, templateString: String): String {
        val regex = runBlocking { regexWithPattern(pattern) }
        return regex.replaceFirst(string, templateString)
    }

    fun stringByReplacingOccurrences(string: String, map: Map<String, String>, keepUnmapped: Boolean = false): String {
        return string.map { char ->
            val keyString = char.uppercaseChar().toString()
            map[keyString] ?: if (keepUnmapped) keyString else ""
        }.joinToString("")
    }

    fun hasValue(value: String?): Boolean {
        return value?.trim { it in spaceCharacterSet }?.isNotEmpty() == true
    }

    suspend fun testStringLengthAgainstPattern(pattern: String, string: String): Boolean {
        return matchesEntirely(pattern, string)
    }
}

