package com.tool.demo.tool;

import com.tool.demo.ip.IPSeeker;
import com.tool.demo.ip.Ip4Entry;

/**
 * @author wh
 * 通过查询IP地址，查找出地址
 */
public class FindIpLocal {

    public static void main(String[] args) {
        IPSeeker seeker = IPSeeker.getInstance();
        System.out.println(findLocalToString(seeker,"8.8.8.8"));
    }

   public static void findLocal(IPSeeker seeker){
       Ip4Entry local = seeker.getLocal("182.92.240.48");

       System.out.println(local.getCountry() + "\t" + local.getArea());
   }

    public static String findLocalToString(IPSeeker seeker, String ip){
        Ip4Entry local = seeker.getLocal(ip);
        if (AreaList.CHINESE_PROVINCE_LIST.contains(local.getCountry())){
            return "中国" + local.getCountry() + local.getArea();
        }else {
            return local.getCountry() + local.getArea();
        }
    }
}
