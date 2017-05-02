package cx.mb.beaconmonitor.service;

import com.github.mikephil.charting.data.LineData;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * LineData and Date list.
 * Created by toshiaki on 2017/05/02.
 */
@Setter
@Getter
public class LineDataContainer {

    /**
     * Plot data.
     */
    private LineData lineData;

    /**
     * Plot dates.
     */
    private List<Date> dates;
}
