package com.github.backproject.service.security;

import com.github.backproject.respository.UserDetails.CustomerUserDetail;
import com.github.backproject.respository.roles.RolesEntity;
import com.github.backproject.respository.userPrincipal.UserPrincipalEntity;
import com.github.backproject.respository.userPrincipal.UserPrincipalRepository;
import com.github.backproject.respository.userPrincipalRoles.UserPrincipalRolesEntity;
import com.github.backproject.service.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Primary
@RequiredArgsConstructor
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserPrincipalRepository userPrincipalRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserPrincipalEntity userPrincipalEntity = userPrincipalRepository.findByEmailFetchJoin(email)
                .orElseThrow(() -> new NotFoundException("email에 해당하는 UersPrincipal이 없습니다"));

        CustomerUserDetail customerUserDetail = CustomerUserDetail.builder()
                .user_id(userPrincipalEntity.getUserEntity().getUserId())
                .email(userPrincipalEntity.getEmail())
                .password(userPrincipalEntity.getPassword())
                .authorities(userPrincipalEntity.getUserPrincipalRoles().stream().map(UserPrincipalRolesEntity::getRolesEntity).map(RolesEntity::getName).collect(Collectors.toList()))
                .build();

        return customerUserDetail;

    }
}
