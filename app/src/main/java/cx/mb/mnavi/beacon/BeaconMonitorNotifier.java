package cx.mb.mnavi.beacon;

import android.os.RemoteException;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import io.realm.Realm;
import io.realm.RealmResults;
import cx.mb.mnavi.realm.Item;
import trikita.log.Log;

/**
 * ビーコン検出通知クラス
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconMonitorNotifier implements MonitorNotifier {

    /**
     * ビーコンマネージャ
     */
    private final BeaconManager beaconManager;

    /**
     * コンストラクタ
     * @param beaconManager ビーコンマネージャ
     */
    public BeaconMonitorNotifier(BeaconManager beaconManager) {
       this.beaconManager = beaconManager;
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.i("I just saw an beacon for the first time!");

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e(e);
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Log.i("I no longer see an beacon");
        try {
            beaconManager.stopRangingBeaconsInRegion(region);

            try (Realm localRealm = Realm.getDefaultInstance()) {
                localRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.delete(Item.class);
                    }
                });
            }
        } catch (RemoteException e) {
            Log.e(e);
        }

    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.i("I have just switched from seeing/not seeing beacons: " + state);

    }
}
