package com.alibou.security.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTestTU {

    @Test
    public void testGetAuthorities() {
        Role role = Role.USER;
        User user = User.builder()
                .role(role)
                .build();
        assertEquals(role.getAuthorities(), user.getAuthorities());
    }

    @Test
    public void testIsAccountNonExpired() {
        User user = new User();
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    public void testIsAccountNonLocked() {
        User user = new User();
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    public void testIsCredentialsNonExpired() {
        User user = new User();
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    public void testIsEnabled() {
        User user = new User();
        assertTrue(user.isEnabled());
    }
}
