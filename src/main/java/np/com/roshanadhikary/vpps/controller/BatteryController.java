package np.com.roshanadhikary.vpps.controller;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class BatteryController {

    private final BatteryRepository repository;

    public BatteryController(BatteryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/batteries/{id}")
    public Battery getBatteryFromId(@PathVariable int id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                new String().format("Battery with ID %s does not exist", id)
                        ));
    }

    @GetMapping("/batteries/")
    public List<Battery> getAllBatteries() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }


    @GetMapping("/batteries/postcode")
    public List<Battery> getBatteriesFromPostcodeRange(@RequestParam int start, @RequestParam int end) {
        List<Battery> batteriesList = repository
                .findBatteriesBetweenPostcodeRange(start, end, Sort.by("name"));

        if (batteriesList.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE,
                    new String().format("Batteries with postcode in range [%s, %s] do not exist", start, end));

        return batteriesList;
    }


    @PostMapping("/batteries/")
    public List<Battery> saveBatteries(@RequestBody List<Battery> batteries) {
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
