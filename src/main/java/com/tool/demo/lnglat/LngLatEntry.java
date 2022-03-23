package com.tool.demo.lnglat;

import lombok.Data;

@Data
public class LngLatEntry {

    /**
     * 经度
     */
    public Double lon;
    /**
     * 纬度
     */
    public Double lat;

    public String area;

}
