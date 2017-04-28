package cx.mb.monitor.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

/**
 * Beacon
 * Created by toshiaki on 2017/01/22.
 */
@Getter
@Setter
public class BeaconItem extends RealmObject {

    /**
     * Primary key(uuid_major_minor).
     */
    @PrimaryKey
    private String id;

    /**
     * Proximity UUID.
     */
    @Required
    private String uuid;

    /**
     * Major.
     */
    private int major;

    /**
     * Minor.
     */
    private int minor;

    /**
     * Status of beacon.
     */
    private BeaconStatus status;

    /**
     * Histories.
     */
    private RealmList<BeaconHistory> histories;
}
