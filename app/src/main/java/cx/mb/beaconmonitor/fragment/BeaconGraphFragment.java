package cx.mb.beaconmonitor.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.apache.commons.lang3.time.DateUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cx.mb.beaconmonitor.R;
import cx.mb.beaconmonitor.chart.YAxisLabelFormatter;
import cx.mb.beaconmonitor.event.BeaconSelectEvent;
import cx.mb.beaconmonitor.service.BeaconGraphService;
import cx.mb.beaconmonitor.service.LineDataContainer;
import io.realm.Realm;
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

    /**
     * Service class.
     */
    private BeaconGraphService service;

    /**
     * Last selected uuid.
     */
    private String uuid;

    /**
     * Last selected major.
     */
    private int major;

    /**
     * Last selected minor.
     */
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
        service = new BeaconGraphService(getActivity());
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

        updateChart(uuid, major, minor);
        final int delay = getResources().getInteger(R.integer.beacon_list_refresh_interval);

        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateChart(uuid, major, minor);
                refreshHandler.postDelayed(this, delay);
            }
        }, delay);
    }

    /**
     * Initialize chart.
     */
    private void initChart() {

        chart.setNoDataText(getString(R.string.beacon_list_no_data));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawZeroLine(false);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTextColor(Color.RED);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
    }

    /**
     * Update chart data.
     *
     * @param uuid  uuid.
     * @param major major.
     * @param minor minor.
     */
    private void updateChart(final String uuid, final int major, final int minor) {

        final Date now = DateUtils.truncate(new Date(), Calendar.MILLISECOND);
        final Date threshold = service.getThreshold(now);

        Log.d("NOW:", now, "THRESHOLD:", threshold);

        final LineDataContainer container = service.createLineData(now, threshold, uuid, major, minor);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new YAxisLabelFormatter(container.getDates().toArray(new Date[]{})));

        chart.setDrawGridBackground(true);
        chart.setData(container.getLineData());
        chart.notifyDataSetChanged();
        chart.invalidate();
    }
}
