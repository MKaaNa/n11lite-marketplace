package com.n11.marketplace.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.config.SecurityConfig;
import com.n11.marketplace.exception.GlobalExceptionHandler;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.security.JwtUtil;
import com.n11.marketplace.security.UserDetailsServiceImpl;
import com.n11.marketplace.service.AdminOrderService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminOrderController.class)
@Import({SecurityConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class AdminSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminOrderService adminOrderService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void listOrdersUnauthenticatedReturns401() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void listOrdersWithUserRoleReturns403() throws Exception {
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listOrdersWithAdminRoleReturns200() throws Exception {
        when(adminOrderService.listAllOrders()).thenReturn(List.of());

        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk());
    }
}
