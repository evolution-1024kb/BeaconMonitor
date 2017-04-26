package cx.mb.monitor.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

/**
 * 検出したビーコン一件を表すクラス
 * Created by toshiaki on 2017/01/22.
 */
@Getter
@Setter
public class BeaconItem extends RealmObject {

    /**
     * PK(uuid_major_minor)
     */
    @PrimaryKey
    private String id;

    /**
     * Proximity UUID
     */
    @Required
    private String uuid;

    /**
     * Major
     */
    private int major;

    /**
     * Minor
     */
    private int minor;

    /**
     * ビーコンの状態を表す
     */
    private BeaconStatus status;

    /**
     * 検出履歴一覧
     */
    private RealmList<BeaconHistory> histories;
}
