package com.dimitriongoua.numerss.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.dimitriongoua.numerss.R;
import com.dimitriongoua.numerss.app.NumeRSS;
import com.dimitriongoua.numerss.model.FeedItem;
import com.dimitriongoua.numerss.ui.FeedImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FeedListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    ImageLoader imageLoader = NumeRSS.getInstance().getImageLoader();

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_feed, null);

        if (imageLoader == null)
            imageLoader = NumeRSS.getInstance().getImageLoader();

        TextView title      = convertView.findViewById(R.id.tv_title);
        TextView timestamp  = convertView.findViewById(R.id.tv_timestamp);
        TextView author     = convertView.findViewById(R.id.tv_author);
        TextView desc       = convertView.findViewById(R.id.tv_description);
        FeedImageView image = convertView.findViewById(R.id.fiv_image);

        FeedItem item = feedItems.get(position);

        String authorText = "Par " + item.getAuthor();
        author.setText(authorText);
        title.setText(item.getTitle());
        desc.setText(item.getDescription());
        timestamp.setText(getDate(item.getTimeStamp(), "dd/MM/y à HH:mm"));

        // Feed image
        if (item.getImage() != null) {
            image.setImageUrl(item.getImage(), imageLoader);
            image.setVisibility(View.VISIBLE);
            image
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            image.setVisibility(View.GONE);
        }

        return convertView;
    }

    public String getDate(String datestr, String pattern) {

        SimpleDateFormat formatter = new SimpleDateFormat("E, d MMM y HH:mm:ss Z");
        String d = "";

        try {
            Date date = formatter.parse(datestr);

            SimpleDateFormat format = new SimpleDateFormat(pattern);
            d = format.format(date);
            d = d.replace("Mon", "lundi");
            d = d.replace("Tue", "mardi");
            d = d.replace("Wed", "mercredi");
            d = d.replace("Thu", "jeudi");
            d = d.replace("Fri", "vendredi");
            d = d.replace("Sat", "samedi");
            d = d.replace("Sun", "dimanche");
            //
            d = d.replace("lun.", "Lundi");
            d = d.replace("mar.", "Mardi");
            d = d.replace("mer.", "Mercredi");
            d = d.replace("jeu.", "Jeudi");
            d = d.replace("ven.", "Vendredi");
            d = d.replace("sam.", "Samedi");
            d = d.replace("dim.", "Dimanche");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Pretty date
        // Today
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/y");
        Date date = new Date();
        String today = dateFormat.format(date);
        d = d.replace(today, "Aujourd'hui");

        // Yesterday
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = dateFormat.format(cal.getTime());
        d = d.replace(yesterday, "Hier");

        return d;
    }

}