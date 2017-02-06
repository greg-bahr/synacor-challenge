package com.gregorybahr;

import java.util.HashMap;

/**
 * Created by greg on 2/5/2017.
 */
public class Disassembler {

    private VirtualMachine vm;
    private int[] byteCode;
    private HashMap<Integer, Opcode> opcodes;
    private int offset;
    private int ln;

    public Disassembler(VirtualMachine vm) {
        this.vm = vm;
        byteCode = vm.getMemory();
        opcodes = vm.getOpcodes();
        offset = 0;
        ln = 0;
    }

    public String decodeOpcode() {
        if (byteCode[offset] > 21) {
            offset++;
            return "";
        }
        Opcode opcode = opcodes.get(byteCode[offset]);
        String name = opcode.name;
        int numParams = opcode.numParams;

        StringBuilder output = new StringBuilder(ln + "| " + name);

        if (name.equals("out")) {
            char c;
            output.append(" "+(char)byteCode[offset+1]);
            while (byteCode[offset+2] == 19 && byteCode[offset+1] != 10) {
                offset+=2;
                c = (char)byteCode[offset+1];
                output.append(c);
            }
            output.deleteCharAt(output.length()-1);
        } else {
            for (int i = 0; i < numParams; i++) {
                output.append(getNumString(byteCode[offset + (i + 1)]));
            }
        }
        offset += 1 + numParams;
        ln++;
        return output.toString();
    }

    public String decodeOpcode(int offset, int[] memory) {
        if (byteCode[offset] > 21) {
            return "";
        }
        Opcode opcode = opcodes.get(byteCode[offset]);
        String name = opcode.name;
        int numParams = opcode.numParams;

        StringBuilder output = new StringBuilder(ln + "| " + name);

        if (name.equals("out")) {
            char c;
            output.append(" "+(char)byteCode[offset+1]);
            while (byteCode[offset+2] == 19 && byteCode[offset+1] != 10) {
                offset+=2;
                c = (char)byteCode[offset+1];
                output.append(c);
            }
            output.deleteCharAt(output.length()-1);
        } else {
            for (int i = 0; i < numParams; i++) {
                output.append(getNumString(byteCode[offset + (i + 1)]));
            }
        }
        return output.toString();
    }

    public boolean isRegister(int num) {
        return num >= 32768 && num <= 32775;
    }

    public String getNumString(int num) {
        if(isRegister(num)) {
            return String.format(" (r%d)", num%32768);
        } else {
            return " "+Integer.toString(num);
        }
    }
}
