package com.gregorybahr;

import java.io.IOException;
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
    private Disassembler ds;
    private boolean disassemble;

    private HashMap<Integer, Opcode> opcodes;

    public VirtualMachine(int[] data, boolean disassemble) {
        pc = 0;
        memory = data;
        stack = new Stack<Integer>();
        registers = new int[8];
        opcodes = new HashMap<Integer, Opcode>();

        // halt
        opcodes.put(0, new Opcode("halt", 0) {
            @Override
            public void execute() {
                System.out.println("Halting.");
                System.exit(0);
            }
        });

        // set a b
        opcodes.put(1, new Opcode("set", 2) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                registers[a] = b;
                pc += 3;
            }
        });

        // push a
        opcodes.put(2, new Opcode("push", 1) {
            @Override
            public void execute() {
                stack.push(interpretMem(memory[pc+1]));
                pc += 2;
            }
        });

        // pop a
        opcodes.put(3, new Opcode("pop", 1) {
            @Override
            public void execute() {
                if(stack.empty()) {
                    throw new IllegalStateException("Attempted to pop empty stack.");
                } else {
                    registers[getRegisterIndex(memory[pc+1])] = stack.pop();
                    pc += 2;
                }
            }
        });

        // eq a b c
        opcodes.put(4, new Opcode("eq", 3) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int c = interpretMem(memory[pc+3]);
                registers[a] = b == c ? 1 : 0;
                pc += 4;
            }
        });

        // gt a b c
        opcodes.put(5, new Opcode("gt", 3) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int c = interpretMem(memory[pc+3]);
                registers[a] = b > c ? 1 : 0;
                pc += 4;
            }
        });

        // jmp a
        opcodes.put(6, new Opcode("jmp", 1) {
            @Override
            public void execute() {
                //System.out.println("Jumping to " + interpretMem(memory[pc+1]) + " which contains " + memory[interpretMem(memory[pc+1])]);
                pc = memory[pc+1];
            }
        });

        // jt a b
        opcodes.put(7, new Opcode("jt", 2) {
            @Override
            public void execute() {
                int a = interpretMem(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);

                if (a != 0) {
                    pc = b;
                    //System.out.println("Jumping to " + b + " which contains " + memory[b]);
                } else {
                    pc += 3;
                }
            }
        });

        // jf a b
        opcodes.put(8, new Opcode("jf", 2) {
            @Override
            public void execute() {
                int a = interpretMem(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);

                if (a == 0) {
                    pc = b;
                    //System.out.println("Jumping to " + b + " which contains " + memory[b]);
                } else {
                    pc += 3;
                }
            }
        });

        // add a b c
        opcodes.put(9, new Opcode("add", 3) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int c = interpretMem(memory[pc+3]);

                registers[a] = (b+c)%32768;

                pc += 4;
            }
        });

        // mult a b c
        opcodes.put(10, new Opcode("mult", 3) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int c = interpretMem(memory[pc+3]);

                registers[a] = (b*c)%32768;

                pc += 4;
            }
        });

        // mod a b c
        opcodes.put(11, new Opcode("mod", 3) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int c = interpretMem(memory[pc+3]);

                registers[a] = b%c;

                pc += 4;
            }
        });

        // and a b c
        opcodes.put(12, new Opcode("and", 3) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int c = interpretMem(memory[pc+3]);
                registers[a] = b&c;
                pc += 4;
            }
        });

        // or a b c
        opcodes.put(13, new Opcode("or", 3) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int c = interpretMem(memory[pc+3]);
                registers[a] = b|c;
                pc += 4;
            }
        });

        // not a b
        opcodes.put(14, new Opcode("not", 2) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                registers[a] = (~b)&((1<<15)-1);
                pc += 3;
            }
        });

        // rmem a b
        opcodes.put(15, new Opcode("rmem", 2) {
            @Override
            public void execute() {
                int a = getRegisterIndex(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);
                int num = memory[b];
                registers[a] = num;
                pc += 3;
            }
        });

        // wmem a b
        opcodes.put(16, new Opcode("wmem", 2) {
            @Override
            public void execute() {
                int a = interpretMem(memory[pc+1]);
                int b = interpretMem(memory[pc+2]);

                memory[a] = b;

                pc += 3;
            }
        });

        // call a
        opcodes.put(17, new Opcode("call", 1) {
            @Override
            public void execute() {
                System.out.println(ds.decodeOpcode(pc, memory));
                int a = interpretMem(memory[pc+1]);
                int b = pc+2;

                stack.push(b);
                pc = a;
            }
        });

        // ret
        opcodes.put(18, new Opcode("ret", 0) {
            @Override
            public void execute() {
                if (stack.empty()) {
                    opcodes.get(0).execute(); // halt
                } else {
                    pc = stack.pop();
                }
            }
        });

        // out a
        opcodes.put(19, new Opcode("out", 1) {
            @Override
            public void execute() {
                System.out.print((char)interpretMem(memory[pc+1]));
                pc += 2;
            }
        });

        // in a
        opcodes.put(20, new Opcode("in", 1) {
            @Override
            public void execute() {
                try {
                    char c = (char) System.in.read();
                    int a = getRegisterIndex(memory[pc+1]);
                    registers[a] = (int)c;
                    pc += 2;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // noop
        opcodes.put(21, new Opcode("noop", 0) {
            @Override
            public void execute() {
                pc += 1;
            }
        });

        ds = new Disassembler(this);
        this.disassemble = disassemble;
    }

    public void cycle() {
        Opcode opcode = opcodes.get(interpretMem(memory[pc]));
        if(opcode != null) {
            if(disassemble) {
                System.out.println(ds.decodeOpcode(pc, memory));
            }
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

    public int getRegisterIndex(int num) {
        if (num < 32768 || num > 32775) {
            throw new IllegalArgumentException("Not a valid register.");
        }
        return num%32768;
    }

    public int[] getMemory() {
        return memory;
    }

    public HashMap<Integer, Opcode> getOpcodes() {
        return opcodes;
    }
}

