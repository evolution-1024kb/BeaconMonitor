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
            }
        });
    }

}
