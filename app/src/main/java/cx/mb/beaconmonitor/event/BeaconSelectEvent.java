package cx.mb.beaconmonitor.event;

import lombok.Getter;
import lombok.Setter;

/**
 * Item click event on Beacon List.
 * Created by toshiaki on 2017/04/28.
 */
@Setter
@Getter
public class BeaconSelectEvent {

    /**
     * uuid.
     */
    private String uuid;

    /**
     * major.
     */
    private int major;

    /**
     * minor.
     */
    private int minor;
}
