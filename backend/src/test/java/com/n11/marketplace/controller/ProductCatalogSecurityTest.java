package com.n11.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.n11.marketplace.config.SecurityConfig;
import com.n11.marketplace.security.JwtFilter;
import com.n11.marketplace.security.JwtUtil;
import com.n11.marketplace.security.UserDetailsServiceImpl;
import com.n11.marketplace.service.ProductCatalogService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductCatalogController.class)
@Import({SecurityConfig.class, JwtFilter.class})
class ProductCatalogSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductCatalogService productCatalogService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void productsShouldBePublicWithoutToken() throws Exception {
        Mockito.when(productCatalogService.getProducts(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 12), 0));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void categoriesShouldBePublicWithoutToken() throws Exception {
        Mockito.when(productCatalogService.getActiveCategories()).thenReturn(List.of());

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk());
    }
}
