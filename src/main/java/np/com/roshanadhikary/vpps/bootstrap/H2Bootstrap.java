package np.com.roshanadhikary.vpps.bootstrap;

import np.com.roshanadhikary.vpps.entity.Battery;
import np.com.roshanadhikary.vpps.repository.BatteryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Bootstrap the in-memory H2 database with some Battery resources when
 * the application starts
 */
@Configuration
public class H2Bootstrap {

    private static final Logger logger = LoggerFactory.getLogger(H2Bootstrap.class);

    public static List<Battery> mockBatteries = new ArrayList<>() {
        {
            add(new Battery("Battery Loc 1", String.valueOf(1010), 20500));
            add(new Battery("Battery Loc 2", String.valueOf(1020), 20000));
            add(new Battery("Battery Loc 3", String.valueOf(1040), 30000));
            add(new Battery("Battery Loc 4", String.valueOf(1060), 30500));

        }
    };
    @Bean
    CommandLineRunner initDb(BatteryRepository repository) {
        return args -> mockBatteries.forEach(battery ->
            logger.info("Preloading " + repository.save(battery))
        );
    }
}
