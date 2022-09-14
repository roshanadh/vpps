package np.com.roshanadhikary.vpps.repository;

import np.com.roshanadhikary.vpps.entity.Battery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BatteryRepository extends CrudRepository<Battery, Integer> {

    /**
     * Fetch a list of Battery resources, each having a postcode value in the supplied range [start, end].
     * The string postcode value is cast to integer for comparison.
     * @param start
     * @param end
     * @return List of Battery resources
     */
    @Query(value = "SELECT b FROM Battery b WHERE CAST(b.postcode as int) BETWEEN ?1 AND ?2 ORDER BY b.name")
    List<Battery> findBatteriesBetweenPostcodeRange(int start, int end);
}
