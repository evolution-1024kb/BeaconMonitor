package cx.mb.mnavi.service;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity用サービスクラス
 * Created by toshiaki on 2017/04/21.
 */

public class MainService {

    /**
     * Bluetooth利用許可コード
     */
    public static final int REQUEST_ENABLE_BLUETOOTH = 100;

    /**
     * パーミッションリクエストコード
     */
    public static final int REQUEST_PERMISSIONS = 200;

    /**
     * 呼び元アクティビティ
     */
    private final Activity owner;

    /**
     * コンストラクタ
     *
     * @param activity 呼び元
     */
    public MainService(Activity activity) {
        this.owner = activity;
    }

    /**
     * Bluetoothが使えるか
     *
     * @return true:利用可 / false:利用不可
     */
    public boolean isBluetoothEnabled() {

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        return adapter.isEnabled();
    }

    /**
     * Bluetoothの利用要求
     */
    public void requestBluetooth() {
        Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        owner.startActivityForResult(btOn, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * 不足しているパーミッションを取得する
     *
     * @return 不足しているパーミッションの一覧
     */
    public List<String> getLackedPermissions() {

        final List<String> permissions = new ArrayList<>();
        if (owner.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (owner.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH);
        }
        if (owner.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        return permissions;
    }

    /**
     * パーミッション許可リクエスト
     *
     * @param lackedPermissions 不足しているパーミッションの一覧
     */
    public void requestPermissions(List<String> lackedPermissions) {
        owner.requestPermissions(lackedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS);
    }

    /**
     * 必要なパーミッションを持っているか
     *
     * @return true:持っている / false:持っていない
     */
    public boolean hasAllPermissions() {
        return getLackedPermissions().isEmpty();
    }
}
