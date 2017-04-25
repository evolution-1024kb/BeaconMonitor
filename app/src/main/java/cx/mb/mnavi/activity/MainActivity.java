package cx.mb.mnavi.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cx.mb.mnavi.R;
import cx.mb.mnavi.service.MainService;
import trikita.log.Log;

/**
 * メインアクティビティ
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 検出画面リクエスト
     */
    private static final int GO_SCAN = 1000;

    /**
     * スキャンボタン
     */
    @BindView(R.id.main_btn_start)
    Button btnScan;

    /**
     * UUIDテキストエディット
     */
    @BindView(R.id.main_edit_uuid)
    EditText editUuid;

    /**
     * クリアボタン
     */
    @BindView(R.id.main_btn_clear)
    Button btnClear;

    /**
     * 画面用サービス
     */
    private MainService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        service = new MainService(this);

        // Bluetooth自体のチェック
        if (!service.isBluetoothEnabled()) {
            service.requestBluetooth();
        } else {
            final List<String> lackedPermissions = service.getLackedPermissions();
            if (!lackedPermissions.isEmpty()) {
                service.requestPermissions(lackedPermissions);
            }
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_btn_start)
    void onClickButton(Button btn) {

        if (!service.hasAllPermissions()) {
            Toast.makeText(this, getString(R.string.error_permissions), Toast.LENGTH_SHORT).show();
            return;
        }

        final String uuid = editUuid.getText().toString();
        final Intent intent = ScanActivity.createIntent(this, uuid);
        startActivityForResult(intent, GO_SCAN);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.main_btn_clear)
    void onBtnClearClick() {
        editUuid.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainService.REQUEST_ENABLE_BLUETOOTH) {
            Log.i("resultCode is RESULT_OK ? :" + String.valueOf(resultCode == Activity.RESULT_OK));
        } else if (requestCode == GO_SCAN) {
            Log.i("back from scan");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MainService.REQUEST_PERMISSIONS) {

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
}
