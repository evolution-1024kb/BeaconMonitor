package cx.mb.monitor.realm;

import io.realm.RealmObject;
import lombok.Getter;
import lombok.Setter;

/**
 * ビーコンの状態を表すクラス
 * Created by toshiaki on 2017/04/25.
 */
@Setter
@Getter
public class BeaconStatus extends RealmObject {
    /**
     * ビーコンへのバックリンク
     */
    private BeaconItem owner;

    /**
     * 検出しているかどうか
     */
    private boolean detection;
}
