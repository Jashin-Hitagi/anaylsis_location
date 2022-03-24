package com.tool.demo.tool;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {

    private String country;

    private String province;

    private String city;

    /**
     * 区
     */
    private String district;

    private String street;

}
