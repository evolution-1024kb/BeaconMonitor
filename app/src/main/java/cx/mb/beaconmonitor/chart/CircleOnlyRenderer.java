package cx.mb.beaconmonitor.chart;

import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Custom renderer (no lines.)
 * Created by toshiaki on 2017/05/01.
 */

public class CircleOnlyRenderer extends LineChartRenderer {
    /**
     * Constructor
     *
     * @param chart           chart.
     * @param animator        animator.
     * @param viewPortHandler viewPortHandler.
     */
    public CircleOnlyRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    @Override
    protected void drawLinear(Canvas c, ILineDataSet dataSet) {
        // do nothing.
    }
}
