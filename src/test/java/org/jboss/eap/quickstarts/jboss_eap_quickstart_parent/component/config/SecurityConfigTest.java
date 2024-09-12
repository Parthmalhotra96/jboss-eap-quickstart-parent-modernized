package org.jboss.eap.quickstarts.jboss_eap_quickstart_parent.component.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SecurityConfigTest {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testUserDetailsService() {
        // Given
        String username = "user";
        String password = "user";

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(password, userDetails.getPassword()));    }

    @Test
    public void testPasswordEncoder() {
        // Given
        String password = "admin";
        String encodedPassword = passwordEncoder.encode(password);

        // When
        boolean matches = passwordEncoder.matches(password, encodedPassword);

        // Then
        assertTrue(matches);
    }

    @Test
    public void testUserDetailsServiceWithAuthorities() {
        // Given
        String username = "admin";
        String password = "admin";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // When
        UserDetails userDetails = new User(username, passwordEncoder.encode(password), authorities);

        // Then
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertTrue(passwordEncoder.matches(password, userDetails.getPassword()));
        assertIterableEquals(authorities, userDetails.getAuthorities());
    }
}
