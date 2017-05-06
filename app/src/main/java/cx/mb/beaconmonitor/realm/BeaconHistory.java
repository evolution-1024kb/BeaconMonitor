package cx.mb.beaconmonitor.realm;

import java.util.Date;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * Beacon detection history.
 * Created by toshiaki on 2017/04/20.
 */
@Getter
@Setter
public class BeaconHistory extends RealmObject {
    /**
     * Date of detection.
     */
    private Date detectAt;

    /**
     * RSSI
     */
    private int rssi;

    /**
     * distance
     */
    private double distance;

    /**
     * txPower
     */
    private int txPower;

    /**
     * Back link to Beacon
     */
    private BeaconItem owner;
}
