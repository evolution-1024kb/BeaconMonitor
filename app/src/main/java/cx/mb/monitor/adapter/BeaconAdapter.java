package cx.mb.monitor.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import cx.mb.monitor.R;
import cx.mb.monitor.realm.BeaconItem;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Adapter of beacons.
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconAdapter extends RealmBaseAdapter<BeaconItem> implements ListAdapter {

    /**
     * String format of major and minor.
     */
    private final String major_minor_format;
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
        major_minor_format = context.getString(R.string.major_minor_format);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.near_items_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.uuid = (TextView) view.findViewById(R.id.item_uuid);
            viewHolder.major = (TextView) view.findViewById(R.id.item_major);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        assert adapterData != null;
        final BeaconItem beacon = adapterData.get(position);
        viewHolder.uuid.setText(beacon.getUuid());
        viewHolder.major.setText(String.format(major_minor_format, beacon.getMajor(), beacon.getMinor()));

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
