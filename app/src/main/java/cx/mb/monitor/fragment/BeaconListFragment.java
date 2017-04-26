package cx.mb.monitor.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cx.mb.monitor.R;
import cx.mb.monitor.adapter.BeaconHistoryAdapter;
import cx.mb.monitor.realm.BeaconHistory;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * ビーコン一覧用フラグメント
 */
public class BeaconListFragment extends Fragment {

    /**
     * UUIDテキスト
     */
    @BindView(R.id.beacon_list_text_uuid)
    EditText editUuid;

    /**
     * クリアボタン
     */
    @BindView(R.id.beacon_list_button_clear)
    Button buttonClear;

    /**
     * フィルタボタン
     */
    @BindView(R.id.beacon_list_button_filter)
    Button buttonFilter;

    /**
     * ビーコン一覧
     */
    @BindView(R.id.beacon_list_list_beacons)
    ListView listBeacons;

    /**
     * Realm
     */
    private Realm realm;

    /**
     * アンバインダ
     */
    private Unbinder unBinder;

    /**
     * コンストラクタ
     */
    public BeaconListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_beacon_list, container, false);
        unBinder = ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        // ListView初期化
        final RealmResults<BeaconHistory> beacons = realm.where(BeaconHistory.class).findAllSorted("scanAt", Sort.DESCENDING);
        final BeaconHistoryAdapter adapter = new BeaconHistoryAdapter(getActivity(), beacons);
        this.listBeacons.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
        unBinder.unbind();
    }

    /**
     * クリアボタンクリック
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.beacon_list_button_clear)
    public void onClearClick() {

        editUuid.setText("");

        final RealmResults<BeaconHistory> results = realm.where(BeaconHistory.class).findAllSorted("scanAt", Sort.DESCENDING);
        final BeaconHistoryAdapter adapter = new BeaconHistoryAdapter(getActivity(), results);
        this.listBeacons.setAdapter(adapter);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.beacon_list_button_filter)
    public void onFilterClick() {

        final String uuid = editUuid.getText().toString();
        if (uuid.equals("")) {
            Toast.makeText(getActivity(), getString(R.string.error_uuid_is_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        final RealmResults<BeaconHistory> results = realm
                .where(BeaconHistory.class)
                .contains("uuid", uuid)
                .findAllSorted("scanAt", Sort.DESCENDING);
        final BeaconHistoryAdapter adapter = new BeaconHistoryAdapter(getActivity(), results);
        this.listBeacons.setAdapter(adapter);
    }
}
