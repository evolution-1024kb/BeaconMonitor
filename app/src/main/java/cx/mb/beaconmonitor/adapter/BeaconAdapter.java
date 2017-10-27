package cx.mb.beaconmonitor.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import cx.mb.beaconmonitor.R;
import cx.mb.beaconmonitor.realm.BeaconItem;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Adapter of beacons.
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconAdapter extends RealmBaseAdapter<BeaconItem> implements ListAdapter {

    /**
     * context.
     */
    private Context context;

    /**
     * Constructor.
     *
     * @param context context.
     * @param data    data.
     */
    public BeaconAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<BeaconItem> data) {
        super(data);
        this.context = context;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.near_items_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.uuid = view.findViewById(R.id.item_uuid);
            viewHolder.major = view.findViewById(R.id.item_major);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        assert adapterData != null;
        final BeaconItem beacon = adapterData.get(position);
        final String major_minor_format = context.getString(R.string.major_minor_format, beacon.getMajor(), beacon.getMinor());

        final int color;
        if (beacon.getStatus().isDetection()) {
            // active
            color = this.context.getColor(R.color.beacon_active);
        } else {
            // negative
            color = this.context.getColor(R.color.beacon_negative);
        }

        viewHolder.uuid.setTextColor(color);
        viewHolder.major.setTextColor(color);

        viewHolder.uuid.setText(beacon.getUuid());
        viewHolder.major.setText(major_minor_format);

        return view;
    }

    /**
     * ViewHolder.
     */
    private static class ViewHolder {
        TextView uuid;
        TextView major;
    }
}
