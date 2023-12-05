fun main()
{
    // tool : https://skilldrick.github.io/easy6502/

    testLDA()
}

fun runProcessor(hex: String, length: Int) {
    var bus = Bus()

    // load instructions in hex
    bus.LoadInstructions(0x00FFu, hex)

    var cpu = Cpu6502()
    cpu.ConnectBus(bus)
    cpu.Reset()

    // executing instructions
    cpu.Info()
    repeat(length) {
        cpu.Clock()
        cpu.Info()
    }
}

fun testLDA() {
    var hex = "a9 05" //Store 5 in accumulator
    var instruction_length = 1
    runProcessor(hex, instruction_length)
}