package np.com.roshanadhikary.vpps.entity;

import lombok.Data;
import javax.persistence.*;


@Data
@Entity
@Table(name="batteries")
public class Battery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column
    private String name;

    @Column
    private int postcode;

    @Column
    private int capacity;
}
