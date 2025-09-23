package com.myarea.myarea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMapService {
    private static final String KAKAO_API_KEY = "a963bc2f3b3efd088d0e5e747c1a47f9"; // REST API 키

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAddressFromCoords(double longitude, double latitude) {
        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x="
                + longitude + "&y=" + latitude;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode documents = root.path("documents");

                if (documents.isArray() && documents.size() > 0) {
                    JsonNode doc = documents.get(0);

                    // 도로명 주소가 있으면 road_address.address_name 우선
                    if (doc.has("road_address") && !doc.path("road_address").isMissingNode()) {
                        return doc.path("road_address").path("address_name").asText();
                    }

                    // 없으면 지번 주소 address.address_name
                    if (doc.has("address") && !doc.path("address").isMissingNode()) {
                        return doc.path("address").path("address_name").asText();
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("카카오 API 응답 파싱 실패", e);
            }
        }

        throw new RuntimeException("카카오 API 요청 실패: " + response.getStatusCode());
    }
}

