package com.bayocode.kphonenumber

import java.io.File

actual fun loadMetadata(): ByteArray {
    return File("/Users/mac/code/BrainCrunchLabs/kphonenumber/metadata/PhoneNumberMetadata.json").readBytes()
}