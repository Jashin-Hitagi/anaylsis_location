package com.tool.demo.tool;

import com.tool.demo.phone.PhoneNumberGeo;

/**
 * @author wh
 * 通过查询手机号，查找归属地
 */
public class FindPhoneLocal {

    public static void main(String[] args) {
        PhoneNumberGeo phoneNumberGeo = new PhoneNumberGeo();
        System.out.println(phoneNumLookUp(phoneNumberGeo,"+8613000311234"));
    }

    public static String phoneNumLookUp(PhoneNumberGeo phoneNumberGeo, String number) {
        try {
            PhoneNumberGeo.Info info = phoneNumberGeo.lookup(number);
            if (info == null) {
                return null;
            }
            return String.format("%s%s%s", info.getCountry()==null?"":info.getCountry(),
                    info.getProvince()==null?"":info.getProvince(),
                    info.getCity()==null?"":info.getCity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
