import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

data class Instruction(var opcode:Function<Byte>, var addrmode:Function<Byte>, var cycles:Byte)

enum class Flags(val value:Int)
{
    // moves bits by flag value
    // moves bits by flag value
    N(1 shl 7), // negative
    V(1 shl 6), // overflow
    U(1 shl 5), // ignored, used with signed variables
    B(1 shl 4), // break
    D(1 shl 3), // decimal
    I(1 shl 2), // interrupt
    Z(1 shl 1), // zero
    C(1 shl 0)  // carry
}

class Cpu6502(
    private var bus: Bus,
    private var lookup: List<Instruction>,

    // registers declaration
    // PC	program counter	(16 bit)
    // AC	accumulator	(8 bit)
    // X	X register	(8 bit)
    // Y	Y register	(8 bit)
    // SR	status register	(8 bit)
    // SP	stack pointer	(8 bit)
    private var pc : Int = 0x0000,
    private var a : Byte = 0x00,
    private var x : Byte = 0x00,
    private var y : Byte = 0x00,
    private var sr : Byte = 0x00,
    private var sp : Byte = 0x00,
    private var addressAbs : Int = 0x0000,
    private var addressRel : Int = 0x0000,
    // current fetched data (used for instructions)
    private var fetched : Byte= 0x00,

    // current operation code
    // https://www.youtube.com/watch?v=TGcjn8zMhfM
    private var opCode: Byte = 0x00,
    // cycles left for current instruction
    private var cycles : Int = 0x00,

    ){
    init {
        lookup = listOf(
//            Instruction(this::BRK, this::BRK, 0x00)
        )
    }

    public fun Clock(){
        // TODO
        if (cycles==0)
        {

        }

        cycles-=1
    }

    fun Reset(){
        // TODO
    }

    private fun Irq()
    {
        // TODO
    }

    private fun Nmi()
    {
        // TODO
    }

    private fun SetFlag(f : Flags, v: Boolean)
    {
        if (v){
            sr = sr or f.value.toByte()
        }
        else{
            sr = sr and f.value.toByte().inv()
        }
    }
    
    private fun GetFlag(f : Flags) : Byte {
        return if ((sr and f.value.toByte()) > 0) 1.toByte() else 0.toByte()
    }

    // addressing modes (opcodes)
    // https://www.youtube.com/watch?v=TGcjn8zMhfM
    private val _opCode: Byte = 0x00 // current operation code


    fun IMP(): Byte {
        fetched = a
        return 0
    }

    fun IMM(): Byte {
        addressAbs = pc++
        return 0
    }

    fun ABS(): Byte {
        var lo = Read(pc)
        pc += 1
        var hi = Read(pc)
        pc +=1
        addressAbs = ((hi shl 8) or lo)
        return 0
    }

    fun ABX(): Byte {
        val lo = Read(pc)
        pc += 1
        val hi = Read(pc)
        pc += 1
        addressAbs = (hi shl 8 or lo)
        addressAbs += x
        return if (addressAbs and 0xFF00 != hi shl 8) 1 else 0
    }

    fun ABY(): Byte {
        val lo = Read(pc)
        pc += 1
        val hi = Read(pc)
        pc += 1
        addressAbs = (hi shl 8 or lo)
        addressAbs += y
        return if (addressAbs and 0xFF00 != hi shl 8) 1 else 0
    }
    fun IND() : Byte {
        val lo = Read(pc)
        pc += 1
        val hi = Read(pc)
        pc += 1
        val tmpPtr = (hi shl 8 or lo)
        //implement hardware bug
        if (lo == 0x00FF) {
            addressAbs = (Read((tmpPtr and 0xFF00)) or Read(tmpPtr))
        } else {
            addressAbs = ((Read((tmpPtr + 1)) shl 8) or Read(tmpPtr))
        }
        return 0
    }

    fun IZX(): Byte {
        val tmp = Read(pc)
        pc += 1
        val lo = Read((tmp + x and 0x00FF))
        val hi = Read((tmp + x + 1 and 0x00FF))
        addressAbs = (hi shl 8 or lo)
        return 0
    }

    fun IZY(): Byte {
        val tmp = Read(pc)
        pc += 1
        val lo = Read((tmp + x and 0x00FF))
        val hi = Read((tmp + x + 1 and 0x00FF))
        addressAbs = (hi shl 8 or lo)
        addressAbs += y
        return if (addressAbs and 0xFF00 !== hi shl 8) {
            1
        } else {
            0
        }
    }

    fun ZPG(): Byte {
        addressAbs = Read(pc)
        pc += 1
        addressAbs = addressAbs and 0x00FF
        return 0
    }

    fun ZPX(): Byte {
        addressAbs = Read((pc + x))
        pc += 1
        addressAbs = (addressAbs and 0x00FF)
        return 0
    }

    fun ZPY(): Byte {
        addressAbs = Read((pc + y))
        pc += 1
        addressAbs = (addressAbs and 0x00FF)
        return 0
    }

    fun REL(): Byte {
        addressRel = Read(pc)
        pc += 1
        if (addressRel and 0x80 == addressRel)
        {
            addressRel = addressRel or 0xFF00
        }
        return 0
    }




    // connecting to bus, interacting  with it
    public fun ConnectBus(bus : Bus)
    {
        this.bus = bus
    }

    public fun Read(address : Int) : Int
    {
        return bus.Read(address.toShort()).toInt()
    }

    public fun Write(address : Short, data : Byte)
    {
        bus.Write(address, data)
    }


}
