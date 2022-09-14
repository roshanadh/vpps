package np.com.roshanadhikary.vpps.service;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.entity.BatteryListEntity;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
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

    /**
     * @param id
     * @return a Battery resource, identified by an integer ID.
     * HTTP response 404 is returned and an exception thrown if no such resource with ID found.
     */
    public Battery findById(int id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("Battery with ID %s does not exist", id)
                        ));
    }

    /**
     * @return list of all Battery resources
     */
    public List<Battery> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    /**
     * @param start
     * @param end
     * @return a list of Battery resources, each having a postcode value in the supplied range [start, end].
     */
    public BatteryListEntity findAllBetweenPostcodeRange(String start, String end) {
        List<Battery> batteries;

        try {
            batteries = repository
                    .findBatteriesBetweenPostcodeRange(Integer.parseInt(start), Integer.parseInt(end));
        } catch (NumberFormatException nfe) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Postcode range should be numeric");
        }

        // throw with 416 Range Not Satisfiable response if no batteries in given range
        if (batteries.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                    String.format("Batteries with postcode in range [%s, %s] do not exist", start, end));

        long totalCapacity = batteries
                .stream()
                .mapToLong(Battery::getCapacity)
                .sum();

        double avgCapacity = batteries
                .stream()
                .mapToDouble(Battery::getCapacity)
                .average()
                .orElse(0D);

        List<String> batteryNames = batteries
                .stream()
                .map(Battery::getName)
                .collect(Collectors.toList());

        var responseEntity = new BatteryListEntity();

        responseEntity.setBatteries(batteryNames);
        responseEntity.setTotalCapacity(totalCapacity);
        responseEntity.setAvgCapacity(avgCapacity);

        return responseEntity;
    }

    /**
     * Persist the supplied list of Battery resources
     * @param batteries
     * @return the list of persisted Battery resources
     */
    public List<Battery> saveAll(List<Battery> batteries) {
        return StreamSupport
                .stream(repository.saveAll(batteries).spliterator(), false)
                .collect(Collectors.toList());
    }
}
