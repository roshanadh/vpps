package np.com.roshanadhikary.vpps.repository;

import np.com.roshanadhikary.vpps.entity.Battery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BatteryRepository extends CrudRepository<Battery, Integer> {

    @Query(value = "SELECT b FROM Battery b WHERE CAST(b.postcode as int) BETWEEN ?1 AND ?2 ORDER BY b.name")
    List<Battery> findBatteriesBetweenPostcodeRange(int start, int end);
}
