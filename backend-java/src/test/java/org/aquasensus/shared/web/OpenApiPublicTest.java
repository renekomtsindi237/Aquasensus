package org.aquasensus.shared.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiPublicTest {

    @Autowired
    MockMvc mvc;

    @Test
    void contratV1DocumenteSansApiInterne() throws Exception {
        mvc.perform(get("/api/docs/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").value(org.hamcrest.Matchers.startsWith("3.")))
                .andExpect(jsonPath("$.paths['/api/v1/water-points/map']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/dashboard/kpi']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/simulation/sms/inbound']").exists())
                .andExpect(jsonPath("$.paths['/internal/analytics/dataset']").doesNotExist());
    }
}
