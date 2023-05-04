package com.github.sdp_begreen.begreen.map

import org.junit.Test
import kotlin.test.assertEquals

class BinTypeTest {

    @Test
    fun toStringReturnsExpectedString() {
        val binType = BinType.ORGANIC
        assertEquals(binType.toString(), "Organic")
    }

}
