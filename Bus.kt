class Bus constructor(private var ram : ByteArray = ByteArray(256*256), private var cpu : Cpu6502 = Cpu6502()){
    init {
        // reset memory
        for(i in (0..ram.size))
        {
            ram[i]=0x00;
        }

        //connect cpu with bus
        cpu.ConnectBus(this);
    }

    public fun Read(address:Short) : Byte
    {
        // currently ram is only thing on the bus so reading is done to range - 0-256^2
        if (address in 0x0000..0xffff)
        {
            return ram[address.toInt()];
        }

        return 0x00;
    }

    public fun Write(address: Int, data: Int){
        // currently ram is only thing on the bus so writing is done to its range - 0-256^2
        if (address in 0x0000..0xffff)
        {
            ram[address.toInt()] = data
        }
    }

    public fun LoadInstructions(address:Short, data:String)
    {
        // TODO load instructions
    }
}