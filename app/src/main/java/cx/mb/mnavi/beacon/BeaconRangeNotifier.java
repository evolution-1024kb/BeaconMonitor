package cx.mb.mnavi.beacon;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.Iterator;

import cx.mb.mnavi.realm.Item;
import io.realm.Realm;
import trikita.log.Log;

/**
 * ビーコン情報通知クラス
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconRangeNotifier implements RangeNotifier {

    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> collection, Region region) {
        if (collection.isEmpty()) {
            return;
        }

        try (Realm localRealm = Realm.getDefaultInstance()) {
            localRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(Item.class);

                    int i = 0;
                    for (Beacon col : collection) {
                        final String uuid = col.getId1().toString().toUpperCase();
                        final int major = col.getId2().toInt();
                        final int minor = col.getId3().toInt();

                        Item item = new Item();
                        item.setId(i);
                        item.setUuid(uuid);
                        item.setMajor(major);
                        item.setMinor(minor);

                        realm.insert(item);
                        i++;
                    }
                }
            });
        }

        for (final Beacon beacon : collection) {
            Log.i(beacon.getId1() + ":" + beacon.getId2() + ":" + beacon.getId3() + ":" + beacon.getDistance());
        }
    }
}
