package cx.mb.beaconmonitor.beacon;


import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import cx.mb.beaconmonitor.realm.BeaconHistory;
import cx.mb.beaconmonitor.realm.BeaconItem;
import cx.mb.beaconmonitor.realm.BeaconStatus;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * A Notifier of beacon detection.
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

                    final RealmResults<BeaconStatus> items = realm.where(BeaconStatus.class).findAll();
                    for (BeaconStatus item : items) {
                        item.setDetection(false);
                    }

                    for (Beacon col : collection) {
                        final String uuid = col.getId1().toString().toUpperCase(Locale.ENGLISH);
                        final int major = col.getId2().toInt();
                        final int minor = col.getId3().toInt();
                        final int rssi = col.getRssi();
                        final double distance = col.getDistance();
                        final int txPower = col.getTxPower();
                        final String id = String.format(Locale.US, "%s_%d_%d", uuid, major, minor);

                        BeaconItem beacon = realm.where(BeaconItem.class).equalTo("id", id).findFirst();
                        BeaconStatus status;

                        if (beacon != null) {
                            Timber.i("Beacon %s is exists.", id);
                        } else {
                            Timber.i("Beacon %s is not exists.", id);
                            beacon = realm.createObject(BeaconItem.class, id);
                            beacon.setUuid(uuid);
                            beacon.setMajor(major);
                            beacon.setMinor(minor);
                            beacon.setStatus(realm.createObject(BeaconStatus.class));

                            realm.insert(beacon);
                        }

                        final BeaconHistory history = realm.createObject(BeaconHistory.class);
                        history.setDetectAt(DateUtils.truncate(new Date(), Calendar.SECOND));
                        history.setRssi(rssi);
                        history.setDistance(distance);
                        history.setTxPower(txPower);
                        history.setOwner(beacon);

                        status = beacon.getStatus();
                        status.setDetection(true);

                        beacon.getHistories().add(history);
                    }
                }
            });
        }

        for (final org.altbeacon.beacon.Beacon beacon : collection) {
            Timber.i("%s:%s:%s:%d:%s", beacon.getId1(), beacon.getId2(), beacon.getId3(), beacon.getRssi(), beacon.getDistance());
        }
    }
}
