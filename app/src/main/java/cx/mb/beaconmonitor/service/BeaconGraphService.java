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

import org.altbeacon.beacon.Beacon;
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
import trikita.log.Log;

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
     *
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
     * @param now       Current datetime.
     * @param threshold oldest datetime.
     * @param uuid      UUID
     * @param major     MAJOR
     * @param minor     MINOR
     * @return line data.
     */
    public LineDataContainer createLineData(Date now, Date threshold, String uuid, int major, int minor) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();

            final ArrayList<Date> dates = new ArrayList<>();

            Calendar cal = Calendar.getInstance();
            cal.setTime(threshold);
            while (now.compareTo(cal.getTime()) > 0) {
                final Date truncate = DateUtils.truncate(cal.getTime(), Calendar.SECOND);
                dates.add(truncate);
                cal.add(Calendar.SECOND, 1);
            }

            List<Entry> rssiEntries = new ArrayList<>();
            List<Entry> distanceEntries = new ArrayList<>();

            int x = 0;
            for (Date date : dates) {
                final BeaconHistory data = realm.where(BeaconHistory.class)
                        .equalTo("owner.uuid", uuid)
                        .equalTo("owner.major", major)
                        .equalTo("owner.minor", minor)
                        .equalTo("detectAt", date)
                        .findFirst();

                final float rssi = data != null ? data.getRssi() : Float.NaN;
                final float distance = data != null ? (float) data.getDistance() : Float.NaN;

                rssiEntries.add(new Entry(x, rssi));
                distanceEntries.add(new Entry(x, distance));
                x++;
            }

            if (rssiEntries.size() == 0 || distanceEntries.size() == 0) {
                rssiEntries.add(new Entry(0, 0));
                distanceEntries.add(new Entry(0, 0));
            }

            final LineDataSet rssiDataSet = new LineDataSet(rssiEntries, "RSSI");
            rssiDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
            rssiDataSet.setCircleColor(Color.BLUE);
            rssiDataSet.setDrawCircleHole(false);
            rssiDataSet.setColor(Color.BLUE);

            final LineDataSet distanceLineDataSet = new LineDataSet(distanceEntries, "DISTANCE");
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
