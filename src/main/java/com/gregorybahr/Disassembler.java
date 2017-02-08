package com.gregorybahr;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by greg on 2/5/2017.
 */
public class Disassembler {

    private VirtualMachine vm;
    private int[] byteCode;
    private HashMap<Integer, Opcode> opcodes;
    private int offset;

    public Disassembler(VirtualMachine vm) {
        this.vm = vm;
        byteCode = vm.getMemory();
        opcodes = vm.getOpcodes();
        offset = 0;
    }

    public String decodeBin() {
        if (byteCode[offset] > 21) {
            offset++;
            return offset-1 + "| unknown " + byteCode[offset-1];
        }
        Opcode opcode = opcodes.get(byteCode[offset]);
        String name = opcode.name;
        int numParams = opcode.numParams;

        StringBuilder output = new StringBuilder(offset + "| " + name);
        for (int i = 0; i < numParams; i++) {
            output.append(getNumString(byteCode[offset + (i + 1)]));
        }
        offset += 1 + numParams;
        return output.toString();
    }

    public String decodeOpcode(int offset, int[] memory) {
        if (byteCode[offset] > 21) {
            return "";
        }
        Opcode opcode = opcodes.get(byteCode[offset]);
        String name = opcode.name;
        int numParams = opcode.numParams;

        StringBuilder output = new StringBuilder(name);

        for (int i = 0; i < numParams; i++) {
            output.append(getNumString(byteCode[offset + (i + 1)]));
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


    public static void main(String[] args) {
        VirtualMachine vm = new VirtualMachine(Main.loadBytesFromFile());
        Disassembler ds = new Disassembler(vm);
        try {
            PrintWriter pw = new PrintWriter("disassem.txt");
            while (ds.offset < 32768) {
                pw.println(ds.decodeBin());
            }
            pw.flush();
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
