package np.com.roshanadhikary.vpps;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * Test data-access logic
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VppsRepositoryTests {

    @Autowired
    private BatteryRepository repository;

    private static final List<Battery> mockBatteries = List.of(
            new Battery(1, "Duracell", String.valueOf(1220), 25000),
            new Battery(2, "Eveready", String.valueOf(1240), 30000),
            new Battery(3, "RabbitCell", String.valueOf(1250), 31000),
            new Battery(4, "AllStarCell", String.valueOf(1270), 28000),
            new Battery(5, "TrustedBattery", String.valueOf(1290), 30000)
    );

    @BeforeAll
    public void init() {
        repository.saveAll(mockBatteries);
    }

    /**
     * batteries returned should be within the given postcode range
     */
    @Test
    public void getBatteriesBetweenPostcodeRangeTest() {
        String start = "1230";
        String end = "1280";

        List<Battery> responseBatteries = repository
                .findBatteriesBetweenPostcodeRange(Integer.parseInt(start), Integer.parseInt(end));

        Assertions
                .assertThat(responseBatteries)
                .map(battery -> battery.getPostcode())
                .allSatisfy(postcode -> Assertions.assertThat(postcode).isBetween(start, end));
    }
}
