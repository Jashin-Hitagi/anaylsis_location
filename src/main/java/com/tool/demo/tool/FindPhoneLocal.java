package com.tool.demo.tool;

import com.tool.demo.phone.PhoneNumberGeo;

/**
 * @author wh
 * 通过查询手机号，查找归属地
 */
public class FindPhoneLocal {

    public static void main(String[] args) {
        PhoneNumberGeo phoneNumberGeo = new PhoneNumberGeo();
        System.out.println(phoneNumLookUp(phoneNumberGeo,"+8616761732244"));
    }

    public static String phoneNumLookUp(PhoneNumberGeo phoneNumberGeo, String number) {
        try {
            PhoneNumberGeo.Info info = phoneNumberGeo.lookup(number);
            if (info == null) {
                return null;
            }
            return String.format("%s%s", info.getProvince(), info.getCity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
