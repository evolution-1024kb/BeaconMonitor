package cx.mb.mnavi.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.Locale;

import cx.mb.mnavi.R;
import cx.mb.mnavi.realm.Beacon;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * 一覧用アダプタ
 * Created by toshiaki on 2017/01/29.
 */
public class ItemsAdapter extends RealmBaseAdapter<Beacon> implements ListAdapter {

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
    public ItemsAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Beacon> data) {
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
        final Beacon beacon = adapterData.get(position);
        final String v = String.format(Locale.US, "UUID: %s\nMAJOR: %3d\nMINOR: %3d\nRSSI: %d\nDISTANCE: %f", beacon.getUuid(), beacon.getMajor(), beacon.getMinor(), beacon.getRssi(), beacon.getDistance());
        viewHolder.uuid.setText(v);

        return convertView;
    }
}
