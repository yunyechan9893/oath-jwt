package com.example.demo.common.oauth2;

import com.example.demo.common.jwt.Token;
import com.example.demo.common.jwt.TokenComponent;
import com.example.demo.common.jwt.TokenHeader;
import com.example.demo.common.jwt.TokenPayload;
import com.example.demo.service.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenComponent tokenComponent;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

    String username = customUserDetails.getUsername();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority auth = iterator.next();
    String role = auth.getAuthority();

    TokenHeader header = new TokenHeader();
    TokenPayload payload = new TokenPayload(0, LocalDateTime.now().plusHours(1), "access_token",role);
    Token token = tokenComponent.createToken(header, payload);
    token.getPayload().getPayload().put("username", username);

    response.addCookie(createCookie("Authorization", token.getEncodingToken()));
    response.sendRedirect("http://localhost:3000/");
  }

  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(60*60*60);
    cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }
}
