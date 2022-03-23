package com.tool.demo.tool;

import com.dengxq.lnglat2Geo.GeoTrans;
import com.dengxq.lnglat2Geo.entity.Admin;
import com.dengxq.lnglat2Geo.entity.CoordinateSystem;
import com.tool.demo.lnglat.LngLatEntry;

/**
 * @author wh
 * 通过查询经纬度，查找出地址
 */
public class FindLatLocal {

    public static void main(String[] args) {
        findLocal();
    }

    public static void findLocal(){
        LngLatEntry entry = new LngLatEntry();
        entry.setLon(120.736102);
        entry.setLat(31.296812);
        Admin aa = GeoTrans.determineAdmin(entry.getLon(), entry.getLat(), CoordinateSystem.GCJ02(), true);
        entry.setArea(aa.toNameString());
        System.out.println(entry.getArea());
    }
}
