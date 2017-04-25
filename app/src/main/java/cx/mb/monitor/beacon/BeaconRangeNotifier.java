package cx.mb.monitor.beacon;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import cx.mb.monitor.realm.BeaconHistory;
import cx.mb.monitor.realm.BeaconItem;
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

        try (final Realm localRealm = Realm.getDefaultInstance()) {
            localRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    for (Beacon col : collection) {
                        final String uuid = col.getId1().toString().toUpperCase();
                        final int major = col.getId2().toInt();
                        final int minor = col.getId3().toInt();
                        final int rssi = col.getRssi();
                        final double distance = col.getDistance();
                        final int txPower = col.getTxPower();
                        final String id = String.format(Locale.US, "%s_%d_%d", uuid, major, minor);

                        BeaconItem beacon = localRealm.where(BeaconItem.class).equalTo("id", id).findFirst();

                        if (beacon != null) {
                            Log.i("Beacon " + id + "is exists.");
                        } else {
                            Log.i("Beacon " + id + "is not exists.");
                            beacon = realm.createObject(BeaconItem.class, id);
                            beacon.setUuid(uuid);
                            beacon.setMajor(major);
                            beacon.setMinor(minor);

                            realm.insert(beacon);
                        }

                        final BeaconHistory history = realm.createObject(BeaconHistory.class);
                        history.setScanAt(DateUtils.truncate(new Date(), Calendar.SECOND));
                        history.setRssi(rssi);
                        history.setDistance(distance);
                        history.setTxPower(txPower);
                        history.setOwner(beacon);

                        beacon.getHistories().add(history);
                    }
                }
            });
        }

        for (final org.altbeacon.beacon.Beacon beacon : collection) {
            Log.i(beacon.getId1() + ":" + beacon.getId2() + ":" + beacon.getId3() + ":" + beacon.getRssi() + ":" + beacon.getDistance());
        }
    }
}
