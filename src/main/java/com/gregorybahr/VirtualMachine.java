package com.gregorybahr;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by greg on 2/4/2017.
 */
public class VirtualMachine {
    private int pc;
    private int[] memory;
    private Stack<Integer> stack;
    private int[] registers;

    private HashMap<Integer, Opcode> opcodes;

    public VirtualMachine(int[] data) {
        pc = 0;
        memory = data;
        stack = new Stack<Integer>();
        registers = new int[8];
        opcodes = new HashMap<Integer, Opcode>();

        // halt
        opcodes.put(0, new Opcode() {
            public void execute() {
                System.out.println("Halting.");
                System.exit(0);
            }
        });

        // out a
        opcodes.put(19, new Opcode() {
            public void execute() {
                System.out.print((char)interpretMem(memory[pc+1]));
                pc += 2;
            }
        });

        // noop
        opcodes.put(21, new Opcode() {
            public void execute() {
                pc += 1;
            }
        });

        // jmp
        opcodes.put(6, new Opcode() {
            public void execute() {
                System.out.println("Jumping to " + interpretMem(memory[pc+1]));

                pc = memory[pc+1];
            }
        });
    }

    public void cycle() {
        Opcode opcode = opcodes.get(interpretMem(memory[pc]));

        if(opcode != null) {
            opcode.execute();
        } else {
            System.out.println("Opcode: " + interpretMem(memory[pc]) + " is not implemented.");
            System.exit(1);
        }
    }

    public int interpretMem(int num) {
        if(num < 32776) {
            if(num > 32767) {
                return registers[num%32768];
            }
            return num;
        } else {
            throw new NumberFormatException(num + " is an invalid number.");
        }
    }
}

