package com.gregorybahr;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by greg on 2/4/2017.
 */
public class Main {
    public static void main(String[] args) {
        VirtualMachine vm = new VirtualMachine(loadBytesFromFile(), false);
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
}
