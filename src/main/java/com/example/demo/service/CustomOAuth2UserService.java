package com.example.demo.service;

import com.example.demo.domain.MemberEntity;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final MemberRepository memberRepository;

  @Override
  @Transactional
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    OAuth2User oAuth2User = super.loadUser(userRequest);
    System.out.println(oAuth2User);

    String registrationId = userRequest.getClientRegistration().getRegistrationId();
    OAuth2Response oAuth2Response;
    if (registrationId.equals("naver")) {
      oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
    } else if (registrationId.equals("google")) {
      oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
    } else {
      return null;
    }

    String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();
    MemberEntity existingMember = memberRepository.findByUsername(username);

    if (Objects.isNull(existingMember)) {
      MemberEntity memberEntity = MemberEntity.create(
          username,
          oAuth2Response.getName(),
          oAuth2Response.getEmail(),
          "ROLE_USER"
      );

      memberRepository.save(memberEntity);

      UserDto userDto = new UserDto();
      userDto.setUsername(username);
      userDto.setName(oAuth2Response.getName());
      userDto.setRole("ROLE_USER");

      return new CustomOAuth2User(userDto);
    } else {
      existingMember.updateInfo(oAuth2Response.getName(), oAuth2Response.getEmail());

      UserDto userDto = new UserDto();
      userDto.setUsername(existingMember.getUsername());
      userDto.setName(existingMember.getName());
      userDto.setRole(existingMember.getRole());

      return new CustomOAuth2User(userDto);
    }


  }
}
