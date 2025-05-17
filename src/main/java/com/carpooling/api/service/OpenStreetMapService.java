package com.carpooling.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class OpenStreetMapService {
    private static final Logger logger = LoggerFactory.getLogger(OpenStreetMapService.class);
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String OSRM_URL = "http://router.project-osrm.org/route/v1/driving";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Double> getCoordinates(String address) {
        try {
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&format=json&limit=1", NOMINATIM_URL, encodedAddress);

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                request.addHeader("User-Agent", "CarpoolingApp/1.0");

                String response = EntityUtils.toString(client.execute(request).getEntity());
                JsonNode results = objectMapper.readTree(response);

                if (results.isArray() && results.size() > 0) {
                    Map<String, Double> coordinates = new HashMap<>();
                    coordinates.put("lat", results.get(0).get("lat").asDouble());
                    coordinates.put("lon", results.get(0).get("lon").asDouble());
                    return coordinates;
                }
            }
        } catch (Exception e) {
            logger.error("Error getting coordinates for address: " + address, e);
        }
        return null;
    }

    public Map<String, Object> calculateRoute(String fromAddress, String toAddress) {
        try {
            Map<String, Double> fromCoords = getCoordinates(fromAddress);
            Map<String, Double> toCoords = getCoordinates(toAddress);

            if (fromCoords == null || toCoords == null) {
                return null;
            }

            String url = String.format("%s/%f,%f;%f,%f",
                OSRM_URL,
                fromCoords.get("lon"), fromCoords.get("lat"),
                toCoords.get("lon"), toCoords.get("lat")
            );

            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet request = new HttpGet(url);
                String response = EntityUtils.toString(client.execute(request).getEntity());
                JsonNode result = objectMapper.readTree(response);

                if (result.has("routes") && result.get("routes").size() > 0) {
                    JsonNode route = result.get("routes").get(0);
                    Map<String, Object> routeInfo = new HashMap<>();
                    routeInfo.put("distance", route.get("distance").asDouble() / 1000); // Convert to km
                    routeInfo.put("duration", route.get("duration").asDouble() / 60); // Convert to minutes
                    return routeInfo;
                }
            }
        } catch (Exception e) {
            logger.error("Error calculating route", e);
        }
        return null;
    }
} 