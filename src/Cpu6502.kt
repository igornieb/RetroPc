data class Instruction(var opcode:() -> Byte, var addrmode:() -> Byte, var cycles:Int)

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

class Cpu6502{
    private lateinit var bus: Bus
    private var lookup: List<Instruction>

    // registers declaration
    // PC	program counter	(16 bit)
    // AC	accumulator	(8 bit)
    // X	X register	(8 bit)
    // Y	Y register	(8 bit)
    // SR	status register	(8 bit)
    // SP	stack pointer	(8 bit)
    private var pc : UInt = 0.toUInt()
    private var a : UByte = 0.toUByte()
    private var x : UByte = 0.toUByte()
    private var y : UByte = 0.toUByte()
    private var sr : UByte = 0.toUByte()
    private var sp : UByte = 0.toUByte()
    private var addressAbs : UInt = 0.toUInt()
    private var addressRel : UInt = 0.toUInt()
    // current fetched data (used for instructions)
    private var fetched : UByte = 0.toUByte()
    // https://www.youtube.com/watch?v=TGcjn8zMhfM
    private var opCode: Int = 0 // current operation code
    // cycles left for current instruction
    private var cycles : Int = 0
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
    private fun Fetch(): UByte {
        if (lookup[opCode].addrmode !== this::IMP) {
            fetched = Read(addressAbs)
        }
        return fetched
    }

    fun Clock(){
        if (cycles == 0)
        {
            opCode = Read(pc).toInt()
            SetFlag(Flags.U, true)
            pc++
            cycles = lookup[opCode].cycles
            var additionalCycle1 = (lookup[opCode].addrmode)()

            // Perform operation
            var additionalCycle2 = (lookup[opCode].opcode)()

            // The addressmode and opcode may have altered the number
            // of cycles this instruction requires before its completed
            cycles += (additionalCycle1.toInt() and additionalCycle2.toInt())

            // Always set the unused status flag bit to 1
            SetFlag(Flags.U, true)
        }

        cycles -= 1
    }

    fun Reset(){
        addressAbs = 65532u
        val hi = Read((addressAbs + 1u) )
        val lo = Read((addressAbs + 0u) )
        // set address
        pc = (hi.toInt() shl 8 or lo.toInt()).toUInt()
        // reset all registers
        a = 0u
        x = 0u
        y = 0u
        sp = 0u
        sr = 0u
        // reset helper variables
        // reset helper variables
        addressRel = 0u
        addressAbs = 0u
        fetched = 0u
        //set cycles
        //set cycles
        cycles = 8

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
            sr = sr or f.value.toUByte()
        }
        else{
            sr = sr and f.value.inv().toUByte()
        }
    }
    
    private fun GetFlag(f : Flags) : Int {
        return if ((sr and f.value.toUByte()) > 0u) 1 else 0
    }

    // addressing modes (opcodes)
    // https://www.youtube.com/watch?v=TGcjn8zMhfM


    private fun IMP(): Byte {
        fetched = a
        return 0
    }

    private fun IMM(): Byte {
        addressAbs = pc++
        return 0
    }

    private fun ABS(): Byte {
        var lo = Read(pc)
        pc++
        var hi = Read(pc)
        pc++
        addressAbs = ((hi.toInt() shl 8) or lo.toInt()).toUInt()
        return 0
    }

    private fun ABX(): Byte {
        val lo = Read(pc)
        pc++
        val hi = Read(pc)
        pc++
        addressAbs = ((hi.toInt() shl 8) or lo.toInt()).toUInt()
        addressAbs += x
        return if ((addressAbs and 65280u).toInt() != hi.toInt() shl 8) 1 else 0
    }

    private fun ABY(): Byte {
        val lo = Read(pc)
        pc++
        val hi = Read(pc)
        pc++
        addressAbs = ((hi.toInt() shl 8) or lo.toInt()).toUInt()
        addressAbs += y
        return if ((addressAbs and 65280u).toInt() != hi.toInt() shl 8) 1 else 0
    }
    private fun IND() : Byte {
        val lo = Read(pc)
        pc++
        val hi = Read(pc)
        pc++
        val tmpPtr = (hi.toInt() shl 8 or lo.toInt())
        //implement hardware bug
        addressAbs = (Read((tmpPtr and 0xFF00).toUInt()) or Read(tmpPtr.toUInt())).toUInt()
        return 0
    }

    private fun IZX(): Byte {
        val tmp = Read(pc)
        pc++
        val lo = Read((tmp + x and 255u))
        val hi = Read((tmp + x + 1u and 255u))
        addressAbs = (hi.toInt() shl 8 or lo.toInt()).toUInt()
        return 0
    }

    private fun IZY(): Byte {
        val tmp = Read(pc)
        pc++
        val lo = Read((tmp + x and 255u))
        val hi = Read((tmp + x + 1u and 255u))
        addressAbs = (hi.toInt() shl 8 or lo.toInt()).toUInt()
        addressAbs += y
        return if ((addressAbs and 65280u).toInt() !== hi.toInt() shl 8) {
            1
        } else {
            0
        }
    }

    private fun ZPG(): Byte {
        addressAbs = Read(pc).toUInt()
        pc++
        addressAbs = addressAbs and 255u
        return 0
    }

    private fun ZPX(): Byte {
        addressAbs = Read((pc + x)).toUInt()
        pc++
        addressAbs = (addressAbs and 255u)
        return 0
    }

    private fun ZPY(): Byte {
        addressAbs = Read((pc + y)).toUInt()
        pc++
        addressAbs = (addressAbs and 255u)
        return 0
    }

    private fun REL(): Byte {
        addressRel = Read(pc).toUInt()
        pc++
        if (addressRel and 128u == addressRel)
        {
            addressRel = addressRel or 65280u
        }
        return 0
    }

    // Instructions (52 difrent instructions)
    private fun ADC(): Byte {
        Fetch()
        val tmp = a + fetched + GetFlag(Flags.C).toUInt()
        SetFlag(Flags.C, tmp > 255u)
        SetFlag(Flags.Z, (tmp and 255u).toInt() == 0)
        SetFlag(Flags.V, (((a xor fetched).toInt() and (a xor tmp.toUByte()).toInt()).inv() and 0x0080) == 1)
        SetFlag(Flags.N, (tmp and 128u).toInt() == 1)
        a = (tmp and 255u).toUByte()
        return 1
    }

    private fun AND(): Byte {
        Fetch()
        a = (fetched and a)
        return 0
    }

    private fun ASL(): Byte {
        Fetch()
        a = (a.toInt() shl 1).toUByte()
        return 0
    }

    private fun BCC(): Byte {
        Fetch()
        if (GetFlag(Flags.C) == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if ((addressAbs and 65280u) != (pc and 65280u)) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    private fun BCS(): Byte {
        Fetch()
        if (GetFlag(Flags.C) == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if ((addressAbs and 65280u) != (pc and 65280u)) {
                cycles++
            }
            pc = addressAbs
        }
        return 0
    }

    private fun BEQ(): Byte {
        if (GetFlag(Flags.Z) == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if ((addressAbs and 65280u) != (pc and 65280u)) {
                cycles++
            }
            pc = addressAbs
        }
        return 0
    }

    private fun BIT(): Byte {
        Fetch()
        val tmp = a and fetched
        SetFlag(Flags.Z, tmp.toInt() and 0x00FF == 0x00)
        SetFlag(Flags.N, (fetched.toInt() and (1 shl 7)) == 1)
        SetFlag(Flags.V, (fetched.toInt() and (1 shl 6)) == 1)
        return 0
    }

    private fun BMI(): Byte {
        Fetch()
        if (GetFlag(Flags.N) == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 65280u != pc and 65280u) {
                cycles++
            }
            pc = addressAbs
        }
        return 0
    }

    private fun BNE(): Byte {
        Fetch()
        if (GetFlag(Flags.Z) == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 65280u != pc and 65280u) {
                cycles++
            }
            pc = addressAbs
        }
        return 0
    }

    private fun BPL(): Byte {
        Fetch()
        if (GetFlag(Flags.N) == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 65280u != pc and 65280u) {
                cycles++
            }
            pc = addressAbs
        }
        return 0
    }

    private fun BRK(): Byte {
        pc++
        SetFlag(Flags.I, true)
        Write((0x0100u + sp), (pc shr 8 and 255u).toUByte())
        sp--
        Write((0x0100u + sp), (pc and 255u).toUByte())
        sp--
        return 0
    }

    private fun BVC(): Byte {
        Fetch()
        if (GetFlag(Flags.V) == 0) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 65280u != pc and 65280u) {
                cycles += 1
            }
            pc = addressAbs
        }
        return 0
    }

    private fun BVS(): Byte {
        Fetch()
        if (GetFlag(Flags.V) == 1) {
            cycles++
            addressAbs = (pc + addressRel)
            if (addressAbs and 65280u != pc and 65280u) {
                cycles++
            }
            pc = addressAbs
        }
        return 0
    }

    private fun CLC(): Byte {
        SetFlag(Flags.C, false)
        return 0
    }

    private fun CLD(): Byte {
        SetFlag(Flags.D, false)
        return 0
    }

    private fun CLI(): Byte {
        SetFlag(Flags.I, false)
        return 0
    }

    private fun CLV(): Byte {
        SetFlag(Flags.V, false)
        return 0
    }

    private fun CMP(): Byte {
        Fetch()
        val tmp = a - fetched
        SetFlag(Flags.Z, (tmp and 255u).toInt() == 0x0000)
        SetFlag(Flags.C, a > fetched)
        SetFlag(Flags.N, (tmp and 128u).toInt() == 1)
        return 1
    }

    private fun CPX(): Byte {
        Fetch()
        val tmp = x - fetched
        SetFlag(Flags.Z, (tmp and 255u).toInt() == 0x0000)
        SetFlag(Flags.C, x > fetched)
        SetFlag(Flags.N, (tmp and 128u).toInt() == 1)
        return 0
    }

    private fun CPY(): Byte {
        Fetch()
        val tmp = y - fetched
        SetFlag(Flags.Z, (tmp and 255u).toInt() == 0x0000)
        SetFlag(Flags.C, y > fetched)
        SetFlag(Flags.N, (tmp and 128u).toInt() == 1)
        return 0
    }

    private fun DEC(): Byte {
        Fetch()
        fetched--
        Write(addressAbs, fetched)
        SetFlag(Flags.Z, fetched.toInt() and 0x00FF == 0x0000)
        SetFlag(Flags.N, fetched.toInt() and 0x0080 == 1)
        return 0
    }

    private fun DEX(): Byte {
        x--
        SetFlag(Flags.Z, x.toInt() and 0x00FF == 0x0000)
        SetFlag(Flags.N, x.toInt() and 0x0080 == 1)
        return 0
    }

    private fun DEY(): Byte {
        y--
        SetFlag(Flags.Z, y.toInt() and 0x00FF == 0x0000)
        SetFlag(Flags.N, y.toInt() and 0x0080 == 1)
        return 0
    }

    private fun EOR(): Byte {
        Fetch()
        a = (a xor fetched)
        SetFlag(Flags.N, a.toInt() and 0x80 == 1)
        SetFlag(Flags.Z, a.toInt() == 0x00)
        return 1
    }

    private fun INC(): Byte {
        Fetch()
        fetched++
        Write(addressAbs, fetched)
        SetFlag(Flags.Z, fetched.toInt() and 0x00FF == 0x0000)
        SetFlag(Flags.N, fetched.toInt() and 0x0080 == 1)
        return 0
    }

    private fun INX(): Byte {
        x++
        SetFlag(Flags.Z, x.toInt() and 0x00FF == 0x0000)
        SetFlag(Flags.N, x.toInt() and 0x0080 == 1)
        return 0
    }

    private fun INY(): Byte {
        y++
        SetFlag(Flags.Z, y.toInt() and 0x00FF == 0x0000)
        SetFlag(Flags.N, y.toInt() and 0x0080 == 1)
        return 0
    }

    private fun JMP(): Byte {
        pc = addressAbs
        return 0
    }

    private fun JSR(): Byte {
        pc--
        Write((0x0100.toUByte() + sp), (pc shr 8 and 255u).toUByte())
        sp--
        Write((0x0100.toUByte() + sp), ((pc shr 8 and 255u).toUByte()))
        sp--
        pc = addressAbs
        return 0
    }

    private fun LDA(): Byte {
        Fetch()
        a = fetched
        SetFlag(Flags.Z, fetched.toInt() == 0x00)
        SetFlag(Flags.N, fetched.toInt() and 0x0080 == 1)
        return 1
    }

    private fun LDX(): Byte {
        Fetch()
        x = fetched
        SetFlag(Flags.Z, x.toInt() == 0x00)
        SetFlag(Flags.N, x.toInt() and 0x80 == 1)
        return 1
    }

    private fun LDY(): Byte {
        Fetch()
        y = fetched
        SetFlag(Flags.Z, y.toInt() == 0x00)
        SetFlag(Flags.N, y.toInt() and 0x80 == 1)
        return 1
    }

    private fun LSR(): Byte {
        Fetch()
        SetFlag(Flags.C, fetched and 0x0001u == fetched)
        val tmp = fetched.toInt() shr 1
        SetFlag(Flags.Z, tmp == 0x00)
        SetFlag(Flags.N, tmp and 0x0080 == 1)
        if (lookup[opCode].addrmode == this::IMP) {
            a = (tmp and 0x00FF).toUByte()
        } else {
            Write(addressAbs, (tmp and 0x00FF).toUByte())
        }
        return 0
    }

    private fun NOP(): Byte {
        // zachowanie nie jest w pełni zaimplementowane (illegal opcodes)
        return 0
    }

    private fun ORA(): Byte {
        Fetch()
        a = a or fetched
        SetFlag(Flags.Z, a.toInt() == 0x00)
        SetFlag(Flags.N, (a and 0x0080u).toInt() == 1)
        return 1
    }

    private fun PHA(): Byte {
        Write((0x0100u + sp), a)
        sp--
        return 0
    }

    private fun PHP(): Byte {
        // SetFlag(Flags.B, true);
        sp++
        Write((0x0100u + sp), sr)
        // SetFlag(Flags.B, false);
        // SetFlag(Flags.U, false);
        return 0
    }

    private fun PLA(): Byte {
        sp++
        a = Read((0x0100u + sp))
        SetFlag(Flags.Z, a.toInt() == 0x00)
        SetFlag(Flags.N, a.toInt() and 0x80 == 1)
        return 0
    }

    private fun PLP(): Byte {
        sp++
        sr = Read((0x0100u + sp))
        SetFlag(Flags.U, true)
        return 0
    }

    private fun ROL(): Byte {
        Fetch()
        val tmp = fetched.toInt() shl 1 or GetFlag(Flags.C)
        SetFlag(Flags.Z, tmp == 0x00)
        SetFlag(Flags.N, tmp and 0x80 == 1)
        SetFlag(Flags.C, tmp and 0x0001 == 1)
        if (lookup[opCode].opcode == this::IMP) {
            a = (tmp and 0x00FF).toUByte()
        } else {
            Write(addressAbs, (tmp and 0x00FF).toUByte())
        }
        return 0
    }

    private fun ROR(): Byte {
        Fetch()
        val tmp = fetched.toInt() shr 1 or (GetFlag(Flags.C) shl 7)
        SetFlag(Flags.Z, tmp == 0x00)
        SetFlag(Flags.N, tmp and 0x80 == 1)
        SetFlag(Flags.C, tmp and 0x0001 == 1)
        if (lookup[opCode].opcode == this::IMP) {
            a = (tmp and 0x00FF).toUByte()
        } else {
            Write(addressAbs, (tmp and 0x00FF).toUByte())
        }
        return 0
    }

    private fun RTI(): Byte {
        sp++
        sr = Read((0x0100u + sp))
        sr = sr and GetFlag(Flags.B).inv().toUByte()
        sr = sr and GetFlag(Flags.U).inv().toUByte()
        sp++
        pc = Read((0x0100u + sp)).toUInt()
        sp++
        pc = pc or (Read((0x0100u + sp)).toInt() shl 8).toUInt()
        return 0
    }

    private fun RTS(): Byte {
        sp++
        pc = Read((0x0100u + sp)).toUInt()
        sp++
        pc = pc or (Read((0x0100u + sp)).toInt() shl 8).toUInt()
        sp++
        return 0
    }

    private fun SBC(): Byte {
        Fetch()
        val value = fetched.toInt() xor 0x00FF
        val tmp = a + value.toUInt() + GetFlag(Flags.C).toUInt()
        SetFlag(Flags.C, tmp > 255u)
        SetFlag(Flags.Z, (tmp and 255u).toInt() == 0)
        SetFlag(Flags.V, ((a.toInt() xor tmp.toInt()) and (value xor tmp.toInt())).inv() and 0x0080 == 1)
        SetFlag(Flags.N, (tmp and 128u).toInt() == 1)
        a = (tmp and 255u).toUByte()
        return 1
    }

    private fun SEC(): Byte {
        SetFlag(Flags.C, true)
        return 0
    }

    private fun SED(): Byte {
        SetFlag(Flags.D, true)
        return 0
    }

    private fun SEI(): Byte {
        SetFlag(Flags.I, true)
        return 0
    }

    private fun STA(): Byte {
        Write(addressAbs, a)
        return 0
    }

    private fun STX(): Byte {
        Write(addressAbs, x)
        return 0
    }

    private fun STY(): Byte {
        Write(addressAbs, y)
        return 0
    }

    private fun TAX(): Byte {
        x = a
        SetFlag(Flags.N, x.toInt() and 0x80 == 1)
        SetFlag(Flags.Z, x.toInt() == 0x00)
        return 0
    }

    private fun TAY(): Byte {
        y = a
        SetFlag(Flags.N, y.toInt() and 0x80 == 1)
        SetFlag(Flags.Z, y.toInt() == 0x00)
        return 0
    }

    private fun TSX(): Byte {
        x = sp
        SetFlag(Flags.N, x.toInt() and 0x80 == 1)
        SetFlag(Flags.Z, x.toInt() == 0x00)
        return 0
    }

    private fun TXA(): Byte {
        a = x
        SetFlag(Flags.N, a.toInt() and 0x80 == 1)
        SetFlag(Flags.Z, a.toInt() == 0x00)
        return 0
    }

    private fun TXS(): Byte {
        sr = x
        return 0
    }

    private fun TYA(): Byte {
        a = y
        SetFlag(Flags.N, a.toInt() and 0x80 == 1)
        SetFlag(Flags.Z, a.toInt() == 0x00)
        return 0
    }

    // represents all illegal instructions
    private fun XXX(): Byte {
        return 0
    }


    // connecting to bus, interacting  with it
    fun ConnectBus(bus : Bus)
    {
        this.bus = bus
    }

    private fun Read(address : UInt) : UByte
    {
        return bus.Read(address)
    }

    private fun Write(address : UInt, data : UByte)
    {
        bus.Write(address, data)
    }

    // Those are debug functions to get status of diffrent registers
    public fun Info()
    {
        println("--------------------")
        println("register A ${a}")
        println("register X ${x}")
        println("register Y ${y}")
        println("stack pointer ${sp}")
        println("stack register ${sr}")
        println("program counter ${pc}")
    }

    public fun A() : UByte
    {
        return a
    }

    public fun X() : UByte
    {
        return x
    }

    public fun Y() : UByte
    {
        return y
    }

    public fun Sp() : UByte
    {
        return sp
    }

    public fun Sr() : UByte
    {
        return sr
    }

    public fun Pc() : UInt
    {
        return pc
    }

}
