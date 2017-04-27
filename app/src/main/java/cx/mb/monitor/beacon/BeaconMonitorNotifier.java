package cx.mb.monitor.beacon;

import android.os.RemoteException;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import trikita.log.Log;

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
     * @param manager BeaconManager.
     */
    public BeaconMonitorNotifier(BeaconManager manager) {
       this.beaconManager = manager;
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
        } catch (RemoteException e) {
            Log.e(e);
        }

    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Log.i("I have just switched from seeing/not seeing beacons: " + state);

    }
}
