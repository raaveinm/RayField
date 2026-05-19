package com.raaveinm.rayfield.ui.mock

import com.raaveinm.rayfield.data.xray.XrayConfig
import java.util.Collections.list

//
// Created by Kirill "Raaveinm" on 5/19/26.
//

// region Example
val mockVlessUser = List(7) { index ->
    XrayConfig.VlessUser(index.toString())
}
// endregion Example