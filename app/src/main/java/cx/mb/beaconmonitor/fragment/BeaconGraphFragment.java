package cx.mb.beaconmonitor.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.apache.commons.lang3.time.DateUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cx.mb.beaconmonitor.R;
import cx.mb.beaconmonitor.chart.CircleOnlyRenderer;
import cx.mb.beaconmonitor.event.BeaconSelectEvent;
import cx.mb.beaconmonitor.chart.YAxisLabelFormatter;
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

    /**
     * Graph refresh handler.
     */
    private Handler refreshHandler = null;
    private String uuid;
    private int major;
    private int minor;

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
        refreshHandler = new Handler();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        refreshHandler.removeCallbacksAndMessages(null);
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

        uuid = event.getUuid();
        major = event.getMajor();
        minor = event.getMinor();

        createSampleChart(uuid, major, minor);
        final int delay = getResources().getInteger(R.integer.beacon_list_refresh_interval);

        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                createSampleChart(uuid, major, minor);
                refreshHandler.postDelayed(this, delay);
            }
        }, delay);
    }

    private void initChart() {

        chart.setNoDataText(getString(R.string.beacon_list_no_data));
    }

    private Date createThreshold(Date now) {

        final int timeSpan = getResources().getInteger(R.integer.beacon_list_time_span_minutes);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, -1 * timeSpan);

        return DateUtils.truncate(calendar.getTime(), Calendar.MILLISECOND);
    }

    private void createSampleChart(final String uuid, final int major, final int minor) {

        final Date now = DateUtils.truncate(new Date(), Calendar.MILLISECOND);
        final Date threshold = createThreshold(now);

        Log.d("NOW:", now, "THRESHOLD:", threshold);
        final RealmResults<BeaconHistory> results = realm.where(BeaconHistory.class)
                .equalTo("owner.uuid", uuid)
                .equalTo("owner.major", major)
                .equalTo("owner.minor", minor)
                .findAllSorted("detectAt", Sort.ASCENDING);

        List<Entry> rssiEntries = new ArrayList<>();
        List<Entry> distanceEntries = new ArrayList<>();

        final ArrayList<Date> dates = new ArrayList<>();
        int x = 0;
        for (BeaconHistory data : results) {
            dates.add(data.getDetectAt());
            rssiEntries.add(new Entry(x, data.getRssi()));
            distanceEntries.add(new Entry(x, (float) data.getDistance()));
            x++;
        }


        final LineDataSet rssiDataSet = new LineDataSet(rssiEntries, "RSSI:");
        rssiDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        final LineDataSet distanceLineDataSet = new LineDataSet(distanceEntries, "DISTANCE:");
        distanceLineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        distanceLineDataSet.setCircleColor(Color.RED);
        distanceLineDataSet.setDrawCircleHole(false);
        distanceLineDataSet.setColor(Color.RED);
        final ViewPortHandler viewPortHandler = chart.getViewPortHandler();
        final ChartAnimator animator = chart.getAnimator();

        final XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new YAxisLabelFormatter(dates.toArray(new Date[]{})));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.RED);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);


        final LineData lineData = new LineData(rssiDataSet, distanceLineDataSet);

        chart.setData(lineData);
        chart.setRenderer(new CircleOnlyRenderer(chart, animator, viewPortHandler));
        chart.notifyDataSetChanged();
        chart.invalidate();
    }
}
