package com.yqian.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Favourite extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    String[] titles;
    String[] singers;
    String[] images;

    ArrayList<FavouriteAlbums> favouriteAlbumses;
    FavAlbumsAdapter favAlbumsAdapter;

    ListView listView;

//    Switch switch_fav;

    TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        sharedPreferences = getSharedPreferences("FAVOURITE", this.MODE_PRIVATE);

        String titleStirng = sharedPreferences.getString("titleList", "");
        String singerString = sharedPreferences.getString("singerList", "");
        String imageString = sharedPreferences.getString("imageList", "");

        titles = titleStirng.split(",");
        singers = singerString.split(",");
        images = imageString.split(",");

        txtEmpty = (TextView)findViewById(R.id.txtEmpty);

        if (titles[0] != ""){
            addFavourites();

            favAlbumsAdapter = new FavAlbumsAdapter(this, R.layout.list_item_image, favouriteAlbumses);

            listView = (ListView)findViewById(R.id.list_view_favourite);
            listView.setAdapter(favAlbumsAdapter);

            txtEmpty.setVisibility(View.INVISIBLE);
        }
        else{

            txtEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void addFavourites(){
        favouriteAlbumses = new ArrayList<>();

        for (int i = 0; i < titles.length; i++){
            favouriteAlbumses.add(new FavouriteAlbums(titles[i], singers[i], images[i]));
        }

    }

    private class FavouriteAlbums {
        private String title;
        private String singer;
        private String image;

        public FavouriteAlbums(String title, String singer, String image) {
            this.title = title;
            this.singer = singer;
            this.image = image;
        }

        public String getTitle(){
            return title;
        }
        public String getSinger(){
            return singer;
        }
        public String getImage(){return image;}
    }

    private class FavAlbumsAdapter extends ArrayAdapter<FavouriteAlbums>{

        private ArrayList<FavouriteAlbums> items;

        public FavAlbumsAdapter(Context context, int textViewResourceId, ArrayList<FavouriteAlbums> items){
            super(context, textViewResourceId, items);
            this.items = items;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null){
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_image, null);
            }

            final FavouriteAlbums o = items.get(position);

            if (o != null){
                TextView tt = (TextView)v.findViewById(R.id.txtTitle);
                TextView bt = (TextView)v.findViewById(R.id.txtSinger);
                ImageView imageView = (ImageView)v.findViewById(R.id.listview_image);
                Switch switch_fav = (Switch)v.findViewById(R.id.switch_fav);

                if (imageView != null){
                    imageView.setImageResource(Integer.parseInt(o.getImage()));
                }
                if (tt != null){
                    tt.setText(o.getTitle());
                }
                if (bt != null){
                    bt.setText(o.getSinger());
                }

                final SharedPreferences.Editor editor = sharedPreferences.edit();

                switch_fav.setChecked(true);

                switch_fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (!isChecked){
                            items.remove(position);
                            notifyDataSetChanged();

                            String titleStirng = sharedPreferences.getString("titleList", "");
                            String singerString = sharedPreferences.getString("singerList", "");
                            String imageString = sharedPreferences.getString("imageList", "");

                            titles = titleStirng.split(",");
                            singers = singerString.split(",");
                            images = imageString.split(",");

                            StringBuilder titleList = new StringBuilder();
                            StringBuilder singerList = new StringBuilder();
                            StringBuilder imageList = new StringBuilder();

                            for (int i = 0; i < titles.length; i++){
                                if (!titles[i].equals(o.getTitle())){
                                    titleList.append(titles[i]);
                                    titleList.append(",");
                                }

                                if (!singers[i].equals(o.getSinger())){
                                    singerList.append(singers[i]);
                                    singerList.append(",");
                                }

                                if (!images[i].equals(o.getImage())){
                                    imageList.append(images[i]);
                                    imageList.append(",");
                                }
                            }

                            editor.putString("titleList", titleList.toString());
                            editor.putString("singerList", singerList.toString());
                            editor.putString("imageList", imageList.toString());
                            editor.commit();


                            Toast.makeText(Favourite.this, "Removed from Favourites", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }


            return v;
        }
    }
}
