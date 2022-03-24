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
        System.out.println(findLocalToString(120.736102, 31.296812));;
    }

    public static Address findLocal(){
        LngLatEntry entry = new LngLatEntry();
        entry.setLon(120.736102);
        entry.setLat(31.296812);
        Admin aa = GeoTrans.determineAdmin(entry.getLon(), entry.getLat(), CoordinateSystem.GCJ02(), true);
        entry.setArea(aa.toNameString());
        return Address.builder()
                .country(aa.country())
                .province(aa.province())
                .city(aa.city())
                .district(aa.district())
                .street(aa.town())
                .build();
    }

    public static String findLocalToString(Double lon, Double lat){
        LngLatEntry entry = new LngLatEntry();
        entry.setLon(lon);
        entry.setLat(lat);
        Admin aa = GeoTrans.determineAdmin(entry.getLon(), entry.getLat(), CoordinateSystem.GCJ02(), true);
        return aa.toNameString();
    }
}
