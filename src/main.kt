fun main()
{
    var bus = Bus()

    // load instructions in hex
    // tool : https://skilldrick.github.io/easy6502/

    // EXAMPLE
    // LDA #12 -> set A register to 12
    // ORA #11 -> sets A register (12) to A|11 -> 15
    // LDX #12 -> set X register to 12
    // LDY #10 -> set Y register to 10
    var hex = "A9 0c 09 0b a0 0a a2 0c"
    bus.LoadInstructions(0x00FFu, hex)

    var cpu = Cpu6502()
    cpu.ConnectBus(bus)
    cpu.Reset()

    // executing instructions
    var ins = 0
    while (ins < 16)
    {
        cpu.Clock()
        ins += 1
        cpu.Info()
    }
}