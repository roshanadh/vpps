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

    @Bean
    CommandLineRunner initDb(BatteryRepository repository) {
        List<Battery> initList = new ArrayList<>() {
            {
                add(new Battery(11, "Battery1", String.valueOf(1010), 20500));
                add(new Battery(11, "Battery2", String.valueOf(1020), 20000));
                add(new Battery(11, "Battery3", String.valueOf(1040), 30000));
                add(new Battery(11, "Battery4", String.valueOf(1060), 30500));

            }
        };

        return args -> initList.forEach(battery ->
            logger.info("Preloading " + repository.save(battery))
        );
    }
}
