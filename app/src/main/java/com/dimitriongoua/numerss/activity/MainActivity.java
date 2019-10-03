package com.dimitriongoua.numerss.activity;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dimitriongoua.numerss.R;
import com.dimitriongoua.numerss.adapter.FeedListAdapter;
import com.dimitriongoua.numerss.model.FeedItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView listView;
    private ProgressBar pb_feed;
    private SwipeRefreshLayout srl_feed;
    private FeedListAdapter listAdapter;
    private List<FeedItem> feedItems;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;
    private String URL_FEED = "https://www.numerama.com/feed/";
    // Yep

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        pb_feed  = findViewById(R.id.pb_feed);
        srl_feed = findViewById(R.id.srl_feed);

        feedItems = new ArrayList<FeedItem>();

        listAdapter = new FeedListAdapter(this, feedItems);
        listView.setAdapter(listAdapter);

        srl_feed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });

        new FetchFeedTask().execute((Void) null);
    }

    public void parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        String pubDate = null;
        String image = null;
        String author = null;
        boolean isItem = false;
        feedItems.clear();
        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                } else if (name.equalsIgnoreCase("dc:creator")) {
                    author = result;
                } else if (name.equalsIgnoreCase("pubdate")) {
                    pubDate = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        String[] parts = description.substring(0, description.indexOf("</p>")).split(" ");
                        image = parts[3];
                        image = image.replace("src=\"", "");
                        image = image.replace("\"", "");
                        Log.e(TAG, "Image : " + image);
                        description = description.substring(description.indexOf("</p>"), description.indexOf("<a "));
                        description = description.replace("</p>", "");
                        description = description.replace("&nbsp;", "");
                        Log.e(TAG, "Description : " + description);
                        Log.e(TAG, "Titre : " + title);
                        FeedItem item = new FeedItem(
                                title,
                                link,
                                pubDate,
                                author,
                                image,
                                description);
                        feedItems.add(item);
                    }
                    else {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

        } finally {
            inputStream.close();
        }
    }

    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {

        private String urlLink;

        @Override
        protected void onPreExecute() {
            srl_feed.setRefreshing(true);
            urlLink = URL_FEED;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (TextUtils.isEmpty(urlLink))
                return false;

            try {
                if(!urlLink.startsWith("http://") && !urlLink.startsWith("https://"))
                    urlLink = "http://" + urlLink;

                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                parseFeed(inputStream);
                return true;
            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            srl_feed.setRefreshing(false);

            if (success) {
                listAdapter.notifyDataSetChanged();
                pb_feed.setVisibility(View.GONE);
                srl_feed.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(MainActivity.this, "Veuillez réessayer plus tard", Toast.LENGTH_LONG).show();
                pb_feed.setVisibility(View.GONE);
            }
        }
    }
}
