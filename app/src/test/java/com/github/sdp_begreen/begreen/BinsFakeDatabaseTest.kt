package com.github.sdp_begreen.begreen

import com.github.sdp_begreen.begreen.models.Bin
import com.github.sdp_begreen.begreen.models.TrashCategory
import org.junit.Test
import org.junit.Assert.*

class BinsFakeDatabaseTest {

    @Test
    fun testAddBin() {
        val bin = Bin(5, TrashCategory.PAPER, 51.5074, -0.1278)
        BinsFakeDatabase.addBin(bin)
        assertTrue(BinsFakeDatabase.fakeBins.contains(bin))
    }

    @Test
    fun testRemoveBin() {
        val bin = Bin(2, TrashCategory.ELECTRONIC, 40.7128, -74.0060)
        BinsFakeDatabase.removeBin(bin.id)
        assertFalse(BinsFakeDatabase.fakeBins.contains(bin))
    }
}
