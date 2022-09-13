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
    public void shouldReturnHelloMessage() throws Exception {
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
    public void shouldFetchBatteryID1() throws Exception {
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
    public void shouldReturnAllBatteries() throws Exception {
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
    public void shouldReturnBatteriesWithinRange() throws Exception {
        String start = "0010";
        String end = "1070";

        List<Battery> resultBatteries = H2Bootstrap
                .mockBatteries
                .stream()
                // only retain batteries with postcode inside the given range [start, end]
                .filter(b -> Integer.parseInt(b.getPostcode()) >= Integer.parseInt(start) && Integer.parseInt(b.getPostcode()) <= Integer.parseInt(end))
                .collect(Collectors.toList());

        mockMvc
                .perform(get(new String().format("/batteries/postcode?start=%s&end=%s", start, end)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCapacity").isNumber())
                .andExpect(jsonPath("$.totalCapacity").value(resultBatteries.stream().mapToLong(b -> b.getCapacity()).sum()))
                .andExpect(jsonPath("$.avgCapacity").isNumber())
                .andExpect(jsonPath("$.avgCapacity").value(resultBatteries.stream().mapToDouble(b -> b.getCapacity()).average().orElse(0D)))
                .andExpect(jsonPath("$.batteries").isArray());
    }

    @Test
    public void shouldPersistAllBatteries() throws Exception {
        List<Battery> mockBatteries = VppsRepositoryTests.mockBatteries;

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
    public void shouldReturn404ForNonExistentBatteryID() throws Exception {
        int id = 102;
        mockMvc
                .perform(get("/batteries/" + id))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotPersistBatteriesWithSamePostcode() throws Exception {
        List<Battery> mockBatteries = H2Bootstrap.mockBatteries;

        mockMvc
                .perform(
                        post("/batteries/")
                                .content(objectMapper.writeValueAsString(mockBatteries))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    public void shouldReturnBadRequestForInvalidRange() throws Exception {
        String start = "abcd";
        String end = "#123";

        mockMvc
                .perform(get(new String().format("/batteries/postcode?start=%s&end=%s", start, end)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}