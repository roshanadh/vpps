package np.com.roshanadhikary.vpps.controller;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
import np.com.roshanadhikary.vpps.service.BatteryService;
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

    private final BatteryService service;

    public BatteryController(BatteryService service) {
        this.service = service;
    }

    @GetMapping("/batteries/{id}")
    public Battery getBatteryFromId(@PathVariable int id) {
        return service.findById(id);
    }

    @GetMapping("/batteries/")
    public List<Battery> getAllBatteries() {
        return service.findAll();
    }


    @GetMapping("/batteries/postcode")
    public List<Battery> getBatteriesFromPostcodeRange(@RequestParam int start, @RequestParam int end) {
        return service.findAllWithinPostcodeRange(start, end);
    }


    @PostMapping("/batteries/")
    public List<Battery> saveBatteries(@RequestBody List<Battery> batteries) {
        return service.saveAll(batteries);
    }
}
