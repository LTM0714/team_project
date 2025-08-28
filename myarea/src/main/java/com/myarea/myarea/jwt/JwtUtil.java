package com.myarea.myarea.jwt;

import com.myarea.myarea.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // JWT 서명을 위한 비밀키(32bytes 이상), HMAC-SHA256 알고리즘을 위한 키 생성
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "mySuperSecretKeyThatIsAtLeast32BytesLong!".getBytes());  // 32바이트 이상

    private final long accessTokenExpiration = 1000 * 60 * 30;
    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24 * 7;

    // 엑세스 토큰 생성, API 요청에 Authorization 헤더로 사용할 토큰
    public String generateAccessToken(String email, UserRole role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성, 재로그인 없이 엑세스 토큰을 재발급하기 위해 사용
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT subject(이메일) 값을 파싱하여 반환
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // role 값을 파싱하여 반환
    public String getUserRoleFromToken(String token){
        return (String) getAllClaims(token).get("role");
    }

    // 토큰 파싱 가능한지 만료되지 않았는지 확인
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (io.jsonwebtoken.SignatureException e) {
            System.out.println("JWT signature invalid: " + e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.out.println("JWT malformed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT invalid: " + e.getMessage());
        }
        return false;
    }

    // 토큰 안 모든 Claims(Payload 정보들)를 반환
    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build()
                .parseClaimsJws(token).getBody();
    }
}
