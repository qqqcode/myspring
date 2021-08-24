package com.qqqio;

import com.qqqspring.tools.StringUtils;

import javax.sound.midi.Soundbank;
import java.io.*;

/**
 * @author Johnson
 * 2021/8/20
 */

public class QqqIOTest {

    public static void main(String[] args) {
        try {
            FileInputStream fileInputStream = new FileInputStream("D://abc.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader1 =new BufferedReader(inputStreamReader);
            String lin;
            while ((lin = bufferedReader1.readLine())!=null){
                System.out.println(lin);
            }


            FileOutputStream fileOutputStream = new FileOutputStream("D://def.txt");
            byte[] b = new byte[1024];
            while (fileInputStream.read(b,0,b.length) != -1) {
                fileOutputStream.write(b);
            }


            FileReader fileReader = new FileReader("D://abc.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine())!=null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
