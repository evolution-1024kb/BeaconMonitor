package mb.cx.mnavi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import mb.cx.mnavi.R;
import mb.cx.mnavi.realm.Item;
import trikita.log.Log;

/**
 * 展示物一覧用アダプタ
 * Created by toshiaki on 2017/01/29.
 */
public class NearItemsAdapter extends RealmBaseAdapter<Item> implements ListAdapter {

    /**
     * ViewHolder
     */
    private static class ViewHolder {
        /**
         * タイトル
         */
        TextView title;

        /**
         * 本文
         */
        TextView body;
    }

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param data    データ
     */
    public NearItemsAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Item> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.near_items_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.body = (TextView) convertView.findViewById(R.id.item_body);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Item item = adapterData.get(position);
        viewHolder.title.setText(item.getTitle());
        viewHolder.body.setText(item.getBody());

        if (item.getActive()) {
            viewHolder.body.setTextColor(Color.rgb(255, 0, 0));
        } else {
            viewHolder.body.setTextColor(viewHolder.title.getCurrentTextColor());
        }
        return convertView;


    }
}
