package cx.mb.beaconmonitor.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.util.List;

import butterknife.ButterKnife;
import cx.mb.beaconmonitor.R;
import cx.mb.beaconmonitor.beacon.BeaconManagerBuilder;
import cx.mb.beaconmonitor.beacon.BeaconMonitorNotifier;
import cx.mb.beaconmonitor.beacon.BeaconRangeNotifier;
import cx.mb.beaconmonitor.service.MainService;
import timber.log.Timber;

/**
 * A Main activity.
 */
public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String KEY_UUID = "UUID";
    private static final String KEY_PERIOD = "PERIOD";

    /**
     * Beacon Manager
     */
    private BeaconManager beaconManager = null;

    /**
     * Service of this activity.
     */
    private MainService service;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        service = new MainService(this);

        // Check bluetooth enabled.
        if (!service.isBluetoothEnabled()) {
            service.requestBluetooth();
        } else {
            final List<String> lackedPermissions = service.getLackedPermissions();
            if (!lackedPermissions.isEmpty()) {
                service.requestPermissions(lackedPermissions);
            } else {
                bindBeaconManager();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MainService.REQUEST_PERMISSIONS) {

            boolean grantedAll = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    grantedAll = false;
                    break;
                }
            }

            Timber.i("isGrantedAll ? : %s", String.valueOf(grantedAll));

            if (grantedAll) {
                bindBeaconManager();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            beaconManager.stopMonitoringBeaconsInRegion(region);
            beaconManager.stopRangingBeaconsInRegion(region);
            beaconManager.removeAllMonitorNotifiers();
            beaconManager.removeAllRangeNotifiers();
        } catch (RemoteException e) {
            Timber.e(e.toString());
        }
        beaconManager.unbind(this);
        beaconManager = null;

    }

    @Override
    public void onBeaconServiceConnect() {

        region = new Region("region", null, null, null);

        beaconManager.addMonitorNotifier(new BeaconMonitorNotifier(beaconManager));
        beaconManager.addRangeNotifier(new BeaconRangeNotifier());

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            Timber.e(e.toString());
            throw new RuntimeException(e);
        }
    }

    /**
     * Connect to beacon manager.
     */
    private void bindBeaconManager() {

        if (beaconManager == null) {
            beaconManager = BeaconManagerBuilder.build(this);
            beaconManager.setForegroundScanPeriod(getIntent().getIntExtra(KEY_PERIOD, 200));
            beaconManager.bind(this);
        }

    }
}

