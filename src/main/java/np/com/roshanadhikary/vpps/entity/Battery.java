package np.com.roshanadhikary.vpps.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;

/**
 * Database entity where name, postcode, and capacity are user-supplied fields
 */

@Data
@Entity
@Table(name="batteries")
@RequiredArgsConstructor
@NoArgsConstructor
public class Battery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @Column
    @NonNull
    private String name;

    @Column
    @NonNull
    private String postcode;

    @Column
    @NonNull
    private int capacity;

}
