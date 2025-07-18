package com.example.demo.common.security.filter;

import com.example.demo.common.jwt.Token;
import com.example.demo.common.jwt.TokenComponent;
import com.example.demo.service.dto.CustomOAuth2User;
import com.example.demo.service.dto.UserDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final TokenComponent tokenComponent;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
    String authorization = null;
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("Authorization")) {

        authorization = cookie.getValue();
      }
    }

    //Authorization 헤더 검증
    if (authorization == null) {
      filterChain.doFilter(request, response);

      //조건이 해당되면 메소드 종료 (필수)
      return;
    }

    //토큰
    String token = authorization;

    Token token1 = tokenComponent.decodeToken(token);
    //토큰 소멸 시간 검증
    if (token1.getPayload().isExpirePeriod()) {

      System.out.println("token expired");
      filterChain.doFilter(request, response);

      //조건이 해당되면 메소드 종료 (필수)
      return;
    }

    //토큰에서 username과 role 획득
    String username = (String) token1.getPayload().getPayload().getOrDefault("username", "");
    String role = token1.getPayload().getRole();

    //userDTO를 생성하여 값 set
    UserDto userDto = new UserDto();
    userDto.setUsername(username);
    userDto.setRole(role);

    //UserDetails에 회원 정보 객체 담기
    CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto);

    //스프링 시큐리티 인증 토큰 생성
    Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
    //세션에 사용자 등록
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
