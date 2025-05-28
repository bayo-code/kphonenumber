# kphonenumber

Phone number parsing library for Kotlin Multiplatform. Based on Google’s Libphonenumber and [PhoneNumberKit](https://github.com/marmelroy/PhoneNumberKit/tree/master).

## Features

* Validate, normalize and extract elements of any phone number string.
* Simple Kotlin syntax and lightweight readable codebase.
* Uses metadata from Google's libPhoneNumber.
* Works with all Kotlin Multiplatform supported targets.
* Convert country codes to country names and vice versa.
* Metadata is embedded, so no need to load anything at runtime.

## Usage

Add the dependency to your `build.gradle.kts`:

```kotlin
implementation("com.bayo-code:kphonenumber:0.11.0")
```

This is available on Maven Central. Then create a `KPhoneNumber` object:

```kotlin
import com.bayocode.kphonenumber.KPhoneNumber

val kPhoneNumber = KPhoneNumber()
``` 

All your interactions with KPhoneNumber will happen through this object.

To parse a string, use this function:

```kotlin
try {
    val phoneNumber = kPhoneNumber.parse("+33 6 89 017383")
} catch (exception: PhoneNumberException) {
    // handle exception
}
```

This returns a `PhoneNumber` object that contains information about the parsed phone number. If the parsing fails, it throws a `PhoneNumberException`, so make sure to catch that.

You can also pass a specific region for the phone number:

```kotlin
try {
    val phoneNumber = kPhoneNumber.parse("+33 6 89 017383", "GB")
} catch (exception: PhoneNumberException) {
    // handle exception
}
```

You can also disable validation, which ensures that the returned object is a valid phone number:

```kotlin
try {
    val phoneNumber = kPhoneNumber.parse("+33 6 89 017383", "GB", true)
} catch (exception: PhoneNumberException) {
    // handle exception
}
```

Formatting the returned phone number is easy:

```kotlin
kPhoneNumber.format(phoneNumber, PhoneNumberFormat.E164) // +61236618300
kPhoneNumber.format(phoneNumber, PhoneNumberFormat.International) // +61 2 3661 8300
kPhoneNumber.format(phoneNumber, PhoneNumberFormat.National) // // (02) 3661 8300
```

### As You Type Formatting

To get an As-you-type formatter to format incomplete phone numbers, you can use the following snippet:

```kotlin
val partialFormatter = kPhoneNumber.partialFormatter()
val formattedString = partialFormatter.formatPartial("+336895555") // +33 6 89 55 55
```

## Updating the Generated Metadata file

* Put the updated `PhoneNumberMetadata.json` file in [`metadata-generator/metadata/`](metadata-generator/metadata) folder.
* Then run the code in the [`metadata-generator`](metadata-generator) module. This will re-generate the metadata and update it in the code.
* Use Intellij or any formatting tool to format the file so it looks pretty
