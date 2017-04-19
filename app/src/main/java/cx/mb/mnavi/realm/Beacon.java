package cx.mb.mnavi.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

/**
 * アイテム情報
 * Created by toshiaki on 2017/01/22.
 */
@Getter
@Setter
public class Beacon extends RealmObject {

    @PrimaryKey
    private long id;

    @Required
    private String uuid;

    private int major;

    private int minor;

    private int rssi;
    private double distance;
}
