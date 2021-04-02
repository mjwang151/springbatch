package com.service;

import com.alibaba.fastjson.JSONObject;
import com.bean.QueryDistributedBean;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLDecoder;

@Component
public class BaiduSearchIp {
    Logger logger = LoggerFactory.getLogger(BaiduSearchIp.class);

    public static void main(String[] args) {
        BaiduSearchIp hc = new BaiduSearchIp();
        hc.getData("218.4.93.234");
    }

    String AccessKey = "00345c5296dc42d293d27e8de1bccd10";
    String AppSecret = "f30524fc3bca423d8ccbd59445353212";
    String AppCode = "0d259abacd634dbb8f29f374aff570b1";
    String URL = "http://ddm-ip.api.bdymkt.com";//生产环境


    public QueryDistributedBean getData(String ip) {
        String param = "?ip=" + ip;
        String srtResult = "";
        try {
            CloseableHttpClient httpCilent = AmarHttpClientPool.getHttpClient();
            HttpGet httpGet = new HttpGet(URL + param);
            httpGet.setHeader("X-Bce-Stage", "release");
            httpGet.setHeader("Content-Type", "application/json; charset=utf-8");
            httpGet.setHeader("Host", "ddm-ip.api.bdymkt.com");
            httpGet.setHeader("X-Bce-Signature", "AppCode/" + AppCode);
            httpGet.setHeader("Provider", "no");
            HttpResponse httpResponse = httpCilent.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                srtResult = EntityUtils.toString(httpResponse.getEntity());//获得返回的结果
                logger.info("上游返回报文：" + srtResult);
                JSONObject jo = JSONObject.parseObject(srtResult);
                JSONObject details = null;
                try {
                    details = jo.getJSONObject("data").getJSONObject("details");
                } catch (Exception e) {
                    try {
                        details = jo.getJSONObject("data").getJSONArray("details").getJSONObject(0);
                    } catch (Exception e1) {
                        logger.error("报文无法解析");
                        return new QueryDistributedBean();
                    }
                }

                String city = URLDecoder.decode(StringUtils.isNotBlank(details.getString("city")) ? details.getString("city") : "");
                String region = URLDecoder.decode(StringUtils.isNotBlank(details.getString("region")) ? details.getString("region") : "");
                QueryDistributedBean queryDistributedBean = new QueryDistributedBean();
                queryDistributedBean.setCity(city);
                queryDistributedBean.setProvince(region);
                return queryDistributedBean;
            } else if (httpResponse.getStatusLine().getStatusCode() == 400) {
                System.out.println("错误请求！");
            } else if (httpResponse.getStatusLine().getStatusCode() == 500) {
                System.out.println("服务器内部错误");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new QueryDistributedBean();
    }


}
