package cx.mb.beaconmonitor.service;

import android.content.Context;
import android.graphics.Color;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cx.mb.beaconmonitor.R;
import cx.mb.beaconmonitor.chart.YAxisLabelFormatter;
import cx.mb.beaconmonitor.realm.BeaconHistory;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Service class for BeaconGraphFragment.
 * Created by toshiaki on 2017/05/02.
 */
public class BeaconGraphService {

    /**
     * Context.
     */
    private Context context;


    /**
     * Constructor.
     *
     * @param context context.
     */
    public BeaconGraphService(Context context) {
        this.context = context;
    }

    /**
     * Get oldest datetime.
     * @param now Current datetime.
     * @return oldest datetime.
     */
    public Date getThreshold(Date now) {

        final int timeSpan = context.getResources().getInteger(R.integer.beacon_list_time_span_minutes);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, -1 * timeSpan);

        return DateUtils.truncate(calendar.getTime(), Calendar.MILLISECOND);
    }

    /**
     * Create line data.
     *
     * @param uuid  UUID
     * @param major MAJOR
     * @param minor MINOR
     * @return line data.
     */
    public LineDataContainer createLineData(String uuid, int major, int minor) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
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

            final LineDataContainer container = new LineDataContainer();
            container.setLineData(new LineData(rssiDataSet, distanceLineDataSet));
            container.setDates(dates);

            return container;
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}
