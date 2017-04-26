package cx.mb.monitor.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Region;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cx.mb.monitor.R;
import cx.mb.monitor.beacon.BeaconManagerBuilder;
import cx.mb.monitor.beacon.BeaconMonitorNotifier;
import cx.mb.monitor.beacon.BeaconRangeNotifier;
import cx.mb.monitor.service.MainService;
import trikita.log.Log;

/**
 * メインアクティビティ
 */
public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String KEY_UUID = "UUID";
    private static final String KEY_PERIOD = "PERIOD";

//    /**
//     * 一覧用フラグメント
//     */
//    @BindView(R.id.fragment_list)
//    Fragment fragmentList;
//
//    /**
//     * グラフ用フラグメント
//     */
//    @BindView(R.id.fragment_graph)
//    Fragment fragmentGraph;

    /**
     * Beacon Manager
     */
    private BeaconManager beaconManager = null;

    /**
     * 画面用サービス
     */
    private MainService service;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        service = new MainService(this);

        // Bluetooth自体のチェック
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

            Log.i("isGrantedAll ? :" + String.valueOf(grantedAll));

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
            Log.e(e);
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
            Log.i(e);
            throw new RuntimeException(e);
        }
    }

    /**
     * ビーコンマネージャへの接続
     */
    private void bindBeaconManager() {

        if (beaconManager == null) {
            beaconManager = BeaconManagerBuilder.build(this);
            beaconManager.setForegroundScanPeriod(getIntent().getIntExtra(KEY_PERIOD, 200));
            beaconManager.bind(this);
        }

    }
}

