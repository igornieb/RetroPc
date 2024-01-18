package com.example.retropc

import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.jupiter.api.BeforeEach

/**
 * tool : https://skilldrick.github.io/easy6502/
 */
class CpuInstructionTest {
    private lateinit var cpu: Cpu6502

    @Before
    fun setup() {
        // Assuming you have a Cpu6502 instance
        cpu = Cpu6502()
        var bus = Bus()
        cpu.ConnectBus(bus)

    }

    @BeforeEach
    fun breforeEach() {
        val initialSr = 0b0000_0000.toUByte()
        cpu.SetFlags(initialSr)
    }

    @Test
    fun testADC() {
        //Assembly
        /* LDA #$15
        *  ADC #$05  */

        // Instructions
        /* Load 0x15 to accumulator
        * Add 0x05 to accumulator */

        // Arrange
        val hex = "a9 15 69 05"
        val instruction_length = 2
        val a = 0x15
        val b = 0x05


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals((a+ b).toUByte(), cpu.A())
        assertEquals(0, cpu.GetFlag(Flags.C))
        assertEquals(0, cpu.GetFlag(Flags.Z))
        assertEquals(0, cpu.GetFlag(Flags.V))
        assertEquals(0, cpu.GetFlag(Flags.N))
    }

    @Test
    fun testADCCarry() {
        //Assembly
        /* LDA #$FF
        *  ADC #$02 */

        // Instructions
        /* Load 0xFF to accumulator
        * Add 0x02 to accumulator */

        // Arrange
        var hex = "a9 ff 69 02"
        var instruction_length = 2
        val flag = Flags.C.value


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(flag, cpu.GetFlag(Flags.C))
        assertEquals(0, cpu.GetFlag(Flags.Z))
        assertEquals(0, cpu.GetFlag(Flags.V))
        assertEquals(0, cpu.GetFlag(Flags.N))
    }

    @Test
    fun testADCWithCarry() {
        //Assembly
        /* LDA #$FF
        *  ADC #$02
        *  LDA #$FF
        *  ADC #$02 */

        // Instructions
        /* Load 0xFF to accumulator
        * Add 0x02 to accumulator  (to set carry flag)
        * Load 0xFF to accumulator
        * Add 0x02 to accumulator */

        // Arrange
        var hex = "a9 ff 69 02 a9 ff 69 02"
        var instruction_length = 4
        val expected = (0xFF+ 0x02 + 0x1).toUByte()


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(expected, cpu.A())
        assertEquals(0, cpu.GetFlag(Flags.Z))
        assertEquals(0, cpu.GetFlag(Flags.V))
        assertEquals(0, cpu.GetFlag(Flags.N))
    }

    @Test
    fun testADCOverflow() {
        //Assembly
        /* LDA #$78
        *  ADC #$50 */

        // Instructions
        /* Load 0x78 to accumulator
        * Add 0x50 to accumulator */

        // Arrange
        var hex = "a9 78 69 50"
        var instruction_length = 2


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(0, cpu.GetFlag(Flags.C))
        assertEquals(0, cpu.GetFlag(Flags.Z))
        assertEquals(1, cpu.GetFlag(Flags.V))
        assertEquals(1, cpu.GetFlag(Flags.N))
    }

    @Test
    fun testADCZero() {
        //Assembly
        /* LDA #$FF
        *  ADC #$01 */

        // Instructions
        /* Load 0xFF to accumulator
        * Add 0x01 to accumulator */

        // Arrange
        var hex = "a9 ff 69 01"
        var instruction_length = 2


        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(1, cpu.GetFlag(Flags.C))
        assertEquals(1, cpu.GetFlag(Flags.Z))
        assertEquals(0, cpu.GetFlag(Flags.V))
        assertEquals(0, cpu.GetFlag(Flags.N))
    }

    @Test
    fun testLDA() {
        //Assembly
        /* LDA #$05 */

        // Instructions
        /* Store 0x5 in accumulator */

        // Arrange
        var hex = "a9 05" //
        var instruction_length = 1

        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(5.toUByte(), cpu.A())
        assertEquals(0, cpu.GetFlag(Flags.N))
        assertEquals(0, cpu.GetFlag(Flags.Z))
    }

    @Test
    fun testLDAZero() {
        //Assembly
        /* LDA #$05 */

        // Instructions
        /* Store 0x5 in accumulator */

        // Arrange
        var hex = "a9 00" //
        var instruction_length = 1

        // Act
        cpu.runInstruction(hex, instruction_length)

        // Assert
        assertEquals(0.toUByte(), cpu.A())
        assertEquals(0, cpu.GetFlag(Flags.N))
        assertEquals(1, cpu.GetFlag(Flags.Z))
    }




}