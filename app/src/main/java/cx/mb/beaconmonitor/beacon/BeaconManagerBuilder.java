package cx.mb.beaconmonitor.beacon;

import android.content.Context;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

import cx.mb.beaconmonitor.R;

/**
 * A Utility class of Beacon Manager.
 * ビーコンマネージャの作成ユーティリティ
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconManagerBuilder {

    /**
     * Build instance.
     *
     * @return BeaconManager.
     */
    public static BeaconManager build(Context context) {

        BeaconManager bm = BeaconManager.getInstanceForApplication(context);
        bm.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(context.getString(R.string.beacon_format)));
        bm.setForegroundBetweenScanPeriod(context.getResources().getInteger(R.integer.beacon_foreground_between_scan_period));
        bm.setBackgroundBetweenScanPeriod(context.getResources().getInteger(R.integer.beacon_foreground_between_scan_period));

        return bm;
    }

}
