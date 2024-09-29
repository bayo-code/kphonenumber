package com.bayocode.kphonenumber

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PartialFormatterTest {

    private lateinit var kPhoneNumber: KPhoneNumber

    @BeforeTest
    fun setup() {
        kPhoneNumber = KPhoneNumber()
    }

    /// Input: +33689555555
    /// Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=%2B33689555555&country=FR
    @Test
    fun testFrenchNumberFromFrenchRegion() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "FR")
        var testNumber = "+"
        assertEquals("+", partialFormatter.formatPartial(testNumber))
        testNumber = "+3"
        assertEquals("+3", partialFormatter.formatPartial(testNumber))
        testNumber = "+33"
        assertEquals("+33", partialFormatter.formatPartial(testNumber))
        testNumber = "+336"
        assertEquals("+33 6", partialFormatter.formatPartial(testNumber))
        testNumber = "+3368"
        assertEquals("+33 68", partialFormatter.formatPartial(testNumber))
        testNumber = "+33689"
        assertEquals("+33 6 89", partialFormatter.formatPartial(testNumber))
        testNumber = "+336895"
        assertEquals("+33 6 89 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+3368955"
        assertEquals("+33 6 89 55", partialFormatter.formatPartial(testNumber))
        testNumber = "+33689555"
        assertEquals("+33 6 89 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+336895555"
        assertEquals("+33 6 89 55 55", partialFormatter.formatPartial(testNumber))
        testNumber = "+3368955555"
        assertEquals("+33 6 89 55 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+33689555555"
        assertEquals("+33 6 89 55 55 55", partialFormatter.formatPartial(testNumber))
    }

    /// Input: 0033689555555
    /// Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=0033689555555&country=FR
    @Test
    fun testFrenchNumberIDDFromFrenchRegion() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "FR")
        var testNumber = "0"
        assertEquals("0", partialFormatter.formatPartial(testNumber))
        testNumber = "00"
        assertEquals("00", partialFormatter.formatPartial(testNumber))
        testNumber = "003"
        assertEquals("00 3", partialFormatter.formatPartial(testNumber))
        testNumber = "0033"
        assertEquals("00 33", partialFormatter.formatPartial(testNumber))
        testNumber = "00336"
        assertEquals("00 33 6", partialFormatter.formatPartial(testNumber))
        testNumber = "003368"
        assertEquals("00 33 68", partialFormatter.formatPartial(testNumber))
        testNumber = "0033689"
        assertEquals("00 33 6 89", partialFormatter.formatPartial(testNumber))
        testNumber = "00336895"
        assertEquals("00 33 6 89 5", partialFormatter.formatPartial(testNumber))
        testNumber = "003368955"
        assertEquals("00 33 6 89 55", partialFormatter.formatPartial(testNumber))
        testNumber = "0033689555"
        assertEquals("00 33 6 89 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "00336895555"
        assertEquals("00 33 6 89 55 55", partialFormatter.formatPartial(testNumber))
        testNumber = "003368955555"
        assertEquals("00 33 6 89 55 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "0033689555555"
        assertEquals("00 33 6 89 55 55 55", partialFormatter.formatPartial(testNumber))
    }

    // 268 464 1234
    // Test for number that is not the country code's main country
    @Test
    fun testAntiguaNumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "AG")
        var number = "2"
        assertEquals("2", partialFormatter.formatPartial(number))
        number = "26"
        assertEquals("26", partialFormatter.formatPartial(number))
        number = "268"
        assertEquals("268", partialFormatter.formatPartial(number))
        number = "2684"
        assertEquals("268-4", partialFormatter.formatPartial(number))
        number = "26846"
        assertEquals("268-46", partialFormatter.formatPartial(number))
        number = "268464"
        assertEquals("268-464", partialFormatter.formatPartial(number))
        number = "2684641"
        assertEquals("268-4641", partialFormatter.formatPartial(number))
        number = "26846412"
        assertEquals("(268) 464-12", partialFormatter.formatPartial(number))
        number = "268464123"
        assertEquals("(268) 464-123", partialFormatter.formatPartial(number))
        number = "2684641234"
        assertEquals("(268) 464-1234", partialFormatter.formatPartial(number))
    }

    // Input: +33689555555
    // Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=%2B33689555555&country=US
    @Test
    fun testFrenchNumberFromAmericanRegion() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        var testNumber = "+"
        assertEquals("+", partialFormatter.formatPartial(testNumber))
        testNumber = "+3"
        assertEquals("+3", partialFormatter.formatPartial(testNumber))
        testNumber = "+33"
        assertEquals("+33", partialFormatter.formatPartial(testNumber))
        testNumber = "+336"
        assertEquals("+33 6", partialFormatter.formatPartial(testNumber))
        testNumber = "+3368"
        assertEquals("+33 68", partialFormatter.formatPartial(testNumber))
        testNumber = "+33689"
        assertEquals("+33 6 89", partialFormatter.formatPartial(testNumber))
        testNumber = "+336895"
        assertEquals("+33 6 89 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+3368955"
        assertEquals("+33 6 89 55", partialFormatter.formatPartial(testNumber))
        testNumber = "+33689555"
        assertEquals("+33 6 89 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+336895555"
        assertEquals("+33 6 89 55 55", partialFormatter.formatPartial(testNumber))
        testNumber = "+3368955555"
        assertEquals("+33 6 89 55 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+33689555555"
        assertEquals("+33 6 89 55 55 55", partialFormatter.formatPartial(testNumber))
    }

    // Input: 01133689555555
    // Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=01133689555555&country=US
    @Test
    fun testFrenchNumberIDDFromAmericanRegion() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        var testNumber = "0"
        assertEquals("0", partialFormatter.formatPartial(testNumber))
        testNumber = "01"
        assertEquals("01", partialFormatter.formatPartial(testNumber))
        testNumber = "011"
        assertEquals("011", partialFormatter.formatPartial(testNumber))
        testNumber = "0113"
        assertEquals("011 3", partialFormatter.formatPartial(testNumber))
        testNumber = "01133"
        assertEquals("011 33", partialFormatter.formatPartial(testNumber))
        testNumber = "011336"
        assertEquals("011 33 6", partialFormatter.formatPartial(testNumber))
        testNumber = "0113368"
        assertEquals("011 33 68", partialFormatter.formatPartial(testNumber))
        testNumber = "01133689"
        assertEquals("011 33 6 89", partialFormatter.formatPartial(testNumber))
        testNumber = "011336895"
        assertEquals("011 33 6 89 5", partialFormatter.formatPartial(testNumber))
        testNumber = "0113368955"
        assertEquals("011 33 6 89 55", partialFormatter.formatPartial(testNumber))
        testNumber = "01133689555"
        assertEquals("011 33 6 89 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "011336895555"
        assertEquals("011 33 6 89 55 55", partialFormatter.formatPartial(testNumber))
        testNumber = "0113368955555"
        assertEquals("011 33 6 89 55 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "01133689555555"
        assertEquals("011 33 6 89 55 55 55", partialFormatter.formatPartial(testNumber))
    }

    @Test
    fun testInvalidNumberNotANumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        val testNumber = "ae4c08c6-be33-40ef-a417-e5166e307b5e"
        assertEquals("ae4c08c6-be33-40ef-a417-e5166e307b5e", partialFormatter.formatPartial(testNumber))
    }

    /// Input: +390549555555
    /// Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=%2B390549555555&country=US
    @Test
    fun testItalianLeadingZeroFromUS() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        var testNumber = "+"
        assertEquals("+", partialFormatter.formatPartial(testNumber))
        testNumber = "+3"
        assertEquals("+3", partialFormatter.formatPartial(testNumber))
        testNumber = "+39"
        assertEquals("+39", partialFormatter.formatPartial(testNumber))
        testNumber = "+390"
        assertEquals("+39 0", partialFormatter.formatPartial(testNumber))
        testNumber = "+3905"
        assertEquals("+39 05", partialFormatter.formatPartial(testNumber))
        testNumber = "+39054"
        assertEquals("+39 054", partialFormatter.formatPartial(testNumber))
        testNumber = "+390549"
        assertEquals("+39 0549", partialFormatter.formatPartial(testNumber))
        testNumber = "+3905495"
        assertEquals("+39 0549 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+39054955"
        assertEquals("+39 0549 55", partialFormatter.formatPartial(testNumber))
        testNumber = "+390549555"
        assertEquals("+39 0549 555", partialFormatter.formatPartial(testNumber))
        testNumber = "+3905495555"
        assertEquals("+39 0549 5555", partialFormatter.formatPartial(testNumber))
        testNumber = "+39054955555"
        assertEquals("+39 0549 55555", partialFormatter.formatPartial(testNumber))
        testNumber = "+390549555555"
        assertEquals("+39 0549 555555", partialFormatter.formatPartial(testNumber))
    }

    /// Input: 0689555555
    /// Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=0689555555&country=FR
    @Test
    fun testFrenchNumberLocal() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "FR")
        var testNumber = "0"
        assertEquals("0", partialFormatter.formatPartial(testNumber))
        testNumber = "06"
        assertEquals("06", partialFormatter.formatPartial(testNumber))
        testNumber = "068"
        assertEquals("068", partialFormatter.formatPartial(testNumber))
        testNumber = "0689"
        assertEquals("06 89", partialFormatter.formatPartial(testNumber))
        testNumber = "06895"
        assertEquals("06 89 5", partialFormatter.formatPartial(testNumber))
        testNumber = "068955"
        assertEquals("06 89 55", partialFormatter.formatPartial(testNumber))
        testNumber = "0689555"
        assertEquals("06 89 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "06895555"
        assertEquals("06 89 55 55", partialFormatter.formatPartial(testNumber))
        testNumber = "068955555"
        assertEquals("06 89 55 55 5", partialFormatter.formatPartial(testNumber))
        testNumber = "0689555555"
        assertEquals("06 89 55 55 55", partialFormatter.formatPartial(testNumber))
    }

    @Test
    fun testUSTollFreeNumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        var testNumber = "8"
        assertEquals("8", partialFormatter.formatPartial(testNumber))
        testNumber = "80"
        assertEquals("80", partialFormatter.formatPartial(testNumber))
        testNumber = "800"
        assertEquals("800", partialFormatter.formatPartial(testNumber))
        testNumber = "8002"
        assertEquals("800-2", partialFormatter.formatPartial(testNumber))
        testNumber = "80025"
        assertEquals("800-25", partialFormatter.formatPartial(testNumber))
        testNumber = "800253"
        assertEquals("800-253", partialFormatter.formatPartial(testNumber))
        testNumber = "8002530"
        assertEquals("800-2530", partialFormatter.formatPartial(testNumber))
        testNumber = "80025300"
        assertEquals("(800) 253-00", partialFormatter.formatPartial(testNumber))
        testNumber = "800253000"
        assertEquals("(800) 253-000", partialFormatter.formatPartial(testNumber))
        testNumber = "8002530000"
        assertEquals("(800) 253-0000", partialFormatter.formatPartial(testNumber))
    }

    // Input: 3148525477
    // Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=3148525477&country=US
    @Test
    fun testUSNumberStartingWithThree() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        var testNumber = "3"
        assertEquals("3", partialFormatter.formatPartial(testNumber))
        testNumber = "31"
        assertEquals("31", partialFormatter.formatPartial(testNumber))
        testNumber = "314"
        assertEquals("314", partialFormatter.formatPartial(testNumber))
        testNumber = "3148"
        assertEquals("314-8", partialFormatter.formatPartial(testNumber))
        testNumber = "31485"
        assertEquals("314-85", partialFormatter.formatPartial(testNumber))
        testNumber = "314852"
        assertEquals("314-852", partialFormatter.formatPartial(testNumber))
        testNumber = "3148525"
        assertEquals("314-8525", partialFormatter.formatPartial(testNumber))
        testNumber = "31485254"
        assertEquals("(314) 852-54", partialFormatter.formatPartial(testNumber))
        testNumber = "314852547"
        assertEquals("(314) 852-547", partialFormatter.formatPartial(testNumber))
        testNumber = "3148525477"
        assertEquals("(314) 852-5477", partialFormatter.formatPartial(testNumber))
    }

    // Input: 4372234563
    // Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=4372234563&country=CA
    @Test
    fun testCANumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "CA")
        var testNumber = "4"
        assertEquals("4", partialFormatter.formatPartial(testNumber))
        testNumber = "43"
        assertEquals("43", partialFormatter.formatPartial(testNumber))
        testNumber = "437"
        assertEquals("437", partialFormatter.formatPartial(testNumber))
        testNumber = "4372"
        assertEquals("437-2", partialFormatter.formatPartial(testNumber))
        testNumber = "43722"
        assertEquals("437-22", partialFormatter.formatPartial(testNumber))
        testNumber = "437223"
        assertEquals("437-223", partialFormatter.formatPartial(testNumber))
        testNumber = "4372234"
        assertEquals("437-2234", partialFormatter.formatPartial(testNumber))
        testNumber = "43722345"
        assertEquals("(437) 223-45", partialFormatter.formatPartial(testNumber))
        testNumber = "437223456"
        assertEquals("(437) 223-456", partialFormatter.formatPartial(testNumber))
        testNumber = "4372234563"
        assertEquals("(437) 223-4563", partialFormatter.formatPartial(testNumber))
    }

    // 07739555555
    @Test
    fun testUKMobileNumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "GB")
        var testNumber = "0"
        assertEquals("0", partialFormatter.formatPartial(testNumber))
        testNumber = "07"
        assertEquals("07", partialFormatter.formatPartial(testNumber))
        testNumber = "077"
        assertEquals("077", partialFormatter.formatPartial(testNumber))
        testNumber = "0773"
        assertEquals("0773", partialFormatter.formatPartial(testNumber))
        testNumber = "07739"
        assertEquals("07739", partialFormatter.formatPartial(testNumber))
        testNumber = "077395"
        assertEquals("07739 5", partialFormatter.formatPartial(testNumber))
        testNumber = "0773955"
        assertEquals("07739 55", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555"
        assertEquals("07739 555", partialFormatter.formatPartial(testNumber))
        testNumber = "077395555"
        assertEquals("07739 5555", partialFormatter.formatPartial(testNumber))
        testNumber = "0773955555"
        assertEquals("07739 55555", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555"
        assertEquals("07739 555555", partialFormatter.formatPartial(testNumber))
    }

    // 07739555555,9
    @Test
    fun testUKMobileNumberWithDigitsPausesAndWaits() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "GB")
        var testNumber = "0"
        assertEquals("0", partialFormatter.formatPartial(testNumber))
        testNumber = "07"
        assertEquals("07", partialFormatter.formatPartial(testNumber))
        testNumber = "077"
        assertEquals("077", partialFormatter.formatPartial(testNumber))
        testNumber = "0773"
        assertEquals("0773", partialFormatter.formatPartial(testNumber))
        testNumber = "07739"
        assertEquals("07739", partialFormatter.formatPartial(testNumber))
        testNumber = "077395"
        assertEquals("07739 5", partialFormatter.formatPartial(testNumber))
        testNumber = "0773955"
        assertEquals("07739 55", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555"
        assertEquals("07739 555", partialFormatter.formatPartial(testNumber))
        testNumber = "077395555"
        assertEquals("07739 5555", partialFormatter.formatPartial(testNumber))
        testNumber = "0773955555"
        assertEquals("07739 55555", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555"
        assertEquals("07739 555555", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555,"
        assertEquals("07739 555555,", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555,9"
        assertEquals("07739 555555,9", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555,9,"
        assertEquals("07739555555,9,", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555,9,1"
        assertEquals("07739 555555,9,1", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555,9,1;"
        assertEquals("07739555555,9,1;", partialFormatter.formatPartial(testNumber)) // not quite the expected, should keep formatting and just add pauses and
        // waits during typing.
        testNumber = "07739555555,9,1;2"
        assertEquals("07739 555555,9,1;2", partialFormatter.formatPartial(testNumber))
        testNumber = "07739555555,9,1;2;"
        assertEquals("07739555555,9,1;2;", partialFormatter.formatPartial(testNumber)) // not quite the expected, should keep formatting and just add pauses and
        // waits during typing.
        testNumber = "07739555555,9,1;2;5"
        assertEquals("07739 555555,9,1;2;5", partialFormatter.formatPartial(testNumber))
    }

    /// Input: +٩٧١٥٠٠٥٠٠٥٥٠ (+971500500550)
    /// Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=%2B971500500550&country=AE
    @Test
    fun testAENumberWithHinduArabicNumerals() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "AE")
        var testNumber = "+"
        assertEquals("+", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩"
        assertEquals("+9", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧"
        assertEquals("+97", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١"
        assertEquals("+971", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5"
        assertEquals("+971 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠"
        assertEquals("+971 50", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠٠"
        assertEquals("+971 50 0", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠٠5"
        assertEquals("+971 50 05", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠٠5٠"
        assertEquals("+971 50 050", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠٠5٠٠"
        assertEquals("+971 50 050 0", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠٠5٠٠5"
        assertEquals("+971 50 050 05", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠٠5٠٠55"
        assertEquals("+971 50 050 055", partialFormatter.formatPartial(testNumber))
        testNumber = "+٩٧١5٠٠5٠٠55٠"
        assertEquals("+971 50 050 0550", partialFormatter.formatPartial(testNumber))
    }

    /// Input: +۹۷۱۵۰۰۵۰۰۵۵۰ (+971500500550)
    /// Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=%2B971500500550&country=AE
    @Test
    fun testAENumberWithEasternArabicNumerals() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "AE")
        var testNumber = "+"
        assertEquals("+", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹"
        assertEquals("+9", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷"
        assertEquals("+97", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷١"
        assertEquals("+971", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵"
        assertEquals("+971 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰"
        assertEquals("+971 50", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰۰"
        assertEquals("+971 50 0", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰۰۵"
        assertEquals("+971 50 05", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰۰۵۰"
        assertEquals("+971 50 050", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰۰۵۰۰"
        assertEquals("+971 50 050 0", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰۰۵۰۰۵"
        assertEquals("+971 50 050 05", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰۰۵۰۰۵۵"
        assertEquals("+971 50 050 055", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱۵۰۰۵۰۰۵۵۰"
        assertEquals("+971 50 050 0550", partialFormatter.formatPartial(testNumber))
    }

    /// Input: +۹۷۱5۰۰5۰۰55۰ (+971500500550)
    /// Expected result: https://libphonenumber.appspot.com/phonenumberparser?number=%2B971500500550&country=AE
    @Test
    fun testAENumberWithMixedEasternArabicNumerals() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "AE")
        var testNumber = "+"
        assertEquals("+", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹"
        assertEquals("+9", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷"
        assertEquals("+97", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷١"
        assertEquals("+971", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5"
        assertEquals("+971 5", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰"
        assertEquals("+971 50", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰۰"
        assertEquals("+971 50 0", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰۰5"
        assertEquals("+971 50 05", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰۰5۰"
        assertEquals("+971 50 050", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰۰5۰۰"
        assertEquals("+971 50 050 0", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰۰5۰۰5"
        assertEquals("+971 50 050 05", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰۰5۰۰55"
        assertEquals("+971 50 050 055", partialFormatter.formatPartial(testNumber))
        testNumber = "+۹۷۱5۰۰5۰۰55۰"
        assertEquals("+971 50 050 0550", partialFormatter.formatPartial(testNumber))
    }

    @Test
    fun testWithPrefixDisabled() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "CZ", withPrefix = false)
        val formatted = partialFormatter.formatPartial("+420777123456")
        assertEquals("777 123 456", formatted)
    }

    @Test
    fun testMinimalFrenchNumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        partialFormatter.formatPartial("+33")
        assertEquals("FR", partialFormatter.currentRegion)
    }

    @Test
    fun testMinimalUSNumberFromFrance() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "FR")
        partialFormatter.formatPartial("+1")
        assertEquals("US", partialFormatter.currentRegion)
    }

    @Test
    fun testRegionResetsWithEachCallToFormatPartial() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "DE")
        partialFormatter.formatPartial("+1 212 555 1212")
        assertEquals("US", partialFormatter.currentRegion)
        partialFormatter.formatPartial("invalid raw number")
        assertEquals("DE", partialFormatter.currentRegion)
    }

    @Test
    fun testMaxDigits() {
        fun test(maxDigits: Int?, formatted: String) {
            val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US", maxDigits = maxDigits)
            assertEquals(formatted, partialFormatter.formatPartial("555 555 5555"))
        }

        test(null, "(555) 555-5555")
        test(0, "")
        test(1, "5")
        test(2, "55")
        test(3, "555")
        test(4, "555-5")
        test(5, "555-55")
        test(6, "555-555")
        test(7, "555-5555")
        test(8, "(555) 555-55")
        test(9, "(555) 555-555")
        test(10, "(555) 555-5555")
        test(11, "(555) 555-5555")
    }

    @Test
    fun testConvenienceInitializerAllowsFormatting() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        val testNumber = "8675309"
        assertEquals("867-5309", partialFormatter.formatPartial(testNumber))
    }

    // *144
    @Test
    fun testBrazilianOperatorService() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "BR")
        var testNumber = "*"
        assertEquals("*", partialFormatter.formatPartial(testNumber))
        testNumber = "*1"
        assertEquals("*1", partialFormatter.formatPartial(testNumber))
        testNumber = "*14"
        assertEquals("*14", partialFormatter.formatPartial(testNumber))
        testNumber = "*144"
        assertEquals("*144", partialFormatter.formatPartial(testNumber))
    }

    // *#06#
    @Test
    fun testImeiCodeRetrieval() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "BR")
        var testNumber = "*"
        assertEquals("*", partialFormatter.formatPartial(testNumber))
        testNumber = "*#"
        assertEquals("*#", partialFormatter.formatPartial(testNumber))
        testNumber = "*#0"
        assertEquals("*#0", partialFormatter.formatPartial(testNumber))
        testNumber = "*#06"
        assertEquals("*#06", partialFormatter.formatPartial(testNumber))
        testNumber = "*#06#"
        assertEquals("*#06#", partialFormatter.formatPartial(testNumber))
    }

    // *#*6#
    @Test
    fun testAsteriskShouldNotBeRejectedInTheMiddle() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "BR")
        var testNumber = "*"
        assertEquals("*", partialFormatter.formatPartial(testNumber))
        testNumber = "*#"
        assertEquals("*#", partialFormatter.formatPartial(testNumber))
        testNumber = "*#*"
        assertEquals("*#*", partialFormatter.formatPartial(testNumber))
        testNumber = "*#*6"
        assertEquals("*#*6", partialFormatter.formatPartial(testNumber))
        testNumber = "*#*6#"
        assertEquals("*#*6#", partialFormatter.formatPartial(testNumber))
    }

    // *#*6#
    @Test
    fun testPoundShouldNotBeRejectedInTheMiddle() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "BR")
        var testNumber = "*"
        assertEquals("*", partialFormatter.formatPartial(testNumber))
        testNumber = "*#"
        assertEquals("*#", partialFormatter.formatPartial(testNumber))
        testNumber = "*#*"
        assertEquals("*#*", partialFormatter.formatPartial(testNumber))
        testNumber = "*#*6"
        assertEquals("*#*6", partialFormatter.formatPartial(testNumber))
        testNumber = "*#*6#"
        assertEquals("*#*6#", partialFormatter.formatPartial(testNumber))
    }

    // Pauses and waits
    // (http://allgaierconsulting.com/techtalk/2014/8/1/why-and-how-to-insert-a-pause-or-wait-key-on-your-iphone)
    // 650,9,2
    @Test
    fun testPausedPhoneNumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        var testNumber = "6"
        assertEquals("6", partialFormatter.formatPartial(testNumber))
        testNumber = "65"
        assertEquals("65", partialFormatter.formatPartial(testNumber))
        testNumber = "650"
        assertEquals("650", partialFormatter.formatPartial(testNumber))
        testNumber = "650,"
        assertEquals("650,", partialFormatter.formatPartial(testNumber))
        testNumber = "650,9"
        assertEquals("650,9", partialFormatter.formatPartial(testNumber))
        testNumber = "650,9,"
        assertEquals("650,9,", partialFormatter.formatPartial(testNumber))
        testNumber = "650,9,2"
        assertEquals("650,9,2", partialFormatter.formatPartial(testNumber))
    }

    // 121;4
    @Test
    fun testWaitPhoneNumber() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        var testNumber = "1"
        assertEquals("1", partialFormatter.formatPartial(testNumber))
        testNumber = "12"
        assertEquals("12", partialFormatter.formatPartial(testNumber))
        testNumber = "121"
        assertEquals("121", partialFormatter.formatPartial(testNumber))
        testNumber = "121;"
        assertEquals("121;", partialFormatter.formatPartial(testNumber))
        testNumber = "121;4"
        assertEquals("121;4", partialFormatter.formatPartial(testNumber))
    }

    @Test
    fun testMinimalRUNumberFromESRegion() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "ES")
        partialFormatter.formatPartial("+7")
        assertEquals("RU", partialFormatter.currentRegion)
    }

    @Test
    fun testMinimalRUNumberFromUSRegion() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "US")
        partialFormatter.formatPartial("+7")
        assertEquals("RU", partialFormatter.currentRegion)
    }

    @Test
    fun testJerseyPhoneNumberWithoutPrefix() {
        val partialFormatter = PartialFormatter(kPhoneNumber, defaultRegion = "JE", withPrefix = false)
        val testNumber = "078297"
        assertEquals("7829 7", partialFormatter.formatPartial(testNumber))
    }
}
