package mb.cx.mnavi.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import mb.cx.mnavi.R;
import mb.cx.mnavi.adapter.NearItemsAdapter;
import mb.cx.mnavi.realm.Item;
import trikita.log.Log;

/**
 * メインアクティビティ
 */
public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    /**
     * 権限リクエスト
     */
    private static final int REQUEST_PERMISSIONS = 100;

    /**
     * Bluetooth有効化リクエスト
     */
    private static final int REQUEST_ENABLE_BLUETOOTH = 200;

    /**
     * Beacon Manager
     */
    private BeaconManager beaconManager;

    /**
     * 展示物一覧
     */
    @BindView(R.id.list_near_items)
    ListView nearItems;

    /**
     * Realmインスタンス
     */
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        // Bluetooth自体のチェック
        final boolean isBluetoothEnabled = isBluetoothEnabled();
        if (isBluetoothEnabled) {
            final boolean granted = isGrantedAllPermissions();
            if (granted) {
                initBeaconManager();
            }
        }

        final RealmResults<Item> items = realm.where(Item.class).findAll().sort("title");
        final NearItemsAdapter adapter = new NearItemsAdapter(this, items);
        nearItems.setAdapter(adapter);
    }

    /**
     * Bluetoothが使えるかどうか
     *
     * @return 有効ならTRUE
     */
    private boolean isBluetoothEnabled() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOn, REQUEST_ENABLE_BLUETOOTH);
            return false;
        }

        return true;
    }

    /**
     * 位置情報およびBluetoothが使用出来るか
     *
     * @return 全て使用可能ならTRUE
     */
    private boolean isGrantedAllPermissions() {

        final List<String> permissions = new ArrayList<>();
        if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (this.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH);
        }
        if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[0]), REQUEST_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                final boolean isGranted = isGrantedAllPermissions();
                if (isGranted) {
                    initBeaconManager();
                }
            }
        }
    }

    /**
     * ビーコンマネージャの初期化
     */
    private void initBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setForegroundBetweenScanPeriod(2000);
        beaconManager.setBackgroundBetweenScanPeriod(2000);
        beaconManager.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS) {

            boolean grantedAll = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    grantedAll = false;
                    break;
                }
            }

            if (grantedAll) {
                initBeaconManager();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        final String uuid = getString(R.string.beacon_proximity_uuid);
        final Identifier identifier = Identifier.parse(uuid);
        final Region region = new Region("startRangingBeaconsInRegion", identifier, null, null);

        beaconManager.addMonitorNotifier(new MonitorNotifier() {
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
        });
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (!collection.isEmpty()) {
                    final Beacon beacon = collection.iterator().next();
                    Log.i(beacon.getId1() + ":" + beacon.getId2() + ":" + beacon.getId3() + ":" + beacon.getDistance());
                }
            }
        });
        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.i(e);
        }
    }
}
