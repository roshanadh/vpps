package np.com.roshanadhikary.vpps.service;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.entity.BatteryListEntity;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BatteryService {

    private final BatteryRepository repository;

    public BatteryService(BatteryRepository repository) {
        this.repository = repository;
    }

    public Battery findById(int id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                new String().format("Battery with ID %s does not exist", id)
                        ));
    }

    public List<Battery> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public BatteryListEntity findAllBetweenPostcodeRange(int start, int end) {
        List<Battery> batteries = repository
                .findBatteriesBetweenPostcodeRange(start, end, Sort.by("name"));

        if (batteries.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                    new String().format("Batteries with postcode in range [%s, %s] do not exist", start, end));

        long totalCapacity = batteries
                .stream()
                .mapToLong(b -> b.getCapacity())
                .sum();

        double avgCapacity = batteries
                .stream()
                .mapToDouble(b -> b.getCapacity())
                .average()
                .orElse(0D);

        BatteryListEntity responseEntity = new BatteryListEntity();

        responseEntity.setBatteries(batteries);
        responseEntity.setTotalCapacity(totalCapacity);
        responseEntity.setAvgCapacity(avgCapacity);

        return responseEntity;
    }

    public List<Battery> saveAll(List<Battery> batteries) {
        try {
            return StreamSupport
                    .stream(repository.saveAll(batteries).spliterator(), false)
                    .collect(Collectors.toList());
        } catch (DataIntegrityViolationException dive) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Battery with postcode already exists");
        }
    }
}
