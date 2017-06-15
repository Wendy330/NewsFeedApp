package layout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yqian.project.MainActivity;
import com.yqian.project.R;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class OneFragment extends Fragment {

    private final String MUSIC_URL_ET = "http://feeds.etonline.com/ETMusic";

    String dataFromActivity = "";

    private String feedUrl = "";
    private URL feedURL;
    private BufferedReader bufferedReader;

    private ArrayList<Post> posts;
    private ListView listView;
    private PostAdapter postAdapter;

    private ArrayList<String> title = new ArrayList<>();
    private ArrayList<String> pubDate = new ArrayList<>();
    private ArrayList<String> link = new ArrayList<>();
    private ArrayList<String> artist = new ArrayList<>();

    private StringBuilder titleString;
    private StringBuilder pubDateString;
    private StringBuilder linkString;
    private StringBuilder artistString;

    private boolean inTitle;
    private boolean inPubDate;
    private boolean inLink;
    private boolean inArtist;

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);

        listView = (ListView) view.findViewById(R.id.list_view_one);

        return view;
    }



    class ETHandler extends DefaultHandler {
        public ETHandler(){
            title = new ArrayList<>(11);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            Log.d("Wendy", "startElement: " + qName);

            if (qName.equals("title")){
                titleString = new StringBuilder(50);
                inTitle = true;
            }

            if (dataFromActivity == MUSIC_URL_ET){
                if (qName.equals("pubDate")){
                    pubDateString = new StringBuilder(30);
                    inPubDate = true;
                }
            }
            else{
                if (qName.equals("im:artist")){
                    artistString = new StringBuilder(30);
                    inArtist = true;
                }
            }

            if (qName.equals("feedburner:origLink")) {
                linkString = new StringBuilder(300);
                inLink = true;
            }

        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            Log.d("Wendy", "endElement: " + qName);

            if (qName.equals("title")){
                inTitle = false;
                title.add(titleString.toString());
            }

            if (dataFromActivity == MUSIC_URL_ET){
                if (qName.equals("pubDate")){
                    inPubDate = false;
                    pubDate.add(pubDateString.toString());
                }
            }
            else{
                if (qName.equals("im:artist")){
                    inArtist = false;
                    artist.add(artistString.toString());
                }
            }

            if (qName.equals("feedburner:origLink")) {
                inLink = false;
                link.add(linkString.toString());
            }


        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);

            String s = new String(ch, start, length);

            Log.d("Wendy", "Characters: " + s);

            if (inTitle){
                titleString.append(ch, start, length);
            }

            if (dataFromActivity == MUSIC_URL_ET){
                if (inPubDate){
                    pubDateString.append(ch, start, length);
                }
            }
            else{
                if (inArtist){
                    artistString.append(ch, start, length);
                }
            }

            if (inLink){
                linkString.append(ch, start, length);
            }
        }
    }

    class ReadRRS extends AsyncTask {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("Wendy", "onPreExecute");
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.d("Wendy", "doInBackground");

            feedUrl = dataFromActivity;

            try {
                SAXParserFactory spf = SAXParserFactory.newInstance();
                feedURL = new URL( feedUrl);

                SAXParser saxParser = spf.newSAXParser();

                ETHandler etHandler = new ETHandler();

                bufferedReader = new BufferedReader(
                        new InputStreamReader(feedURL.openStream()));

                saxParser.parse(new InputSource(bufferedReader), etHandler);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d("Wendy", "onPostExecute");

            makePost(11);

            postAdapter = new PostAdapter(getActivity(), R.layout.list_item, posts);

            listView.setAdapter(postAdapter);
        }
    }

    class Post{
        private String title;
        private String pubDate;

        public Post(String title, String pubDate){
            this.title = title;
            this.pubDate = pubDate;
        }

        public String getTitle(){
            return title;
        }
        public String getPubDate(){
            return pubDate;
        }
    }

    private void makePost(int items){
        posts = new ArrayList<>(11);

        if (dataFromActivity == MUSIC_URL_ET){
            for (int i = 2; i < items; i++) {
                posts.add(new Post(title.get(i), pubDate.get(i - 1)));
            }
        }
        else{
            for (int i = 1; i < items; i++) {
                posts.add(new Post(title.get(i), artist.get(i - 1)));
            }
        }
    }

    private class PostAdapter extends ArrayAdapter<Post> {
        private ArrayList<Post> items;

        public PostAdapter(Context context, int textViewResourceId, ArrayList<Post> items){
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null){
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            Post o = items.get(position);
            if (o != null){
                TextView tt = (TextView)v.findViewById(R.id.txtTop);
                TextView bt = (TextView)v.findViewById(R.id.txtBottom);
                if (tt != null){
                    tt.setText("Title: " + o.getTitle());
                }
                if (bt != null){
                    if (dataFromActivity == MUSIC_URL_ET){
                        bt.setText("Publish Date: " + o.getPubDate());
                    }
                    else{
                        bt.setText("Artist: " + o.getPubDate());
                    }
                }
            }
            return v;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ReadRRS readRRS = new ReadRRS();
        readRRS.execute();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (dataFromActivity == MUSIC_URL_ET) {
                    Uri uri = Uri.parse(link.get(i));

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        MainActivity mainActivity = (MainActivity)getActivity();

        dataFromActivity = mainActivity.getPassMusicUrl();

        if (dataFromActivity.isEmpty()){
            dataFromActivity = MUSIC_URL_ET;
        }

        feedUrl = dataFromActivity;
    }

}
