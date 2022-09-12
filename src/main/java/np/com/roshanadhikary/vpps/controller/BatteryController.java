package np.com.roshanadhikary.vpps.controller;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.entity.BatteryListEntity;
import np.com.roshanadhikary.vpps.service.BatteryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BatteryListEntity getBatteriesFromPostcodeRange(@RequestParam int start, @RequestParam int end) {
        return service.findAllWithinPostcodeRange(start, end);
    }


    @PostMapping("/batteries/")
    public List<Battery> saveBatteries(@RequestBody List<Battery> batteries) {
        return service.saveAll(batteries);
    }
}
