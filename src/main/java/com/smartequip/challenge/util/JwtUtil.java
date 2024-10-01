package com.smartequip.challenge.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String SECRET_KEY;
  @Value("${jwt.expirationTime}")
  private long EXPIRATION_TIME;

  public String generateToken(String question) {
      return Jwts.builder()
          .subject(question)
          .claim("question", question)
          .issuedAt(new Date(System.currentTimeMillis()))
          .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
          .signWith(getSignInKey())
          .compact();
  }

  public String validateTokenAndGetQuestion(String token) {
    try {
      Jws<Claims> parsed = Jwts.parser()
          .verifyWith(getSignInKey())
          .build()
          .parseSignedClaims(token);

      return (String) parsed.getPayload().get("question");
    } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException |
             SignatureException | IllegalArgumentException | NullPointerException e) {
      throw new io.jsonwebtoken.security.SecurityException("Invalid JWT token", e);
    }
  }

  private SecretKey getSignInKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
  }
}
