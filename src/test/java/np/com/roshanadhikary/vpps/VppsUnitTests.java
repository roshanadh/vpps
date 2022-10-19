package np.com.roshanadhikary.vpps;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.entity.BatteryListEntity;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
import np.com.roshanadhikary.vpps.service.BatteryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

@SpringBootTest
class VppsUnitTests {

	@Autowired
	private BatteryService service;

	@MockBean
	private BatteryRepository repository;

	private static final List<Battery> mockBatteries = List.of(
			new Battery("Battery Loc 1", String.valueOf(1220), 25000),
			new Battery("Battery Loc 2", String.valueOf(1240), 30000)
	);

	@Test
	void shouldFetchAllBatteries() {
		when(repository.findAll())
				.thenReturn(mockBatteries);

		List<Battery> responseBatteries = service.findAll();

		Assertions.assertEquals(mockBatteries.size(), responseBatteries.size());
	}

	@Test
	void shouldFetchBatteriesWithinRange() {
		String start = "1200";
		String end = "1280";

		long totalCapacity = mockBatteries
				.stream()
				.mapToLong(b -> b.getCapacity())
				.sum();

		double avgCapacity = mockBatteries
				.stream()
				.mapToDouble(b -> b.getCapacity())
				.average()
				.orElse(0D);

		when(repository.findBatteriesBetweenPostcodeRange(Integer.parseInt(start), Integer.parseInt(end)))
				.thenReturn(mockBatteries);

		BatteryListEntity responseEntity = service.findAllBetweenPostcodeRange(start, end);

		Assertions.assertEquals(totalCapacity, responseEntity.getTotalCapacity());
		Assertions.assertEquals(avgCapacity, responseEntity.getAvgCapacity());
		Assertions.assertIterableEquals(
				mockBatteries
						.stream()
						.map(b -> b.getName())
						.collect(Collectors.toList()),
				responseEntity.getBatteries());
	}

	@Test
	void shouldThrowExceptionForNoBatteriesWithinRange() {
		String start = "0001";
		String end = "0010";

		when(repository.findBatteriesBetweenPostcodeRange(Integer.parseInt(start), Integer.parseInt(end)))
				.thenReturn(new ArrayList<>());

		Exception rse = Assertions
				.assertThrows(ResponseStatusException.class, () -> service.findAllBetweenPostcodeRange(start, end));
	}

	@Test
	void shouldPersistAllBatteries() {
		when(repository.saveAll(mockBatteries)).thenReturn(mockBatteries);

		Assertions.assertEquals(mockBatteries, service.saveAll(mockBatteries));
	}

	@Test
	void shouldPersistSingleBattery() {
		Battery mockBattery = mockBatteries.get(0);

		when(repository.save(mockBattery)).thenReturn(mockBattery);

		Assertions.assertEquals(mockBattery, service.save(mockBattery));
	}
}
