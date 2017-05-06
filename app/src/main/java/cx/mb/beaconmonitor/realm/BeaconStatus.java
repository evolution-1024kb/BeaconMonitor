package cx.mb.beaconmonitor.realm;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Status of beacon.
 * Created by toshiaki on 2017/04/25.
 */
@Setter
@Getter
public class BeaconStatus extends RealmObject {
    /**
     * Back link.
     */
    private BeaconItem owner;

    /**
     * detection.
     */
    private boolean detection;
}
