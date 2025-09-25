package com.myarea.myarea.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMapService {
    private static final String KAKAO_API_KEY = ""; // REST API 키

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

                    // 도로명 주소
                    JsonNode roadAddrNode = doc.path("road_address");
                    if (roadAddrNode != null && !roadAddrNode.isNull()) {
                        String roadAddress = roadAddrNode.path("address_name").asText(null);
                        if (roadAddress != null) return roadAddress;
                    }

                    // 지번 주소
                    JsonNode jibunAddrNode = doc.path("address");
                    if (jibunAddrNode != null && !jibunAddrNode.isNull()) {
                        String jibunAddress = jibunAddrNode.path("address_name").asText(null);
                        if (jibunAddress != null) return jibunAddress;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("카카오 API 응답 파싱 실패", e);
            }
        }

        throw new RuntimeException("카카오 API 요청 실패: " + response.getStatusCode());
    }
}

//@Service
//public class KakaoMapService {
//    private static final String KAKAO_API_KEY = "a963bc2f3b3efd088d0e5e747c1a47f9"; // 반드시 REST API 키 확인
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public String getAddressFromCoords(double longitude, double latitude) {
//        String url = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x="
//                + longitude + "&y=" + latitude;
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "KakaoAK " + KAKAO_API_KEY);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<String> response = restTemplate.exchange(
//                url, HttpMethod.GET, entity, String.class
//        );
//
//        return response.getBody();
//    }
//}

