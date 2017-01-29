package mb.cx.mnavi.realm;

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
public class Item extends RealmObject {

    /**
     * ID
     */
    @PrimaryKey
    private long id;

    /**
     * タイトル
     */
    @Required
    private String title;

    /**
     * 本文
     */
    private String body;

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
     * ビーコン検出中かどうか
     */
    @Required
    private Boolean active;
}
