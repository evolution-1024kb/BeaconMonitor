package cx.mb.mnavi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import butterknife.BindView;
import butterknife.ButterKnife;
import cx.mb.mnavi.R;
import cx.mb.mnavi.adapter.BeaconHistoryAdapter;
import cx.mb.mnavi.beacon.BeaconManagerBuilder;
import cx.mb.mnavi.beacon.BeaconMonitorNotifier;
import cx.mb.mnavi.beacon.BeaconRangeNotifier;
import cx.mb.mnavi.realm.BeaconHistory;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import trikita.log.Log;

/**
 * スキャンアクティビティ
 */
public class ScanActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String KEY_UUID = "UUID";
    private static final String KEY_PERIOD = "PERIOD";
    @BindView(R.id.beacons)
    ListView beacons;
    /**
     * Beacon Manager
     */
    private BeaconManager beaconManager = null;

    /**
     * Realmインスタンス
     */
    private Realm realm;

    /**
     * UUID
     */
    private String uuid = "";

    /**
     * 表示用インテントの作成
     *
     * @param context コンテキスト
     * @param uuid    UUID
     * @return インテント
     */
    public static Intent createIntent(Context context, String uuid) {

        final Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra(KEY_UUID, uuid);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        // ListView初期化
        final RealmResults<BeaconHistory> beacons = realm.where(BeaconHistory.class).findAllSorted("scanAt", Sort.DESCENDING);
        final BeaconHistoryAdapter adapter = new BeaconHistoryAdapter(this, beacons);
        this.beacons.setAdapter(adapter);

        this.uuid = getIntent().getStringExtra(KEY_UUID);

        if (beaconManager == null) {
            beaconManager = BeaconManagerBuilder.build(this);
            beaconManager.setForegroundScanPeriod(getIntent().getIntExtra(KEY_PERIOD, 200));
            beaconManager.bind(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        final Region region = createRegion();
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

        realm.close();
    }

    @Override
    public void onBeaconServiceConnect() {

        final Region region = createRegion();

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
     * 検出領域の作成
     *
     * @return 検出領域
     */
    private Region createRegion() {
        final Identifier identifier = (uuid == null || uuid.equals("")) ? null : Identifier.parse(uuid);

        return new Region("startRangingBeaconsInRegion", identifier, null, null);
    }
}
