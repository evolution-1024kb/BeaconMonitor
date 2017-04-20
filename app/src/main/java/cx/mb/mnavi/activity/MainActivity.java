package cx.mb.mnavi.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cx.mb.mnavi.R;
import io.realm.Realm;
import trikita.log.Log;

/**
 * メインアクティビティ
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 権限リクエスト
     */
    private static final int REQUEST_PERMISSIONS = 100;

    /**
     * Bluetooth有効化リクエスト
     */
    private static final int REQUEST_ENABLE_BLUETOOTH = 200;
    private static final int GO_SCAN = 1000;

    /**
     * Realmインスタンス
     */
    private Realm realm;

    @BindView(R.id.btn_start)
    Button btnScan;

    @BindView(R.id.txt_uuid)
    EditText editUuid;

    @BindView(R.id.edit_period)
    EditText editPeriod;

    @BindView(R.id.edit_between)
    EditText editBetween;

    @BindView(R.id.btn_clear)
    Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        // Bluetooth自体のチェック
        final boolean isBluetoothEnabled = isBluetoothEnabled();
        if (isBluetoothEnabled) {
            final boolean granted = isGrantedAllPermissions();
            Log.i("isGrantedAll ? :" + String.valueOf(granted));
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_start)
    void onClickButton(Button btn) {

        final String uuid = editUuid.getText().toString();
        final int period = Integer.parseInt(editPeriod.getText().toString());
        final int betweenPeriod = Integer.parseInt(editBetween.getText().toString());
        final Intent intent = ScanActivity.createIntent(this, uuid, period, betweenPeriod);
        startActivityForResult(intent, GO_SCAN);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.btn_clear)
    void onBtnClearClick() {
        editUuid.setText("");
    }

    /**
     * Bluetoothが使えるかどうか
     *
     * @return 有効ならTRUE
     */
    private boolean isBluetoothEnabled() {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOn, REQUEST_ENABLE_BLUETOOTH);
            return false;
        }

        return true;
    }

    /**
     * 位置情報およびBluetoothが使用出来るか
     *
     * @return 全て使用可能ならTRUE
     */
    private boolean isGrantedAllPermissions() {

        final List<String> permissions = new ArrayList<>();
        if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (this.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH);
        }
        if (this.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN);
        }

        if (!permissions.isEmpty()) {
            requestPermissions(permissions.toArray(new String[0]), REQUEST_PERMISSIONS);
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            Log.i("resultCode is RESULT_OK ? :" + String.valueOf(resultCode == Activity.RESULT_OK));
        } else if (requestCode == GO_SCAN) {
            Log.i("back from scan");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_PERMISSIONS) {

            boolean grantedAll = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    grantedAll = false;
                    break;
                }
            }

            Log.i("isGrantedAll ? :" + String.valueOf(grantedAll));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
