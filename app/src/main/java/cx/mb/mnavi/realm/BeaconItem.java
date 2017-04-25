package cx.mb.mnavi.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

/**
 * Beacon.
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
     * 検出履歴一覧
     */
    private RealmList<BeaconHistory> histories;
}
