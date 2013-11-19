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

    /**
     * General constructor
     *
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param objects
     */
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
            itemView = this.inflater.inflate(R.layout.stickyview, parent, false);

            holder = new ViewHolder();

            holder.text1 = (TextView)itemView.findViewById(R.id.id_textview);
            holder.text2 = (TextView)itemView.findViewById(R.id.memo_textview);
            holder.text3 = (TextView)itemView.findViewById(R.id.url_textview);

            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }
        
        String userID = sticky.getUserID();
        if (userID.length() >= 20) {
			userID = userID.substring(0, 20) + "...";
		}
        
		String memo = sticky.getMemo();
		//Log.i("naheon", "***" + memo);
		if (memo.length() >= maxLength) {
			memo = memo.substring(0, maxLength) + "...";
		}
		
		String url = sticky.getURL();
		//Log.i("naheon", "***" + url);
		if (url.length() >= maxLength) {
			url = url.substring(0, maxLength) + "...";
		}
		
        holder.text1.setText(userID);
        holder.text2.setText(memo);
        holder.text3.setText(url);

        return itemView;
    }
}
