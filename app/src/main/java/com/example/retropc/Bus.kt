package com.example.retropc

import android.util.Log

class Bus {
    @OptIn(ExperimentalUnsignedTypes::class)
    private var ram : UByteArray = UByteArray(256*256)
    private var cpu : Cpu6502 = Cpu6502()
    init {
        // reset memory
        for(i in (0..ram.size-1))
        {
            ram[i]=0u
        }

        //connect cpu with bus
        cpu.ConnectBus(this)
    }

    fun Read(address:UInt) : UByte
    {
        // currently ram is only thing on the bus so reading is done to range - 0-256^2
        if (address in (0x0000u..0xffffu)) {
            return ram[address.toInt()]
        }
        return 0.toUByte();
    }

    fun Write(address : UInt, data : UByte){
        // currently ram is only thing on the bus so writing is done to its range - 0-256^2
        if (address.toInt() in 0x0000..0xffff)
        {
            ram[address.toInt()] = data
        }
    }

    fun LoadInstructions(address:UInt, data:String)
    {
        // setting starting point to address
        // after Reset method is called on cpu, this location will be read and execution will start from address
        ram[0xFFFC] = address.toUByte()

        // BM: for safety remove new line chars
        var hex : List<String> = data.replace("\n","").split(" ")


        var tmpAddress = address
        for (element in hex)
        {
            var value = element.toUByte(radix = 16)
            Write(tmpAddress, value)
            tmpAddress++
        }
        println("Program loaded");

    }
}