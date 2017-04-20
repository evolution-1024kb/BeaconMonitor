package cx.mb.mnavi.realm;

import java.util.Date;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * history of beacon detection.
 * Created by toshiaki on 2017/04/20.
 */
@Getter
@Setter
public class BeaconHistory extends RealmObject {
    /**
     * datetime of detection.
     */
    private Date scanAt;

    /**
     * RSSI
     */
    private int rssi;

    /**
     * Distance
     */
    private double distance;

    /**
     * txPower
     */
    private int txPower;
}
