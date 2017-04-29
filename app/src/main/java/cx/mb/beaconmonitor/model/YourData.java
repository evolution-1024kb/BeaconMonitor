package cx.mb.beaconmonitor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Dummy data.
 * Created by toshiaki on 2017/04/29.
 */
@Getter
@Setter
public class YourData {

    private float valueX;
    private float valueY;

    public YourData(float x, float y) {
        this.valueX = x;
        this.valueY = y;
    }
}
