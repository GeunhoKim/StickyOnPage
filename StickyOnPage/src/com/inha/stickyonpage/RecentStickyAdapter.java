package com.inha.stickyonpage;

import java.util.List;

import com.inha.stickyonpage.db.Sticky;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecentStickyAdapter extends ArrayAdapter<Sticky> {

    private static class ViewHolder {
        private TextView text1;
        private TextView text2;
        private TextView text3;

        ViewHolder() {
            // nothing to do here
        }
    }

    /** Inflater for list items */
    private final LayoutInflater inflater;
    private final int maxLength = 27;

    public RecentStickyAdapter (final Context context,
            final int textViewResourceId,
            final List<Sticky> objects) {
        super(context, textViewResourceId, objects);

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final Sticky sticky = getItem(position);

        if(null == itemView) {
            itemView = this.inflater.inflate(R.layout.recentsticky, parent, false);

            holder = new ViewHolder();

            holder.text1 = (TextView)itemView.findViewById(R.id.id_textview);
            holder.text2 = (TextView)itemView.findViewById(R.id.memo_textview);
            holder.text3 = (TextView)itemView.findViewById(R.id.url_textview);

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }
        
        String userID = sticky.getUserID();
		String memo = sticky.getMemo();		
		String url = sticky.getURL();

        holder.text1.setText(userID);
        holder.text2.setText(memo);
        holder.text3.setText(url);

        return itemView;
    }
}
