//package com.uoit.phlarit;
//
//import android.util.Pair;
//
//import org.apache.http.client.entity.UrlEncodedFormEntityHC4;
//import org.apache.http.client.methods.HttpPostHC4;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.json.JSONObject;
//
//import java.io.InputStream;
//import java.util.List;
//import org.apache.http.impl.client.HttpClientBuilder;
//
//
//import java.io.IOException;
//
///**
// * A JSONParser is a software componenet that read the data from the
// * web server
// *
// */
//public class JSONParser {
//
//
//    static InputStream inputStream = null;
//    static JSONObject jsonObject = null;
//    static String json = "";
//
//    public JSONParser() {
//        // Do nothing
//    }
//
//    public JSONObject makeHttpRequest(String url, String method, List<Pair<String, String>> params) {
//
//        try {
//            if(method == "POST") {
//                CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//                HttpPostHC4 httpPostHC4 = new HttpPostHC4(url);
//                httpPostHC4.setEntity(new UrlEncodedFormEntityHC4(params));
//
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return jsonObject;
//    }
//
//
//}
