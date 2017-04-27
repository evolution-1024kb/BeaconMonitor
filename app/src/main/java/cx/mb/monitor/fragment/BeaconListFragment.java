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
import cx.mb.monitor.adapter.BeaconAdapter;
import cx.mb.monitor.realm.BeaconHistory;
import cx.mb.monitor.realm.BeaconItem;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * A fragment of beacon list.
 */
public class BeaconListFragment extends Fragment {

    /**
     * UUID edit text.
     */
    @BindView(R.id.beacon_list_text_uuid)
    EditText editUuid;

    /**
     * Clear button.
     */
    @BindView(R.id.beacon_list_button_clear)
    Button buttonClear;

    /**
     * Filter button.
     */
    @BindView(R.id.beacon_list_button_filter)
    Button buttonFilter;

    /**
     * list of beacons.
     */
    @BindView(R.id.beacon_list_list_beacons)
    ListView listBeacons;

    /**
     * Realm
     */
    private Realm realm;

    /**
     * ButterKnife un binder.
     */
    private Unbinder unBinder;

    /**
     * Constructor
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

        // initialize ListView
        final RealmResults<BeaconItem> beacons = realm.where(BeaconItem.class).findAll();
        final BeaconAdapter adapter = new BeaconAdapter(getActivity(), beacons);
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
     * Clear button click.
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.beacon_list_button_clear)
    public void onClearClick() {

        editUuid.setText("");

        final RealmResults<BeaconItem> results = realm.where(BeaconItem.class).findAll();
        final BeaconAdapter adapter = new BeaconAdapter(getActivity(), results);
        this.listBeacons.setAdapter(adapter);
    }

    /**
     * Filter button click.
     */
    @SuppressWarnings("unused")
    @OnClick(R.id.beacon_list_button_filter)
    public void onFilterClick() {

        final String uuid = editUuid.getText().toString();
        if (uuid.equals("")) {
            Toast.makeText(getActivity(), getString(R.string.error_uuid_is_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        final RealmResults<BeaconItem> results = realm
                .where(BeaconItem.class)
                .contains("uuid", uuid)
                .findAllSorted("detectAt", Sort.DESCENDING);
        final BeaconAdapter adapter = new BeaconAdapter(getActivity(), results);
        this.listBeacons.setAdapter(adapter);
    }
}
