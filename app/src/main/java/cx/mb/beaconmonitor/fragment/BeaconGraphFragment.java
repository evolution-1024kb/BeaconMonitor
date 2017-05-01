package cx.mb.beaconmonitor.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cx.mb.beaconmonitor.R;
import cx.mb.beaconmonitor.event.BeaconSelectEvent;
import cx.mb.beaconmonitor.formatter.YAxisLabelFormatter;
import cx.mb.beaconmonitor.realm.BeaconHistory;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import trikita.log.Log;

/**
 * Graph fragment.
 */
public class BeaconGraphFragment extends Fragment {
    /**
     * ButterKnife un binder.
     */
    private Unbinder unBinder;

    /**
     * Line chart.
     */
    @BindView(R.id.beacon_graph_chart)
    LineChart chart;

    /**
     * Realm instance.
     */
    private Realm realm;
//    /**
//     * Selected uuid.
//     */
//    private String uuid;
//
//    /**
//     * Selected major.
//     */
//    private int major;
//
//    /**
//     * Selected minor.
//     */
//    private int minor;

    public BeaconGraphFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_beacon_graph, container, false);
        unBinder = ButterKnife.bind(this, view);
        realm = Realm.getDefaultInstance();

        initChart();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
        unBinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    /**
     * Receive beacon selected event.
     *
     * @param event event object.
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBeaconSelect(BeaconSelectEvent event) {
        Log.d("UUID:%s, MAJOR:%d, MINOR:%d", event.getUuid(), event.getMajor(), event.getMinor());

        final String uuid = event.getUuid();
        final int major = event.getMajor();
        final int minor = event.getMinor();

        createSampleChart(uuid, major, minor);
    }

    private void initChart() {

//        YourData[] dataObjects = new YourData[10];
//        dataObjects[0] = new YourData(1, 1);
//        dataObjects[1] = new YourData(2, 8);
//        dataObjects[2] = new YourData(3, 2.6f);
//        dataObjects[3] = new YourData(4, 4);
//        dataObjects[4] = new YourData(5, 9);
//        dataObjects[5] = new YourData(6, 10);
//        dataObjects[6] = new YourData(7, -4);
//        dataObjects[7] = new YourData(8, 0);
//        dataObjects[8] = new YourData(9, 9);
//        dataObjects[9] = new YourData(10, 10);
//
//        List<Entry> entries = new ArrayList<Entry>();
//        for (YourData data : dataObjects) {
//            entries.add(new Entry(data.getValueX(), data.getValueY()));
//        }
//
//        final LineDataSet dataSet = new LineDataSet(entries, "Label");
//        final LineData lineData = new LineData(dataSet);
//        chart.setData(lineData);
//        chart.invalidate();
    }

    private void createSampleChart(final String uuid, final int major, final int minor) {

        final RealmResults<BeaconHistory> results = realm.where(BeaconHistory.class)
                .equalTo("owner.uuid", uuid)
                .equalTo("owner.major", major)
                .equalTo("owner.minor", minor)
                .findAllSorted("detectAt", Sort.ASCENDING);

        List<Entry> entries = new ArrayList<Entry>();

        final ArrayList<Date> dates = new ArrayList<>();
        int x = 0;
        for (BeaconHistory data : results) {
            Log.d("TIME:", data.getDetectAt(), "RSSI:", data.getRssi());

            dates.add(data.getDetectAt());
            entries.add(new Entry(x, data.getRssi()));
            x++;
        }

        final XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.RED);
        xAxis.setValueFormatter(new YAxisLabelFormatter(dates.toArray(new Date[]{})));

        final LineDataSet dataSet = new LineDataSet(entries, "RSSI:");
        final LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }
}
