package np.com.roshanadhikary.vpps;

import static org.mockito.Mockito.when;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.entity.BatteryListEntity;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
import np.com.roshanadhikary.vpps.service.BatteryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;

import java.util.List;

@SpringBootTest
class VppsMockTests {

	@Autowired
	private BatteryService service;

	@MockBean
	private BatteryRepository repository;

	private static final List<Battery> mockBatteries = List.of(
			new Battery(1, "Duracell", 1220, 25000),
			new Battery(2, "Eveready", 1240, 30000)
	);

	@Test
	public void getBatteriesTest() {
		when(repository.findAll())
				.thenReturn(mockBatteries);

		List<Battery> responseBatteries = service.findAll();

		Assertions.assertEquals(mockBatteries.size(), responseBatteries.size());
	}

	@Test
	public void getBatteriesBetweenPostcodeRangeTest() {
		int start = 1200;
		int end = 1280;

		long totalCapacity = mockBatteries
				.stream()
				.mapToLong(b -> b.getCapacity())
				.sum();

		double avgCapacity = mockBatteries
				.stream()
				.mapToDouble(b -> b.getCapacity())
				.average()
				.orElse(0D);

		when(repository.findBatteriesBetweenPostcodeRange(start, end, Sort.by("name")))
				.thenReturn(mockBatteries);

		BatteryListEntity responseEntity = service.findAllBetweenPostcodeRange(start, end);

		Assertions.assertEquals(totalCapacity, responseEntity.getTotalCapacity());
		Assertions.assertEquals(avgCapacity, responseEntity.getAvgCapacity());
		Assertions.assertIterableEquals(mockBatteries, responseEntity.getBatteries());
	}

	@Test
	public void saveBatteriesTest() {
		when(repository.saveAll(mockBatteries)).thenReturn(mockBatteries);

		Assertions.assertEquals(mockBatteries, service.saveAll(mockBatteries));
	}
}
