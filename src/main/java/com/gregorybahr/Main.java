package com.gregorybahr;

import java.io.*;

/**
 * Created by greg on 2/4/2017.
 */
public class Main {
    public static void main(String[] args) {
        VirtualMachine vm = loadSave();
        while (true) {
            vm.cycle();
        }
    }

    private static int[] loadBytesFromFile() {
        int[] programBytes = new int[32768];
        byte[] buffer = new byte[2];
        BufferedInputStream inputStream = null;
        int bytesRead = 0;
        int offset = 0;

        try {
            inputStream = new BufferedInputStream(new FileInputStream("challenge.bin"));
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                programBytes[offset] = ((buffer[1]&0xFF) << 8) | (buffer[0]&0xFF); // program is little endian
                offset += 1;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return programBytes;
    }

    private static VirtualMachine loadSave() {
        VirtualMachine vm = new VirtualMachine(loadBytesFromFile());
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("saveState.bin"));
            vm = (VirtualMachine)is.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return vm;
    }
}
