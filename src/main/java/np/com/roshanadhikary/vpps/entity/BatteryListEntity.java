package np.com.roshanadhikary.vpps.entity;

import lombok.Data;

import java.util.List;

/**
 * Response entity for action GET /batteries/postcode?start={}&end={}
 */
@Data
public class BatteryListEntity {
    private long totalCapacity;
    private double avgCapacity;
    private List<String> batteries;
}
