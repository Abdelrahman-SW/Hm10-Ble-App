package com.beapps.starmaxtestproject.domain

import com.clj.fastble.data.BleDevice
import kotlinx.coroutines.flow.MutableStateFlow

object BleInfo {
    val scannedDevices = MutableStateFlow<MutableList<BleDevice>>(mutableListOf())
    val selectedDevice = MutableStateFlow<BleDevice?>(null)
    val bleState = MutableStateFlow(BleState.DISCONNECTED)

}

