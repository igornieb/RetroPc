package Tests

import Bus
import Cpu6502
import java.io.File
import java.io.InputStream

fun main()
{
    var bus = Bus()
    var cpu = Cpu6502()
    cpu.ConnectBus(bus)

    // load instructions in hex

    // tool : https://skilldrick.github.io/easy6502/

    testLDA(cpu)
    nestest()
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

fun nestest() {
    var prevLine: String = ""
    File("src/Tests/nestest.txt").forEachLine {
        // Pass the previous line into test, because
        if (prevLine != "") {
            runNestestLine(it, prevLine) //Run the command from previous line and check with current line status
            prevLine = it
        } else {
            prevLine = it
        }
    }
}



fun runNestestLine(line: String, prevLine: String) {
    var line = line.split(" ", ",", ":").filter{it.isNotEmpty()}
    val lineLength = line.size
    val cycle = line[lineLength-1]
    val ppu = line[lineLength-3]
    val p =line[lineLength-8]
    val y =line[lineLength-10]
    val x =line[lineLength-12]
    val a =line[lineLength-14]


    // #TODO isolate command from prevLine, execute, compare spu state to the expeccted above
    print(line[lineLength-14])
    print("\n")
}