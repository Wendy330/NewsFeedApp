package com.yqian.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Recommandation extends AppCompatActivity {

    DatabaseReference root_ref = FirebaseDatabase.getInstance().getReference();

    ArrayList<Albums> albums_list;

    ArrayList<String> listviewTitle = new ArrayList<>();

    AlbumAdapter albumAdapter;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    int[] listviewImage = new int[]{
            R.drawable.album1, R.drawable.album2, R.drawable.album3, R.drawable.album4,
            R.drawable.album5, R.drawable.album6, R.drawable.album7, R.drawable.album8,
    };


    StringBuilder titleList= new StringBuilder();
    StringBuilder singerList = new StringBuilder();
    StringBuilder imageList = new StringBuilder();
//    StringBuilder switchStateList = new StringBuilder();


    String[] titles1;

//    boolean switchState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommandation);

        final ListView listView = (ListView)findViewById(R.id.list_view_three);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("FAVOURITE", this.MODE_PRIVATE);

//        sharedPreferences.edit().clear().commit();

//        String titleString1 = sharedPreferences.getString("titleList", "");
//        titles1 = titleString1.split(",");

        String titleStirng = sharedPreferences.getString("titleList", "");
        String singerString = sharedPreferences.getString("singerList", "");
        String imageString = sharedPreferences.getString("imageList", "");

        if (!titleStirng.isEmpty() && !singerString.isEmpty() && !imageString.isEmpty()){
            String[] titles = titleStirng.split(",");
            String[] singers = singerString.split(",");
            String[] images = imageString.split(",");

            for (int i = 0; i < titles.length; i++) {
                titleList.append(titles[i]);
                titleList.append(",");

                singerList.append(singers[i]);
                singerList.append(",");

                imageList.append(images[i]);
                imageList.append(",");
            }
        }

        root_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                albums_list = new ArrayList<>();

                for (DataSnapshot ab : dataSnapshot.child("albums").getChildren()){
                    String singer_id = "";

                    if (ab.child("title").getValue() != null){
                        listviewTitle.add(ab.child("title").getValue().toString());
                        singer_id = ab.child("singer_id").getValue().toString();

                        albums_list.add(new Albums(ab.child("title").getValue().toString(), dataSnapshot.child("singers").child(singer_id).child("name").getValue().toString()));
                    }
                }

                albumAdapter = new AlbumAdapter(Recommandation.this, R.layout.list_item_image, albums_list);
                listView.setAdapter(albumAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public class Albums {
        private String title;
        private String singer;

        public Albums(String title, String singer) {
            this.title = title;
            this.singer = singer;
        }

        public String getTitle(){
            return title;
        }
        public String getSinger(){
            return singer;
        }
    }

    private class AlbumAdapter extends ArrayAdapter<Albums> {
        private ArrayList<Albums> items;

        public AlbumAdapter(Context context, int textViewResourceId, ArrayList<Albums> items){
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

            final Albums o = items.get(position);

            if (o != null){
                final TextView tt = (TextView)v.findViewById(R.id.txtTitle);
                final TextView bt = (TextView)v.findViewById(R.id.txtSinger);
                final ImageView imageView = (ImageView)v.findViewById(R.id.listview_image);

                editor = sharedPreferences.edit();

                final Switch switch_fav = (Switch)v.findViewById(R.id.switch_fav);

                switch_fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked){

                            titleList.append(String.valueOf(tt.getText()));
                            titleList.append(",");

                            singerList.append(String.valueOf(bt.getText()));
                            singerList.append(",");

                            imageList.append(String.valueOf(imageView.getTag()));
                            imageList.append(",");

//                            switchStateList.append(String.valueOf(true));
//                            switchStateList.append(",");

                            Toast.makeText(Recommandation.this, "Added to Favourites", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            String titleStirng = sharedPreferences.getString("titleList", "");
                            String singerString = sharedPreferences.getString("singerList", "");
                            String imageString = sharedPreferences.getString("imageList", "");
//                            String switchString = sharedPreferences.getString("switchState", "");
//                            boolean switchState = sharedPreferences.getBoolean("switchState", false);

                            String[] titles = titleStirng.split(",");
                            String[] singers = singerString.split(",");
                            String[] images = imageString.split(",");
//                            String[] switches = switchString.split(",");

                            titleList = new StringBuilder();
                            singerList = new StringBuilder();
                            imageList = new StringBuilder();
//                            switchStateList = new StringBuilder();

                            for (int i = 0; i < titles.length; i++){
                                if (!titles[i].equals(o.getTitle())){
                                    titleList.append(titles[i]);
                                    titleList.append(",");
                                }

                                if (!singers[i].equals(o.getSinger())){
                                    singerList.append(singers[i]);
                                    singerList.append(",");
                                }

                                if (!images[i].equals(listviewImage[position])){
                                    imageList.append(images[i]);
                                    imageList.append(",");

//                                    switchStateList.append(String.valueOf(false));
//                                    switchStateList.append(",");
                                }

                            }

                            Toast.makeText(Recommandation.this, "Removed from Favourites", Toast.LENGTH_LONG).show();
                        }

//                        switchStateList.add(String.valueOf(isChecked));

                        editor.putString("titleList", titleList.toString());
                        editor.putString("singerList", singerList.toString());
                        editor.putString("imageList", imageList.toString());
                        editor.commit();

                    }
                });

                if (imageView != null){
                    imageView.setImageResource(listviewImage[position]);
                    imageView.setTag(listviewImage[position]);
                }
                if (tt != null){
                    tt.setText(o.getTitle());
                }
                if (bt != null){
                    bt.setText(o.getSinger());
                }




//                for (int i = 0; i < titles1.length; i++){
//                    if (titles1[i].equals(o.getTitle())){
//                        switch_fav.setChecked(true);
////
////                        i = titles1.length;
//
////                        Toast.makeText(Recommandation.this, titles1[i], Toast.LENGTH_LONG).show();
//
//                        i = titles1.length;
//                    }
//                }
            }

            return v;
        }
    }



}
