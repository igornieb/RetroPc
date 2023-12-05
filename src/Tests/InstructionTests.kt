package Tests

import Bus
import Cpu6502

fun main()
{
    var bus = Bus()
    var cpu = Cpu6502()
    cpu.ConnectBus(bus)

    // load instructions in hex

    // tool : https://skilldrick.github.io/easy6502/

    testLDA(cpu)
}



fun testLDA(cpu: Cpu6502) {
    var hex = "a9 05" //Store 5 in accumulator
    var instruction_length = 1
    cpu.runInstruction(hex, instruction_length)
    if (cpu.A().toInt() == 5) {
        print("LDA test passed")
    } else {
        print("LDA test failed")
    }
}