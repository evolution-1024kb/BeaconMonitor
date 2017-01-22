package mb.cx.mnavi.utility;

import io.realm.Realm;
import io.realm.RealmResults;
import mb.cx.mnavi.realm.Item;

/**
 * Realmの仮データ登録
 * Created by toshiaki on 2017/01/22.
 */
public class RealmSetup {
    public void setup() {

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmResults<Item> items = realm.where(Item.class).findAll();
                items.deleteAllFromRealm();

                final Item one = realm.createObject(Item.class, 1L);
                one.setTitle("ONE");
                one.setBody("ONE-ONE");
                one.setUuid("UUID");
                one.setMajor(1);
                one.setMinor(1);

                final Item two = realm.createObject(Item.class, 2L);
                two.setTitle("TWO");
                two.setBody("TWO-TWO");
                two.setUuid("UUID");
                two.setMajor(1);
                two.setMinor(2);

                final Item thr = realm.createObject(Item.class, 3L);
                thr.setTitle("THREE");
                thr.setBody("THREE-THREE");
                thr.setUuid("UUID");
                thr.setMajor(1);
                thr.setMinor(3);

                final Item fur = realm.createObject(Item.class, 4L);
                fur.setTitle("FOUR");
                fur.setBody("FOUR-FOUR");
                fur.setUuid("UUID");
                fur.setMajor(1);
                fur.setMinor(4);

                final Item fiv = realm.createObject(Item.class, 5L);
                fiv.setTitle("FIVE");
                fiv.setBody("FIVE-FIVE");
                fiv.setUuid("UUID");
                fiv.setMajor(1);
                fiv.setMinor(5);
            }
        });
        realm.close();
    }

}
