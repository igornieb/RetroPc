package com.example.retropc

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CpuFlagTest {
    private lateinit var cpu: Cpu6502

    @Before
    fun setup() {
        // Assuming you have a Cpu6502 instance
        cpu = Cpu6502()
    }

    @Test
    fun testSetFlagNegative() {
        testSetFlagTrue(Flags.N)
        testSetFlagFalse(Flags.N)
    }

    @Test
    fun testSetFlagOverflow() {
        testSetFlagTrue(Flags.V)
        testSetFlagFalse(Flags.V)
    }

    @Test
    fun testSetFlagIgnored() {
        testSetFlagTrue(Flags.U)
        testSetFlagFalse(Flags.U)
    }

    @Test
    fun testSetFlagBreak() {
        testSetFlagTrue(Flags.B)
        testSetFlagFalse(Flags.B)
    }

    @Test
    fun testSetFlagDecimal() {
        testSetFlagTrue(Flags.D)
        testSetFlagFalse(Flags.D)
    }

    @Test
    fun testSetFlagInterrupt() {
        testSetFlagTrue(Flags.I)
        testSetFlagFalse(Flags.I)
    }

    @Test
    fun testSetFlagZero() {
        testSetFlagTrue(Flags.Z)
        testSetFlagFalse(Flags.Z)
    }

    @Test
    fun testSetFlagCarry() {
        testSetFlagTrue(Flags.C)
        testSetFlagFalse(Flags.C)
    }

    private fun testSetFlagTrue(flag: Flags) {
        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)

        // Act
        cpu.SetFlag(flag, true)
//        println("---State---")
//        println("flag: " + flag.value.toString(2))
//        println("cpu: " + cpu.Sr().toString(2))
//        println("-----------")

        // Assert
        assertEquals(flag.value.toUByte(), cpu.Sr())

        // Reset for the next test
        cpu.SetFlags(initialSr)
    }

    private fun testSetFlagFalse(flag: Flags) {
        // Arrange
        val initialSr = 0b1111_1111.toUByte()
        cpu.SetFlags(initialSr)

        // Act
        cpu.SetFlag(flag, false)
//        println("---State---")
//        println("flag: " + flag.value.toString(2))
//        println("inverse flag: " + flag.value.inv().toString(2))
//        println("cpu: " + cpu.Sr().toString(2))
//        println("-----------")

        // Assert
        assertEquals(flag.value.inv().toUByte(), cpu.Sr())

        // Reset for the next test
        cpu.SetFlags(initialSr)
    }
}