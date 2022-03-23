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
        findLocal(seeker);
    }

   public static void findLocal(IPSeeker seeker){
       Ip4Entry local = seeker.getLocal("182.92.240.48");
       System.out.println(local.getCountry() + "\t" + local.getArea());
   }
}
