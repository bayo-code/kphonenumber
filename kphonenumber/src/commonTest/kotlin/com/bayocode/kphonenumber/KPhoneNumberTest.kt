package com.bayocode.kphonenumber

import kotlin.test.*

class KPhoneNumberTest {
    private val kPhoneNumber = KPhoneNumber()
    
    @BeforeTest
    fun setup() {
        println("Test Setup")
    }
    
    @AfterTest
    fun tearDown() {
        println("Test Teardown")
    }
    
    @Test
    fun testMetadataMainCountryFetch() {
        val countryMetadata = kPhoneNumber.metadataManager.mainTerritory(1)
        assertEquals(countryMetadata?.codeID, "US")
    }
    
    @Test
    fun testMetadataMainCountryFunction() {
        val countryName = kPhoneNumber.mainCountry(1)
        assertEquals(countryName, "US")
        val invalidCountry = kPhoneNumber.mainCountry(992322)
        assertEquals(invalidCountry, null)
    }
    
    @Test
    fun testInvalidNumberE() {
        val throwable = assertFails {
            val phoneNumber = kPhoneNumber.parse("202 00e 0000", "US")
            println(kPhoneNumber.format(phoneNumber, PhoneNumberFormat.E164))
        }
        println(throwable)
    }
    
    @Test
    fun testValidNumber6() {
        val phoneNumber = kPhoneNumber.parse("6297062979", "IN")
        println(kPhoneNumber.format(phoneNumber, PhoneNumberFormat.E164))
    }
    
    @Test
    fun testValidNumberBool() {
        assertTrue(kPhoneNumber.isValidPhoneNumber("6297062979", "IN"))
        assertFalse(kPhoneNumber.isValidPhoneNumber("202 00e 0000", "US"))
    }
    
    @Test
    fun testAmbiguousFixedOrMobileNumber() {
        val phoneNumber = kPhoneNumber.parse("+16307792428", "US")
        println(kPhoneNumber.format(phoneNumber, PhoneNumberFormat.E164))
        val type = phoneNumber.type
        assertEquals(type, PhoneNumberType.FixedOrMobile)
    }
    
    @Test
    fun testInvalidGBNumbers() {
        assertFails {
            val phoneNumber = kPhoneNumber.parse("+44629996885", "GB")
            println(kPhoneNumber.format(phoneNumber, PhoneNumberFormat.E164))
        }
    }
    
    @Test
    fun testInvalidBENumbers() {
        assertFails {
            val phoneNumber = kPhoneNumber.parse("+32910853865", "BE")
            println(kPhoneNumber.format(phoneNumber, PhoneNumberFormat.E164))
        }
    }
    
    @Test
    fun testInvalidDZNumbers() {
        assertFails {
            val phoneNumber = kPhoneNumber.parse("+21373344376", "DZ")
            println(kPhoneNumber.format(phoneNumber, PhoneNumberFormat.E164))
        }
    }

    @Test
    fun testValidNigerianNumber() {
        val phoneNumber = kPhoneNumber.parse("8100493234", "NG")
        println(kPhoneNumber.format(phoneNumber, PhoneNumberFormat.International))
        assertTrue(kPhoneNumber.isValidPhoneNumber("2348100493234", "NG"))
    }
}