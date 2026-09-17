package com.aryntra.darpan.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DeviceInfoProviderTest {

    @Test
    fun getDeviceInfo_returnsNonNullState() {
        val provider = DeviceInfoProvider()
        val state = provider.getDeviceInfo()

        assertNotNull(state)
        assertNotNull(state.deviceName)
        assertNotNull(state.manufacturer)
        assertNotNull(state.androidVersion)
    }
}
