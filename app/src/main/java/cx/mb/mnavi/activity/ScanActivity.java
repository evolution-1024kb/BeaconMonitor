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
import cx.mb.mnavi.adapter.ItemsAdapter;
import cx.mb.mnavi.beacon.BeaconManagerBuilder;
import cx.mb.mnavi.beacon.BeaconMonitorNotifier;
import cx.mb.mnavi.beacon.BeaconRangeNotifier;
import cx.mb.mnavi.realm.Beacon;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import trikita.log.Log;

public class ScanActivity extends AppCompatActivity implements BeaconConsumer {

    /**
     * Beacon Manager
     */
    private BeaconManager beaconManager;
    /**
     * Realmインスタンス
     */
    private Realm realm;

    @BindView(R.id.beacons)
    ListView beacons;

    /**
     * UUID
     */
    private String uuid = "";

    /*** 表示用インテントの作成
     *
     * @return インテント
     */
    public static Intent createIntent(Context context, String uuid) {

        final Intent intent = new Intent(context, ScanActivity.class);
        intent.putExtra("UUID", uuid);

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

        beaconManager = BeaconManagerBuilder.build(this);
        beaconManager.bind(this);

        // ListView初期化
        final RealmResults<Beacon> beacons = realm.where(Beacon.class).findAllSorted(new String[]{"uuid", "major", "minor"}, new Sort[]{Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING});
        final ItemsAdapter adapter = new ItemsAdapter(this, beacons);
        this.beacons.setAdapter(adapter);

        uuid = getIntent().getStringExtra("UUID");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        realm.close();
    }

    @Override
    public void onBeaconServiceConnect() {

        final Identifier identifier = (uuid == null || uuid.equals("")) ? null : Identifier.parse(uuid);
        final Region region = new Region("startRangingBeaconsInRegion", identifier, null, null);

        beaconManager.addMonitorNotifier(new BeaconMonitorNotifier(beaconManager));
        beaconManager.addRangeNotifier(new BeaconRangeNotifier());

        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.i(e);
            throw new RuntimeException(e);
        }
    }
}
