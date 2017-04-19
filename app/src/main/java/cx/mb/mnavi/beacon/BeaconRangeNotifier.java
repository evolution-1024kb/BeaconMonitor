package cx.mb.mnavi.beacon;

import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import cx.mb.mnavi.realm.Beacon;
import io.realm.Realm;
import trikita.log.Log;

/**
 * ビーコン情報通知クラス
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconRangeNotifier implements RangeNotifier {

    @Override
    public void didRangeBeaconsInRegion(final Collection<org.altbeacon.beacon.Beacon> collection, Region region) {
        if (collection.isEmpty()) {
            return;
        }

        try (Realm localRealm = Realm.getDefaultInstance()) {
            localRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(Beacon.class);

                    int i = 0;
                    for (org.altbeacon.beacon.Beacon col : collection) {
                        final String uuid = col.getId1().toString().toUpperCase();
                        final int major = col.getId2().toInt();
                        final int minor = col.getId3().toInt();
                        final int rssi = col.getRssi();
                        final double distance = col.getDistance();

                        Beacon beacon = new Beacon();
                        beacon.setId(i);
                        beacon.setUuid(uuid);
                        beacon.setMajor(major);
                        beacon.setMinor(minor);
                        beacon.setRssi(rssi);
                        beacon.setDistance(distance);

                        realm.insert(beacon);
                        i++;
                    }
                }
            });
        }

        for (final org.altbeacon.beacon.Beacon beacon : collection) {
            Log.i(beacon.getId1() + ":" + beacon.getId2() + ":" + beacon.getId3() + ":" + beacon.getRssi() + ":" + beacon.getDistance());
        }
    }
}
