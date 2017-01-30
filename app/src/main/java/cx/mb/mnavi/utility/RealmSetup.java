package cx.mb.mnavi.utility;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmResults;
import cx.mb.mnavi.R;
import cx.mb.mnavi.realm.Item;

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

        final String uuid = context.getString(R.string.beacon_proximity_uuid).toUpperCase();

        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                final RealmResults<Item> items = realm.where(Item.class).findAll();
                items.deleteAllFromRealm();

                final Item one = realm.createObject(Item.class, 1L);
                one.setTitle("浅い海・深い海");
                one.setBody("異なる種類なのに、似ている生物。同じ仲間なのに、まったく違う姿の生物。");
                one.setUuid(uuid);
                one.setMajor(1);
                one.setMinor(1);
                one.setActive(false);

                final Item two = realm.createObject(Item.class, 2L);
                two.setTitle("ヘンテコ生き物");
                two.setBody("動きがヘンテコ、形がヘンテコ、生態がヘンテコ…。面白い生物を世界中から集めました。");
                two.setUuid(uuid);
                two.setMajor(1);
                two.setMinor(2);
                two.setActive(false);

                final Item thr = realm.createObject(Item.class, 3L);
                thr.setTitle("光の海");
                thr.setBody("光の届かない深い海に生きる宝石サンゴ。石垣島からやってきた赤ちゃんサンゴ。");
                thr.setUuid(uuid);
                thr.setMajor(1);
                thr.setMinor(3);
                thr.setActive(false);

                final Item fur = realm.createObject(Item.class, 4L);
                fur.setTitle("深い海");
                fur.setBody("深海生物には、怖い？気持ち悪い？といったイメージが多いようです。ここでは可愛らしいものや、チャーミングなものなど、ユニークな深海生物をご紹介しています。");
                fur.setUuid(uuid);
                fur.setMajor(1);
                fur.setMinor(4);
                fur.setActive(false);

                final Item fiv = realm.createObject(Item.class, 5L);
                fiv.setTitle("駿河湾");
                fiv.setBody("日本一深い湾「駿河湾」には沿岸から深海まで様々な生物が生息しています。この水槽では、代表的な駿河湾の生物たちを紹介しています。");
                fiv.setUuid(uuid);
                fiv.setMajor(1);
                fiv.setMinor(5);
                fiv.setActive(false);
            }
        });
        realm.close();
    }

}
