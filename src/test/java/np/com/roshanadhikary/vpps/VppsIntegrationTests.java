package np.com.roshanadhikary.vpps;

import com.fasterxml.jackson.databind.ObjectMapper;
import np.com.roshanadhikary.vpps.bootstrap.H2Bootstrap;
import np.com.roshanadhikary.vpps.entity.Battery;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class VppsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnHelloMessage() throws Exception {
        mockMvc
                .perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString("Hello World!")));
    }

    @Test
    // order specified to prevent a later save operation from overriding DB and
    // causing the assertions of name, postcode, and capacity to not make sense
    @Order(1)
    void shouldFetchBatteryID1() throws Exception {
        mockMvc
                .perform(get("/batteries/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(H2Bootstrap.mockBatteries.get(0).getName()))
                .andExpect(jsonPath("$.postcode").value(H2Bootstrap.mockBatteries.get(0).getPostcode()))
                .andExpect(jsonPath("$.capacity").value(H2Bootstrap.mockBatteries.get(0).getCapacity()));
    }

    @Test
    @Order(2)
    void shouldReturnAllBatteries() throws Exception {
        mockMvc
                .perform(get("/batteries/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                // by the time of execution of this method, H2Bootstrap.mockBatteries are
                // the only ones persisted in DB, so assert against its size
                .andExpect(jsonPath("$", Matchers.hasSize(H2Bootstrap.mockBatteries.size())));
    }

    @Test
    @Order(3)
    void shouldReturnBatteriesWithinRange() throws Exception {
        String start = "0010";
        String end = "1070";

        List<Battery> resultBatteries = H2Bootstrap
                .mockBatteries
                .stream()
                // only retain batteries with postcode inside the given range [start, end]
                .filter(b -> Integer.parseInt(b.getPostcode()) >= Integer.parseInt(start) && Integer.parseInt(b.getPostcode()) <= Integer.parseInt(end))
                .collect(Collectors.toList());

        mockMvc
                .perform(get(String.format("/batteries/postcode?start=%s&end=%s", start, end)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCapacity").isNumber())
                .andExpect(jsonPath("$.totalCapacity").value(resultBatteries.stream().mapToLong(b -> b.getCapacity()).sum()))
                .andExpect(jsonPath("$.avgCapacity").isNumber())
                .andExpect(jsonPath("$.avgCapacity").value(resultBatteries.stream().mapToDouble(b -> b.getCapacity()).average().orElse(0D)))
                .andExpect(jsonPath("$.batteries").isArray());
    }

    @Test
    void shouldPersistAllBatteries() throws Exception {
        List<Battery> mockBatteries = List.of(
            new Battery("Cannington", "6107", 13500),
            new Battery("Midland", "6057", 50500),
            new Battery("Hay Street", "6000", 23500),
            new Battery("Mount Adams", "6525", 12000),
            new Battery("Koolan Island", "6733", 10000),
            new Battery("Armadale", "6992", 25000),
            new Battery("Lesmurdie", "6076", 13500),
            new Battery("Kalamunda", "6076", 13500),
            new Battery("Carmel", "6076", 36000),
            new Battery("Bentley", "6102", 85000),
            new Battery("Akunda Bay", "2084", 13500),
            new Battery("Werrington County", "2747", 13500),
            new Battery("Bagot", "0820", 27000),
            new Battery("Yirrkala", "0880", 13500),
            new Battery("University of Melbourne", "3010", 85000),
            new Battery("Norfolk Island", "2899", 13500),
            new Battery("Ootha", "2875", 13500),
            new Battery("Kent Town", "5067", 13500),
            new Battery("Northgate Mc", "9464", 13500),
            new Battery("Gold Coast Mc", "9729", 50000)
        );

        mockMvc
                .perform(
                        post("/batteries/")
                                .content(objectMapper.writeValueAsString(mockBatteries))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", Matchers.hasSize(mockBatteries.size())));
    }

    // negative tests

    @Test
    void shouldReturn404ForNonExistentBatteryID() throws Exception {
        int id = 102;
        mockMvc
                .perform(get("/batteries/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidRange() throws Exception {
        String start = "abcd";
        String end = "#123";

        mockMvc
                .perform(get(String.format("/batteries/postcode?start=%s&end=%s", start, end)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn416ForNoBatteriesInGivenRange() throws Exception {
        String start = "0000";
        String end = "0010";

        mockMvc
                .perform(get(String.format("/batteries/postcode?start=%s&end=%s", start, end)))
                .andDo(print())
                .andExpect(status().isRequestedRangeNotSatisfiable());
    }
}
