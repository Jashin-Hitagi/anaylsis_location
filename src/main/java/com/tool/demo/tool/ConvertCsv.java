package com.tool.demo.tool;

import com.tool.demo.ip.IPSeeker;
import com.tool.demo.phone.PhoneNumberGeo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ConvertCsv {

    public static void convert(String[] args){
        String type = args[0];
        String config = args[1];
        String input = args[2];
        String output = args[3];
        IPSeeker seeker = IPSeeker.getInstance();
        PhoneNumberGeo phoneNumberGeo = new PhoneNumberGeo();
        File inputFile = new File(input);
        if (!inputFile.exists()){
            log.error("CSV文件不存在");
            return;
        }
        String line = "";
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            FileOutputStream outFos = createFos(output);
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if ("0".equals(type)){
                    outFos.write((data[0] + "," + FindIpLocal.findLocalToString(seeker,data[0]) + "\n").getBytes());
                }else if ("1".equals(type)){
                    List<String> headList = new ArrayList<>();
                    String line2 = "";
                    try (BufferedReader br2 = new BufferedReader(new FileReader(config))) {
                        while ((line2 = br2.readLine()) != null) {
                            headList = Arrays.asList(line2.split(",").clone());
                        }
                    }
                    final String[] phoneNum = {data[0]};
                    headList.forEach(head->{
                        if (phoneNum[0].startsWith(head)){
                            phoneNum[0] = phoneNum[0].trim().substring(head.length()).replaceAll("\\s|-", "");
                        }
                    });
                    outFos.write((phoneNum[0] + "," + FindPhoneLocal.phoneNumLookUp(phoneNumberGeo,data[0]) + "\n").getBytes());
                }
            }
            outFos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static FileOutputStream createFos(String filePath){
        deleteFile(filePath);
        return  new FileOutputStream(filePath, true);
    }

    public static void deleteFile(String filePath){
        File outFile = new File(filePath);
        if (outFile.exists()) {
            outFile.delete();
        }
    }
}
