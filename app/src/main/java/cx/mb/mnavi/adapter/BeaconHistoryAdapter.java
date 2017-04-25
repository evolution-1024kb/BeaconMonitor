package cx.mb.mnavi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Locale;

import cx.mb.mnavi.R;
import cx.mb.mnavi.realm.BeaconHistory;
import cx.mb.mnavi.realm.BeaconItem;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * 一覧用アダプタ
 * Created by toshiaki on 2017/01/29.
 */
public class BeaconHistoryAdapter extends RealmBaseAdapter<BeaconHistory> implements ListAdapter {

    /**
     * ViewHolder
     */
    private static class ViewHolder {
        TextView uuid;
    }

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param data    データ
     */
    public BeaconHistoryAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<BeaconHistory> data) {
        super(data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.near_items_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.uuid = (TextView) convertView.findViewById(R.id.item_uuid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        assert adapterData != null;
        final BeaconHistory beacon = adapterData.get(position);

        final String date = DateFormat.format("kk:mm:ss", beacon.getScanAt()).toString();
        final String v = String.format(Locale.US, "UUID:%s, ScanAt:%s - 距離:%f", beacon.getOwner().getUuid(), date, beacon.getDistance());
        viewHolder.uuid.setText(v);

        return convertView;
    }
}
