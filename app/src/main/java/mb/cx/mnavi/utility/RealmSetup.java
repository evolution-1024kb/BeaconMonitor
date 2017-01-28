package mb.cx.mnavi.utility;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmResults;
import mb.cx.mnavi.R;
import mb.cx.mnavi.realm.Item;

/**
 * Realmの仮データ登録
 * Created by toshiaki on 2017/01/22.
 */
public class RealmSetup {

    /**
     * テストデータを作成
     * @param context コンテキスト
     */
    public void setup(Context context) {

        final String uuid = context.getString(R.string.beacon_proximity_uuid);

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmResults<Item> items = realm.where(Item.class).findAll();
                items.deleteAllFromRealm();

                final Item one = realm.createObject(Item.class, 1L);
                one.setTitle("ONE");
                one.setBody("ONE-ONE");
                one.setUuid(uuid);
                one.setMajor(1);
                one.setMinor(1);

                final Item two = realm.createObject(Item.class, 2L);
                two.setTitle("TWO");
                two.setBody("TWO-TWO");
                two.setUuid(uuid);
                two.setMajor(1);
                two.setMinor(2);

                final Item thr = realm.createObject(Item.class, 3L);
                thr.setTitle("THREE");
                thr.setBody("THREE-THREE");
                thr.setUuid(uuid);
                thr.setMajor(1);
                thr.setMinor(3);

                final Item fur = realm.createObject(Item.class, 4L);
                fur.setTitle("FOUR");
                fur.setBody("FOUR-FOUR");
                fur.setUuid(uuid);
                fur.setMajor(1);
                fur.setMinor(4);

                final Item fiv = realm.createObject(Item.class, 5L);
                fiv.setTitle("FIVE");
                fiv.setBody("FIVE-FIVE");
                fiv.setUuid(uuid);
                fiv.setMajor(1);
                fiv.setMinor(5);
            }
        });
        realm.close();
    }

}
