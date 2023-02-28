package com.github.sdp_begreen.begreen

import com.github.sdp_begreen.begreen.coroutines.coroutinesPlayground
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoroutinesTests {
    private val outContent: ByteArrayOutputStream = ByteArrayOutputStream()
    private val errContent: ByteArrayOutputStream = ByteArrayOutputStream()
    private val originalOut: PrintStream = System.out
    private val originalErr: PrintStream = System.err

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
        System.setErr(PrintStream(errContent))
    }

    @After
    fun restoreStreams() {
        System.setOut(originalOut)
        System.setErr(originalErr)
    }

    @Test
    fun testLaunch() {
        coroutinesPlayground.tryLaunch()
        assertEquals(outContent.toString(), "Starting main function..\n" +
                "Did something that was 3 seconds long\n" +
                "4 sec passed on the main thread\n")
    }

    @Test
    fun testAsync() {
        coroutinesPlayground.tryAsync()
        assertEquals(outContent.toString(), "Gone to calculate sum of a & b\n" +
                "Carry on with some other task while the coroutine is waiting for a result...\n" +
                "Sum of a & b is: 3")
    }

    @Test
    fun testDeferred(){
        coroutinesPlayground.tryDeferred()
        assertEquals(outContent.toString(), "One of the criteria unmatched, sorry!")
    }
}
