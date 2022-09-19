package com.zerobase.parkinglot.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import lombok.experimental.UtilityClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@UtilityClass
public class GeoCodingUtil {

//    @Value("${google.geocoding.key}")
    private static final String KEY = "AIzaSyB5J_0k9aKuHEIjTJR7fYwoPx0kSU09lNQ";
    private static final String STATIC_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";

    public static double[] getGeoCode(String address) {
        try {
            String urlString = String.format(STATIC_URL,
                URLEncoder.encode(address, "UTF-8"),
                KEY
            );

            URL url = new URL(urlString);

            StringBuilder sb = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

            while(br.ready()) {
                sb.append(br.readLine().trim());
            }

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject)parser.parse(sb.toString());
            JSONArray results = (JSONArray) object.get("results");
            JSONObject results0 = (JSONObject) results.get(0);
            JSONObject geometry = (JSONObject) results0.get("geometry");
            JSONObject location = (JSONObject) geometry.get("location");

            System.out.println((double) location.get("lat")+" "+(double) location.get("lng"));
            return new double[]{(double) location.get("lat"), (double) location.get("lng")};

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
