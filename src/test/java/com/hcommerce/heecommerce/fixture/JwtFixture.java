package com.hcommerce.heecommerce.fixture;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtFixture {

    public static final String SECRET = "12345678901234567890123456789012";

    public static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";

    public static final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";

    public static Claims CLAIMS = Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(JwtFixture.SECRET.getBytes()))
        .build()
        .parseClaimsJws(JwtFixture.TOKEN)
        .getBody();
}
