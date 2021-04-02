package com.bean;

/**
 * @author mjwang
 */
public class QueryDistributedBean {

    private String datadate;
    private String province;
    private String province_shortName;

    private String city;
    private long querycount;
    private String reqAddr;


    public String getDatadate() {
        return datadate;
    }

    public void setDatadate(String datadate) {
        this.datadate = datadate;
    }

    public String getProvince_shortName() {
        return province_shortName;
    }

    public void setProvince_shortName(String province_shortName) {
        this.province_shortName = province_shortName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public long getQuerycount() {
        return querycount;
    }

    public void setQuerycount(long querycount) {
        this.querycount = querycount;
    }

    public String getReqAddr() {
        return reqAddr;
    }

    public void setReqAddr(String reqAddr) {
        this.reqAddr = reqAddr;
    }
}
