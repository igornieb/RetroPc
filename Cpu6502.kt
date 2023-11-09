data class Instruction(var opcode:Function<Byte>, var addrmode:Function<Byte>, var cycles:Byte)

enum class Flags(val value:Int)
{
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
    private var a : Int = 0x00,
    private var x : Int = 0x00,
    private var y : Int = 0x00,
    private var sr : Int = 0x00,
    private var sp : Int = 0x00,
    private var addressAbs : Int = 0x0000,
    private var addressRel : Int = 0x0000,
    // current fetched data (used for instructions)
    private var fetched : Int = 0x00,
    // https://www.youtube.com/watch?v=TGcjn8zMhfM
    private var opCode: Int = 0x00, // current operation code
    // cycles left for current instruction
    private var cycles : Int = 0x00,
    ){
    init {
        lookup = listOf(
            Instruction(this::BRK, this::IMM, 7), Instruction(this::ORA, this::IZX, 6), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 3), Instruction(this::ORA,this::ZPG, 3), Instruction(this::ASL, this::ZPG, 5), Instruction(this::XXX, this::IMP, 5), Instruction(this::PHP, this::IMP, 3), Instruction(this::ORA, this::IMM, 2), Instruction(this::ASL, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::NOP, this::IMP, 4), Instruction(this::ORA, this::ABS, 4), Instruction(this::ASL, this::ABS, 6), Instruction(this::XXX, this::IMP, 6),
            Instruction(this::BPL, this::IMM, 2), Instruction(this::ORA, this::IZY, 5), Instruction(this::XXX, this::IMP, 2),  Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 4), Instruction(this::ORA,this::ZPX, 4), Instruction(this::ASL, this::ZPX, 6), Instruction(this::XXX, this::IMP, 6), Instruction(this::CLC, this::IMP, 2), Instruction(this::ORA, this::ABY, 4), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 7), Instruction(this::NOP, this::IMP, 4), Instruction(this::ORA, this::ABX, 4), Instruction(this::ASL, this::ABX, 7), Instruction(this::XXX, this::IMP, 7),
            Instruction(this::JSR, this::ABS, 6), Instruction(this::AND, this::IZX, 6), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::BIT, this::ZPG, 3), Instruction(this::AND,this::ZPG, 3), Instruction(this::ROL, this::ZPG, 5), Instruction(this::XXX, this::IMP, 5), Instruction(this::PLP, this::IMP, 4), Instruction(this::AND, this::IMM, 2), Instruction(this::ROL, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::BIT, this::ABS, 4), Instruction(this::AND, this::ABS, 4), Instruction(this::ROL, this::ABS, 6), Instruction(this::XXX, this::IMP, 6),
            Instruction(this::BMI, this::REL, 2), Instruction(this::AND, this::IZY, 5), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 4), Instruction(this::AND,this::ZPX, 4), Instruction(this::ROL, this::ZPX, 6), Instruction(this::XXX, this::IMP, 6), Instruction(this::SEC, this::IMP, 2), Instruction(this::AND, this::ABY, 4), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 7), Instruction(this::NOP, this::IMP, 4), Instruction(this::AND, this::ABX, 4), Instruction(this::ROL, this::ABX, 7), Instruction(this::XXX, this::IMP, 7),
            Instruction(this::RTI, this::IMP, 6), Instruction(this::EOR, this::IZX, 6), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 3), Instruction(this::EOR, this::ZPG, 3), Instruction(this::LSR, this::ZPG, 5), Instruction(this::XXX, this::IMP, 5), Instruction(this::PHA, this::IMP, 3), Instruction(this::EOR, this::IMM, 2), Instruction(this::LSR, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::JMP, this::ABS, 3), Instruction(this::EOR, this::ABS, 4), Instruction(this::LSR, this::ABS, 6), Instruction(this::XXX, this::IMP, 6),
            Instruction(this::BVC, this::REL, 2), Instruction(this::EOR, this::IZY, 5), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 4), Instruction(this::EOR, this::ZPX, 4), Instruction(this::LSR, this::ZPX, 6), Instruction(this::XXX, this::IMP, 6), Instruction(this::CLI, this::IMP, 2), Instruction(this::EOR, this::ABY, 4), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 7), Instruction(this::NOP, this::IMP, 4), Instruction(this::EOR, this::ABX, 4), Instruction(this::LSR, this::ABX, 7), Instruction(this::XXX, this::IMP, 7),
            Instruction(this::RTS, this::IMP, 6), Instruction(this::ADC, this::IZX, 6), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 3), Instruction(this::ADC, this::ZPG, 3), Instruction(this::ROR, this::ZPG, 5), Instruction(this::XXX, this::IMP, 5), Instruction(this::PLA, this::IMP, 4), Instruction(this::ADC, this::IMM, 2), Instruction(this::ROR, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::JMP, this::IND, 5), Instruction(this::ADC, this::ABS, 4), Instruction(this::ROR, this::ABS, 6), Instruction(this::XXX, this::IMP, 6),
            Instruction(this::BVS, this::REL, 2), Instruction(this::ADC, this::IZY, 5), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 4), Instruction(this::ADC, this::ZPX, 4), Instruction(this::ROR, this::ZPX, 6), Instruction(this::XXX, this::IMP, 6), Instruction(this::SEI, this::IMP, 2), Instruction(this::ADC, this::ABY, 4), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 7), Instruction(this::NOP, this::IMP, 4), Instruction(this::ADC, this::ABX, 4), Instruction(this::ROR, this::ABX, 7), Instruction(this::XXX, this::IMP, 7),
            Instruction(this::NOP, this::IMP, 2), Instruction(this::STA, this::IZX, 6), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 6), Instruction(this::STY, this::ZPG, 3), Instruction(this::STA, this::ZPG, 3), Instruction(this::STX, this::ZPG, 3), Instruction(this::XXX, this::IMP, 3), Instruction(this::DEY, this::IMP, 2), Instruction(this::NOP, this::IMP, 2), Instruction(this::TXA, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::STY, this::ABS, 4), Instruction(this::STA, this::ABS, 4), Instruction(this::STX, this::ABS, 4), Instruction(this::XXX, this::IMP, 4),
            Instruction(this::BCC, this::REL, 2), Instruction(this::STA, this::IZY, 6), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 6), Instruction(this::STY, this::ZPX, 4), Instruction(this::STA, this::ZPX, 4), Instruction(this::STX, this::ZPY, 4), Instruction(this::XXX, this::IMP, 4), Instruction(this::TYA, this::IMP, 2), Instruction(this::STA, this::ABY, 5), Instruction(this::TXS, this::IMP, 2), Instruction(this::XXX, this::IMP, 5), Instruction(this::NOP, this::IMP, 5), Instruction(this::STA, this::ABX, 5), Instruction(this::XXX, this::IMP, 5), Instruction(this::XXX, this::IMP, 5),
            Instruction(this::LDY, this::IMM, 2), Instruction(this::LDA, this::IZX, 6), Instruction(this::LDX, this::IMM, 2), Instruction(this::XXX, this::IMP, 6), Instruction(this::LDY, this::ZPG, 3), Instruction(this::LDA, this::ZPG, 3), Instruction(this::LDX, this::ZPG, 3), Instruction(this::XXX, this::IMP, 3), Instruction(this::TAY, this::IMP, 2), Instruction(this::LDA, this::IMM, 2), Instruction(this::TAX, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::LDY, this::ABS, 4), Instruction(this::LDA, this::ABS, 4), Instruction(this::LDX, this::ABS, 4), Instruction(this::XXX, this::IMP, 4),
            Instruction(this::BCS, this::REL, 2), Instruction(this::LDA, this::IZY, 5), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 5), Instruction(this::LDY, this::ZPX, 4), Instruction(this::LDA, this::ZPX, 4), Instruction(this::LDX, this::ZPY, 4), Instruction(this::XXX, this::IMP, 4), Instruction(this::CLV, this::IMP, 2), Instruction(this::LDA, this::ABY, 4), Instruction(this::TSX, this::IMP, 2), Instruction(this::XXX, this::IMP, 4), Instruction(this::LDY, this::ABX, 4), Instruction(this::LDA, this::ABX, 4), Instruction(this::LDX, this::ABY, 4), Instruction(this::XXX, this::IMP, 4),
            Instruction(this::CPY, this::IMM, 2), Instruction(this::CMP, this::IZX, 6), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::CPY, this::ZPG, 3), Instruction(this::CMP, this::ZPG, 3), Instruction(this::DEC, this::ZPG, 5), Instruction(this::XXX, this::IMP, 5), Instruction(this::INY, this::IMP, 2), Instruction(this::CMP, this::IMM, 2), Instruction(this::DEX, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::CPY, this::ABS, 4), Instruction(this::CMP, this::ABS, 4), Instruction(this::DEC, this::ABS, 6), Instruction(this::XXX, this::IMP, 6),
            Instruction(this::BNE, this::REL, 2), Instruction(this::CMP, this::IZY, 5), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 4), Instruction(this::CMP, this::ZPX, 4), Instruction(this::DEC, this::ZPX, 6), Instruction(this::XXX, this::IMP, 6), Instruction(this::CLD, this::IMP, 2), Instruction(this::CMP, this::ABY, 4), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 7), Instruction(this::CPX, this::ABS, 4), Instruction(this::CMP, this::ABX, 4), Instruction(this::DEC, this::ABX, 7), Instruction(this::XXX, this::IMP, 7),
            Instruction(this::CPX, this::IMM, 2), Instruction(this::SBC, this::IZX, 6), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::CPX, this::ZPG, 3), Instruction(this::SBC, this::ZPG, 3), Instruction(this::INC, this::ZPG, 5), Instruction(this::XXX, this::IMP, 5), Instruction(this::INX, this::IMP, 2), Instruction(this::SBC, this::IMM, 2), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 2), Instruction(this::CPX, this::ABS, 4), Instruction(this::SBC, this::ABS, 4), Instruction(this::INC, this::ABS, 6), Instruction(this::XXX, this::IMP, 6),
            Instruction(this::BEQ, this::REL, 2), Instruction(this::SBC, this::IZY, 5), Instruction(this::XXX, this::IMP, 2), Instruction(this::XXX, this::IMP, 8), Instruction(this::NOP, this::IMP, 4), Instruction(this::SBC, this::ZPX, 4), Instruction(this::INC, this::ZPX, 6), Instruction(this::XXX, this::IMP, 6), Instruction(this::SED, this::IMP, 2), Instruction(this::SBC, this::ABY, 4), Instruction(this::NOP, this::IMP, 2), Instruction(this::XXX, this::IMP, 7), Instruction(this::NOP, this::IMP, 4), Instruction(this::SBC, this::ABX, 4), Instruction(this::INC, this::ABX, 7), Instruction(this::XXX, this::IMP, 7),
            )
    }

    private fun Fetch(): Int {
        if (lookup[opCode].addrmode !== this::IMP) {
            fetched = Read(addressAbs)
        }
        return fetched
    }

    fun Clock(){
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
            sr = sr or f.value
        }
        else{
            sr = sr and f.value.inv()
        }
    }
    
    private fun GetFlag(f : Flags) : Int {
        return if ((sr and f.value) > 0) 1 else 0
    }

    // addressing modes (opcodes)
    // https://www.youtube.com/watch?v=TGcjn8zMhfM


    fun IMP(): Byte {
        fetched = a.toInt()
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

    // Instructions (52 difrent instructions)
    fun ADC(): Byte {
        Fetch()
        val tmp = a + fetched + GetFlag(Flags.C)
        SetFlag(Flags.C, tmp > 255)
        SetFlag(Flags.Z, tmp and 0x00FF == 0)
        SetFlag(Flags.V, (((a xor fetched) and (a xor tmp)).inv() and 0x0080).toInt() == 1)
        SetFlag(Flags.N, tmp and 0x80 == 1)
        a = (tmp and 0x00FF)
        return 1
    }

    fun AND(): Byte {
        Fetch()
        a = (fetched and a)
        return 0
    }

    fun ASL(): Byte {
        Fetch()
        a = (a shl 1)
        return 0
    }

    fun BCC(): Byte {
        Fetch()
        if (GetFlag(Flags.C).toInt() == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun BCS(): Byte {
        Fetch()
        if (GetFlag(Flags.C).toInt() == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun BEQ(): Byte {
        if (GetFlag(Flags.Z).toInt() == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun BIT(): Byte {
        Fetch()
        val tmp: Int = a and fetched
        SetFlag(Flags.Z, tmp and 0x00FF == 0x00)
        SetFlag(Flags.N, fetched and (1 shl 7) == 1)
        SetFlag(Flags.V, fetched and (1 shl 6) == 1)
        return 0
    }

    fun BMI(): Byte {
        Fetch()
        if (GetFlag(Flags.N).toInt() == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun BNE(): Byte {
        Fetch()
        if (GetFlag(Flags.Z).toInt() == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun BPL(): Byte {
        Fetch()
        if (GetFlag(Flags.N).toInt() == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun BRK(): Byte {
        pc += 1
        SetFlag(Flags.I, true)
        Write((0x0100 + sp), (pc shr 8 and 0x00FF))
        sp--
        Write((0x0100 + sp), (pc and 0x00FF))
        sp--
        return 0
    }

    fun BVC(): Byte {
        Fetch()
        if (GetFlag(Flags.V).toInt() == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun BVS(): Byte {
        Fetch()
        if (GetFlag(Flags.V).toInt() == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 0xFF00 !== pc and 0xFF00) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    fun CLC(): Byte {
        SetFlag(Flags.C, false)
        return 0
    }

    fun CLD(): Byte {
        SetFlag(Flags.D, false)
        return 0
    }

    fun CLI(): Byte {
        SetFlag(Flags.I, false)
        return 0
    }

    fun CLV(): Byte {
        SetFlag(Flags.V, false)
        return 0
    }

    fun CMP(): Byte {
        Fetch()
        val tmp = a - fetched
        SetFlag(Flags.Z, tmp and 0x00FF == 0x0000)
        SetFlag(Flags.C, a > fetched)
        SetFlag(Flags.N, tmp and 0x0080 == 1)
        return 1
    }

    fun CPX(): Byte {
        Fetch()
        val tmp = x - fetched
        SetFlag(Flags.Z, tmp and 0x00FF == 0x0000)
        SetFlag(Flags.C, x > fetched)
        SetFlag(Flags.N, tmp and 0x0080 == 1)
        return 0
    }

    fun CPY(): Byte {
        Fetch()
        val tmp = y - fetched
        SetFlag(Flags.Z, tmp and 0x00FF == 0x0000)
        SetFlag(Flags.C, y > fetched)
        SetFlag(Flags.N, tmp and 0x0080 == 1)
        return 0
    }

    fun DEC(): Byte {
        Fetch()
        fetched -= 1
        Write(addressAbs, fetched)
        SetFlag(Flags.Z, fetched and 0x00FF == 0x0000)
        SetFlag(Flags.N, fetched and 0x0080 == 1)
        return 0
    }

    fun DEX(): Byte {
        x -= 1
        SetFlag(Flags.Z, x and 0x00FF == 0x0000)
        SetFlag(Flags.N, x and 0x0080 == 1)
        return 0
    }

    fun DEY(): Byte {
        y -= 1
        SetFlag(Flags.Z, y and 0x00FF == 0x0000)
        SetFlag(Flags.N, y and 0x0080 == 1)
        return 0
    }

    fun EOR(): Byte {
        Fetch()
        a = (a xor fetched)
        SetFlag(Flags.N, a and 0x80 == 1)
        SetFlag(Flags.Z, a == 0x00)
        return 1
    }

    fun INC(): Byte {
        Fetch()
        fetched += 1
        Write(addressAbs, fetched)
        SetFlag(Flags.Z, fetched and 0x00FF == 0x0000)
        SetFlag(Flags.N, fetched and 0x0080 == 1)
        return 0
    }

    fun INX(): Byte {
        x += 1
        SetFlag(Flags.Z, x and 0x00FF == 0x0000)
        SetFlag(Flags.N, x and 0x0080 == 1)
        return 0
    }

    fun INY(): Byte {
        y += 1
        SetFlag(Flags.Z, y and 0x00FF == 0x0000)
        SetFlag(Flags.N, y and 0x0080 == 1)
        return 0
    }

    fun JMP(): Byte {
        pc = addressAbs
        return 0
    }

    fun JSR(): Byte {
        pc -= 1
        Write((0x0100 + sp), (pc shr 8 and 0x00FF))
        sp -= 1
        Write((0x0100 + sp), (pc shr 8 and 0x00FF))
        sp -= 1
        pc = addressAbs
        return 0
    }

    fun LDA(): Byte {
        Fetch()
        a = fetched
        SetFlag(Flags.Z, fetched == 0x00)
        SetFlag(Flags.N, fetched and 0x0080 == 1)
        return 1
    }

    fun LDX(): Byte {
        Fetch()
        x = fetched
        SetFlag(Flags.Z, x == 0x00)
        SetFlag(Flags.N, x and 0x80 == 1)
        return 1
    }

    fun LDY(): Byte {
        Fetch()
        y = fetched
        SetFlag(Flags.Z, y == 0x00)
        SetFlag(Flags.N, y and 0x80 == 1)
        return 1
    }

    fun LSR(): Byte {
        Fetch()
        SetFlag(Flags.C, fetched and 0x0001 == fetched)
        val tmp = fetched shr 1
        SetFlag(Flags.Z, tmp == 0x00)
        SetFlag(Flags.N, tmp and 0x0080 == 1)
        if (lookup.get(opCode).addrmode == this::IMP) {
            a = (tmp and 0x00FF)
        } else {
            Write(addressAbs, (tmp and 0x00FF))
        }
        return 0
    }

    fun NOP(): Byte {
        // zachowanie nie jest w pełni zaimplementowane (illegal opcodes)
        return 0
    }

    fun ORA(): Byte {
        Fetch()
        a = a or fetched
        SetFlag(Flags.Z, a == 0x00)
        SetFlag(Flags.N, a and 0x0080 == 1)
        return 1
    }

    fun PHA(): Byte {
        Write((0x0100 + sp), a)
        sp -= 1
        return 0
    }

    fun PHP(): Byte {
        // SetFlag(Flags.B, true);
        sp += 1
        Write((0x0100 + sp), sr)
        // SetFlag(Flags.B, false);
        // SetFlag(Flags.U, false);
        return 0
    }

    fun PLA(): Byte {
        sp += 1
        a = Read((0x0100 + sp))
        SetFlag(Flags.Z, a == 0x00)
        SetFlag(Flags.N, a and 0x80 == 1)
        return 0
    }

    fun PLP(): Byte {
        sp += 1
        sr = Read((0x0100 + sp))
        SetFlag(Flags.U, true)
        return 0
    }

    fun ROL(): Byte {
        Fetch()
        val tmp = fetched shl 1 or GetFlag(Flags.C)
        SetFlag(Flags.Z, tmp == 0x00)
        SetFlag(Flags.N, tmp and 0x80 == 1)
        SetFlag(Flags.C, tmp and 0x0001 == 1)
        if (lookup[opCode].opcode == this::IMP) {
            a = (tmp and 0x00FF)
        } else {
            Write(addressAbs, (tmp and 0x00FF))
        }
        return 0
    }

    fun ROR(): Byte {
        Fetch()
        val tmp = fetched shr 1 or (GetFlag(Flags.C) shl 7)
        SetFlag(Flags.Z, tmp == 0x00)
        SetFlag(Flags.N, tmp and 0x80 == 1)
        SetFlag(Flags.C, tmp and 0x0001 == 1)
        if (lookup.get(opCode).opcode == this::IMP) {
            a = (tmp and 0x00FF)
        } else {
            Write(addressAbs, (tmp and 0x00FF))
        }
        return 0
    }

    fun RTI(): Byte {
        sp += 1
        sr = Read((0x0100 + sp))
        sr = sr and GetFlag(Flags.B).inv()
        sr = sr and GetFlag(Flags.U).inv()
        sp += 1
        pc = Read((0x0100 + sp))
        sp += 1
        pc = pc or (Read((0x0100 + sp)) shl 8)
        return 0
    }

    fun RTS(): Byte {
        sp += 1
        pc = Read((0x0100 + sp))
        sp += 1
        pc = pc or (Read((0x0100 + sp)) shl 8)
        sp += 1
        return 0
    }

    fun SBC(): Byte {
        Fetch()
        val value = fetched xor 0x00FF
        val tmp = a + value + GetFlag(Flags.C)
        SetFlag(Flags.C, tmp > 255)
        SetFlag(Flags.Z, tmp and 0x00FF == 0)
        SetFlag(Flags.V, ((a xor tmp) and (value xor tmp)).inv() and 0x0080 == 1)
        SetFlag(Flags.N, tmp and 0x80 == 1)
        a = (tmp and 0x00FF)
        return 1
    }

    fun SEC(): Byte {
        SetFlag(Flags.C, true)
        return 0
    }

    fun SED(): Byte {
        SetFlag(Flags.D, true)
        return 0
    }

    fun SEI(): Byte {
        SetFlag(Flags.I, true)
        return 0
    }

    fun STA(): Byte {
        Write(addressAbs, a)
        return 0
    }

    fun STX(): Byte {
        Write(addressAbs, x)
        return 0
    }

    fun STY(): Byte {
        Write(addressAbs, y)
        return 0
    }

    fun TAX(): Byte {
        x = a
        SetFlag(Flags.N, x and 0x80 == 1)
        SetFlag(Flags.Z, x == 0x00)
        return 0
    }

    fun TAY(): Byte {
        y = a
        SetFlag(Flags.N, y and 0x80 == 1)
        SetFlag(Flags.Z, y == 0x00)
        return 0
    }

    fun TSX(): Byte {
        x = sp
        SetFlag(Flags.N, x and 0x80 == 1)
        SetFlag(Flags.Z, x == 0x00)
        return 0
    }

    fun TXA(): Byte {
        a = x
        SetFlag(Flags.N, a and 0x80 == 1)
        SetFlag(Flags.Z, a == 0x00)
        return 0
    }

    fun TXS(): Byte {
        sr = x
        return 0
    }

    fun TYA(): Byte {
        a = y
        SetFlag(Flags.N, a and 0x80 == 1)
        SetFlag(Flags.Z, a == 0x00)
        return 0
    }

    // represents all illegal instructions
    fun XXX(): Byte {
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

    public fun Write(address: Int, data: Int)
    {
        bus.Write(address, data)
    }


}
