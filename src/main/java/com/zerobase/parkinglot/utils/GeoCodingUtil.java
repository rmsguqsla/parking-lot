package com.zerobase.parkinglot.utils;

import com.zerobase.parkinglot.error.ErrorCode;
import com.zerobase.parkinglot.parkinglot.exception.ParkingLotException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeoCodingUtil {

    @Value("${geocoding.key}")
    private String key;
    private static final String STATIC_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";

    public double[] getGeoCode(String address) {
        log.info(key);
        try {
            String urlString = String.format(STATIC_URL,
                URLEncoder.encode(address, "UTF-8"),
                key
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

            return new double[]{(double) location.get("lat"), (double) location.get("lng")};

        } catch (IndexOutOfBoundsException e) {
            log.error(e.toString());
            throw new ParkingLotException(ErrorCode.INVALID_ADDRESS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
