package cx.mb.mnavi.realm;

import java.util.Date;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * ビーコン検出履歴
 * Created by toshiaki on 2017/04/20.
 */
@Getter
@Setter
public class BeaconHistory extends RealmObject {
    /**
     * 検出日時
     */
    private Date scanAt;

    /**
     * RSSI
     */
    private int rssi;

    /**
     * おおよその距離
     */
    private double distance;

    /**
     * txPower
     */
    private int txPower;
}
