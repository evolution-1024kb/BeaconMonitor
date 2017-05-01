package cx.mb.beaconmonitor.formatter;

import android.text.format.DateFormat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.Calendar;
import java.util.Date;

import trikita.log.Log;

/**
 * Graph Y Axis label formatter.
 * Created by toshiaki on 2017/05/01.
 */
public class YAxisLabelFormatter implements IAxisValueFormatter {

    /**
     * array of detectAt;
     */
    private final Date[] origin;

    /**
     * Constructor.
     *
     * @param origin array of detectAt.
     */
    public YAxisLabelFormatter(Date[] origin) {
        this.origin = origin;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {

        final String time = DateFormat.format("kk:mm:ss", origin[(int) value]).toString();
//        Log.d("Time:%s", time);
        return time;
    }
}
