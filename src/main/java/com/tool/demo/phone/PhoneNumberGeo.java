package com.tool.demo.phone;


import com.tool.demo.tool.AreaList;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * PhoneNumberGeo
 *
 * @author rebie
 * @since 2020/12/10.
 */
@Service
public class PhoneNumberGeo {

    private static final String[] PHONE_NUMBER_TYPE = {null, "移动", "联通", "电信", "电信虚拟运营商", "联通虚拟运营商", "移动虚拟运营商"};
    private static final int INDEX_SEGMENT_LENGTH = 9;
    private static final int DATA_FILE_LENGTH_HINT = 3747505;
    private static final String PHONE_DAT_FILE_PATH = "phone.dat";
    private static final String STATIC_PHONE_DAT_FILE_PATH = "static-phone.dat";
    private static final String COUNTRY_PHONE_DAT_FILE_PATH = "country-phone.dat";

    private static byte[] dataByteArray;
    private static Map<String, Info> staticData;
    private static Map<String, Info> countryData;
    private static int indexAreaOffset;
    private static int phoneRecordCount;

    private final ByteBuffer byteBuffer;

    public PhoneNumberGeo() {
        initData();
        byteBuffer = ByteBuffer.wrap(dataByteArray);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    private static synchronized void initData() {
        if (dataByteArray == null) {
            ByteArrayOutputStream byteData = new ByteArrayOutputStream(DATA_FILE_LENGTH_HINT);
            byte[] buffer = new byte[1024];

            int readBytesLength;
            try (InputStream inputStream = PhoneNumberGeo.class.getClassLoader().getResourceAsStream(PHONE_DAT_FILE_PATH)) {
                while ((readBytesLength = inputStream.read(buffer)) != -1) {
                    byteData.write(buffer, 0, readBytesLength);
                }
            } catch (Exception e) {
                System.err.println("Can't find phone.dat in classpath: " + PHONE_DAT_FILE_PATH);
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            dataByteArray = byteData.toByteArray();

            ByteBuffer byteBuffer = ByteBuffer.wrap(dataByteArray);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            int dataVersion = byteBuffer.getInt();
            indexAreaOffset = byteBuffer.getInt();
            phoneRecordCount = (dataByteArray.length - indexAreaOffset) / INDEX_SEGMENT_LENGTH;
        }
        staticData = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(PhoneNumberGeo.class.getClassLoader().getResourceAsStream(STATIC_PHONE_DAT_FILE_PATH))));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNoneBlank(line)) {
                    String[] pc = line.split("\t");
                    Info info = new Info();
                    info.setProvince(pc[0]);
                    info.setCity(pc[1]);
                    info.setCountry("中国");
                    staticData.put(pc[2], info);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        countryData = new HashMap<>();
        BufferedReader countryReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(PhoneNumberGeo.class.getClassLoader().getResourceAsStream(COUNTRY_PHONE_DAT_FILE_PATH))));
        try {
            while ((line = countryReader.readLine()) != null) {
                if (StringUtils.isNoneBlank(line)) {
                    String[] pc = line.split("\t");
                    Info info = new Info();
                    info.setCountry(pc[0]);
                    countryData.put(pc[1], info);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PhoneNumberGeo phoneNumberGeo = new PhoneNumberGeo();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            System.out.println(phoneNumberGeo.lookup(scanner.next()));
        }
    }

    public Info lookup(String phoneNumber) {

        String phoneNum = phoneNumber;

        if (StringUtils.isBlank(phoneNum)) {
            return null;
        }

        if (phoneNum.startsWith("+86")) {
            phoneNum = phoneNum.substring(3);
            phoneNum = phoneNum.replaceAll("\\s|-", "");
        }else if (phoneNum.startsWith("86")) {
            phoneNum = phoneNum.substring(2);
            phoneNum = phoneNum.replaceAll("\\s|-", "");
        }else if (phoneNum.startsWith("0")) {
            Optional<String> st = staticData.keySet().stream().filter(phoneNum::startsWith).findFirst();
            if (st.isPresent()) {
                Info info = BeanCopyUtil.copyProperties(staticData.get(st.get()), Info.class);
                info.setPhoneNumber(phoneNumber);
                info.setPhoneType("固定电话");
                return info;
            } else {
                return null;
            }
        }else {
            phoneNum = phoneNum.replace("+", "").trim();
            Optional<String> st = countryData.keySet().stream().filter(phoneNum::startsWith).findFirst();
            if (st.isPresent()) {
                Info info = BeanCopyUtil.copyProperties(countryData.get(st.get()), Info.class);
                info.setPhoneNumber(phoneNumber);
                info.setPhoneType("国际电话");
                return info;
            } else {
                return null;
            }
        }


        if (phoneNum.length() > 11 || phoneNum.length() < 7) {
            return null;
        }
        int phoneNumberPrefix;
        try {
            phoneNumberPrefix = Integer.parseInt(phoneNum.substring(0, 7));
        } catch (Exception e) {
            return null;
        }
        int left = 0;
        int right = phoneRecordCount;
        while (left <= right) {
            int middle = (left + right) >> 1;
            int currentOffset = indexAreaOffset + middle * INDEX_SEGMENT_LENGTH;
            if (currentOffset >= dataByteArray.length) {
                return null;
            }

            byteBuffer.position(currentOffset);
            int currentPrefix = byteBuffer.getInt();
            if (currentPrefix > phoneNumberPrefix) {
                right = middle - 1;
            } else if (currentPrefix < phoneNumberPrefix) {
                left = middle + 1;
            } else {
                int infoBeginOffset = byteBuffer.getInt();
                int phoneType = byteBuffer.get();

                int infoLength = -1;
                for (int i = infoBeginOffset; i < indexAreaOffset; ++i) {
                    if (dataByteArray[i] == 0) {
                        infoLength = i - infoBeginOffset;
                        break;
                    }
                }

                String infoString = new String(dataByteArray, infoBeginOffset, infoLength, StandardCharsets.UTF_8);
                String[] infoSegments = infoString.split("\\|");

                Info phoneNumberInfo = new Info();
                phoneNumberInfo.setCountry("");
                if (AreaList.CHINESE_PROVINCE_LIST.contains(infoSegments[0])){
                    phoneNumberInfo.setCountry("中国");
                }
                phoneNumberInfo.setPhoneNumber(phoneNumber);
                phoneNumberInfo.setProvince(infoSegments[0]);
                phoneNumberInfo.setCity(infoSegments[1]);
                phoneNumberInfo.setZipCode(infoSegments[2]);
                phoneNumberInfo.setAreaCode(infoSegments[3]);
                phoneNumberInfo.setPhoneType(PHONE_NUMBER_TYPE[phoneType]);
                return phoneNumberInfo;
            }
        }
        return null;
    }

    @Data
    @ToString
    public static class Info {
        private String phoneNumber;
        private String province;
        private String city;
        private String zipCode;
        private String areaCode;
        private String phoneType;
        private String country;
    }
}