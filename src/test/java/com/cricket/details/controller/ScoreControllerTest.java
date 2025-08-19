package com.cricket.details.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.cricket.details.service.ScoreService;

@WebMvcTest(ScoreController.class)
public class ScoreControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ScoreService scoreService;

    @Test
    public void addScore_ValidationFailure() throws Exception {
        mockMvc.perform(post("/scores/addScore").contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                             "runs" : "8",
                             "result" : "Win"
                             "match": "G2G"
                         }
                        """)
                .with(user("test").roles("USER")))
                .andExpect(status().isBadRequest());
    }

}
