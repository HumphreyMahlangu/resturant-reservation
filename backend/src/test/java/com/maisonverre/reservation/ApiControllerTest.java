package com.maisonverre.reservation;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:api-test",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMIN")
class ApiControllerTest {
    @Autowired MockMvc mockMvc;

    @Test
    void createsMenuAndOrderResources() throws Exception {
        String role = id(mockMvc.perform(post("/api/roles").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"name":"STAFF","description":"Restaurant staff"}
                                """)).andExpect(status().isCreated()).andReturn());
        String user = id(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"name":"Ava Stone","email":"ava@example.com","roleId":"%s","status":"ACTIVE"}
                                """.formatted(role))).andExpect(status().isCreated()).andReturn());
        String category = id(mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"name":"Mains","description":"Main courses"}
                                """)).andExpect(status().isCreated()).andReturn());
        String item = id(mockMvc.perform(post("/api/menu-items").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"categoryId":"%s","name":"Pasta","price":18.50,"quantityAvailable":10}
                                """.formatted(category))).andExpect(status().isCreated()).andReturn());
        String order = id(mockMvc.perform(post("/api/orders").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"userId":"%s","totalAmount":18.50}
                                """.formatted(user))).andExpect(status().isCreated()).andReturn());

        mockMvc.perform(post("/api/order-items").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"%s","menuItemId":"%s","quantity":1,"unitPrice":18.50}
                                """.formatted(order, item)))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/order-items")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"orderId":"%s","amount":18.50,"paymentMethod":"CARD"}
                                """.formatted(order)))
                .andExpect(status().isCreated());
    }

    @Test
    void createsAndUpdatesReviewAndNotification() throws Exception {
        String user = id(mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"name":"Sam Lee","email":"sam@example.com"}
                                """)).andExpect(status().isCreated()).andReturn());
        String category = id(mockMvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"name":"Desserts"}
                                """)).andExpect(status().isCreated()).andReturn());
        String item = id(mockMvc.perform(post("/api/menu-items").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"categoryId":"%s","name":"Tiramisu","price":9.00,"quantityAvailable":5}
                                """.formatted(category))).andExpect(status().isCreated()).andReturn());
        String review = id(mockMvc.perform(post("/api/reviews").contentType(MediaType.APPLICATION_JSON)
                .content("""
                                {"userId":"%s","menuItemId":"%s","rating":5,"comment":"Excellent"}
                                """.formatted(user, item))).andExpect(status().isCreated()).andReturn());
        mockMvc.perform(put("/api/reviews/" + review).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":"%s","menuItemId":"%s","rating":4,"comment":"Very good"}
                                """.formatted(user, item)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.rating").value(4));
        mockMvc.perform(post("/api/notifications").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"userId":"%s","message":"Your booking is confirmed","read":false}
                                """.formatted(user)))
                .andExpect(status().isCreated());
    }

    private static String id(MvcResult result) throws Exception {
        return JsonPath.read(result.getResponse().getContentAsString(), "$.id").toString();
    }
}

