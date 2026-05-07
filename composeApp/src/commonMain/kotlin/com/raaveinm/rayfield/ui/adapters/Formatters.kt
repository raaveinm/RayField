package com.raaveinm.rayfield.ui.adapters

import androidx.compose.foundation.text.input.InputTransformation

//
// Created by Kirill "Raaveinm" on 5/6/26.
//

val IpAutoFormatTransformation = InputTransformation {
    val originalText = asCharSequence().toString()
    var result = ""
    var currentOctet = ""
    var octetCount = 0

    for (char in originalText) {
        if (char == '.') {
            if (currentOctet.isNotEmpty()) {
                result += "$currentOctet."
                currentOctet = ""
                octetCount++
            } else if (result.isNotEmpty() && !result.endsWith(".")) {
                result += "."
                octetCount++
            }
        } else if (char.isDigit()) {
            val potentialOctet = currentOctet + char
            if (potentialOctet.toIntOrNull() != null && potentialOctet.toInt() > 255) {
                if (octetCount < 3) {
                    result += "$currentOctet."
                    currentOctet = char.toString()
                    octetCount++
                }
            } else {
                currentOctet += char
            }
        }
    }

    if (currentOctet.isNotEmpty() && octetCount < 4)
        result += currentOctet

    if (result != originalText)
        replace(0, length, result)
}
