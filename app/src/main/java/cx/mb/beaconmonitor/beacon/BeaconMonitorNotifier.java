package cx.mb.beaconmonitor.beacon;

import android.os.RemoteException;
import cx.mb.beaconmonitor.realm.BeaconStatus;
import io.realm.Realm;
import io.realm.RealmResults;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import timber.log.Timber;

/**
 * A Monitor of beacon detection.
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconMonitorNotifier implements MonitorNotifier {

    /**
     * Beacon Manager.
     */
    private final BeaconManager beaconManager;

    /**
     * Constructor.
     *
     * @param manager BeaconManager.
     */
    public BeaconMonitorNotifier(BeaconManager manager) {
        this.beaconManager = manager;
    }

    @Override
    public void didEnterRegion(Region region) {
        Timber.i("I just saw an beacon for the first time!");

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Timber.e(e.toString());
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Timber.i("I no longer see an beacon");

        try {
            beaconManager.stopRangingBeaconsInRegion(region);
            try (Realm realm = Realm.getDefaultInstance()) {
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        final RealmResults<BeaconStatus> items = realm.where(BeaconStatus.class).findAll();
                        for (BeaconStatus item : items) {
                            item.setDetection(false);
                        }
                    }
                });
            }
        } catch (RemoteException e) {
            Timber.e(e.toString());
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Timber.i("I have just switched from seeing/not seeing beacons: %d", state);

    }
}
