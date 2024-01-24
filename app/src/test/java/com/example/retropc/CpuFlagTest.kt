package com.example.retropc

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before

class CpuFlagTest {
    private lateinit var cpu: Cpu6502

    @Before
    fun setup() {
        cpu = Cpu6502()
        var bus = Bus()
        cpu.ConnectBus(bus)
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
    }
    @Test
    fun testSEC() {
        //Assembly
        /* SEC  */

        // Instructions
        /* Set Carry Flag */

        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
        val hex = "38"
        val instruction_length = 1


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(1, cpu.GetFlag(Flags.C))
    }

    @Test
    fun testCLC() {
        //Assembly
        /* SEC
        CLC  */

        // Instructions
        /* Set Carry Flag
        * Clear Carry Flag */

        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
        val hex = "38 18"
        val instruction_length = 2


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(0, cpu.GetFlag(Flags.C))
    }

    @Test
    fun testSED() {
        //Assembly
        /* SED  */

        // Instructions
        /* Set Decimal Flag */

        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
        val hex = "f8"
        val instruction_length = 1


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(1, cpu.GetFlag(Flags.D))
    }

    @Test
    fun testCLD() {
        //Assembly
        /* SED
        CLD  */

        // Instructions
        /* Set Decimal Flag
        * Clear Decimal Flag */

        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
        val hex = "f8 d8"
        val instruction_length = 2


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(0, cpu.GetFlag(Flags.D))
    }

    @Test
    fun testSEI() {
        //Assembly
        /* SEI  */

        // Instructions
        /* Set interrupt Flag */

        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
        val hex = "78"
        val instruction_length = 1


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(1, cpu.GetFlag(Flags.I))
    }

    @Test
    fun testCLI() {
        //Assembly
        /* SEI
        CLI  */

        // Instructions
        /* Set interrupt Flag
        * Clear interrupt Flag */

        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
        val hex = "78 58"
        val instruction_length = 2


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(0, cpu.GetFlag(Flags.I))
    }

    @Test
    fun testCLV() {
        //Assembly
        /* LDA #$7F
        ADC #$01
        CLV  */

        // Instructions
        /* Load 0x7f to accumulator
        * Add 1 to accumulator to set overflow flag
        * Clear overflow Flag */

        // Arrange
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
        val hex = "a9 7f 69 01 b8"
        val instruction_length = 3


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(0, cpu.GetFlag(Flags.V))
    }

}