package cx.mb.monitor.service;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * A service of MainActivity.
 * Created by toshiaki on 2017/04/21.
 */

public class MainService {

    /**
     * Permission request code.
     */
    public static final int REQUEST_PERMISSIONS = 200;
    /**
     * Bluetooth enable request code.
     */
    private static final int REQUEST_ENABLE_BLUETOOTH = 100;
    /**
     * Owner activity.
     */
    private final Activity owner;

    /**
     * Constructor.
     *
     * @param activity Owner Activity.
     */
    public MainService(Activity activity) {
        this.owner = activity;
    }

    /**
     * Is bluetooth enabled?
     *
     * @return true:Yes / false:No
     */
    public boolean isBluetoothEnabled() {

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return false;
        }
        return adapter.isEnabled();
    }

    /**
     * Bluetooth enable request.
     */
    public void requestBluetooth() {
        Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        owner.startActivityForResult(btOn, REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Find lacked permissions.
     *
     * @return list of lacked permission.
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
     * Lacked permissions grant request.
     *
     * @param lackedPermissions list of lacked permissions.
     */
    public void requestPermissions(List<String> lackedPermissions) {
        owner.requestPermissions(lackedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS);
    }

    /**
     * Check required permissions.
     *
     * @return true:Yes / false:No
     */
    public boolean hasAllPermissions() {
        return getLackedPermissions().isEmpty();
    }
}
