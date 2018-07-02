package com.example.karthik.spider4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karthik.spider4.TopArtists.ArtistList;
import com.example.karthik.spider4.TopArtists.ArtistListResponse;
import com.example.karthik.spider4.TopTracks.MusicGenreList;

import java.util.List;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by karthik on 28-06-2018.
 */

public class FragmentB extends Fragment {
    String BASE_URL = "http://api.musixmatch.com/ws/1.1/";
    String API_KEY = "28ba11b5b9e792712fa5b640c26d6398";
    ListView listView;
    DatabaseArtist databaseArtist;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b, container, false);
        listView = (ListView) view.findViewById(R.id.topArtistList);
        databaseArtist = new DatabaseArtist(getActivity());
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        APIinterface apIinterface = retrofit.create(APIinterface.class);
        Call<ArtistListResponse> artistListResponseCall = apIinterface.getTopArtists(API_KEY, 1, 15, "in");
        artistListResponseCall.enqueue(new Callback<ArtistListResponse>() {
            @Override
            public void onResponse(Call<ArtistListResponse> call, Response<ArtistListResponse> response) {
                ArtistListResponse artistListResponse = response.body();
                List<ArtistList> artistLists = artistListResponse.getMessage().getBody().getArtistList();
                customArtists adapter = new customArtists(getActivity(), R.layout.topartist_singlerow, artistLists);
                listView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<ArtistListResponse> call, Throwable t) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistList artistList = (ArtistList) listView.getItemAtPosition(position);
                int artistId = artistList.getArtist().getArtistId();
                Intent intent = new Intent(getActivity(), ArtistDetails.class);
                Bundle bundle = new Bundle();
                bundle.putInt("artistId", artistId);
                intent.putExtras(bundle);
                startActivity(intent);


            }
        });

        return view;

    }


    class customArtists extends ArrayAdapter<ArtistList> {

        public customArtists(@NonNull Context context, @LayoutRes int resource, @NonNull List<ArtistList> objects) {
            super(context, resource, objects);
        }

        private boolean[] fav = new boolean[15];

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View row = convertView;
            final int[] flag = {0};
            final ArtistList artistList = getItem(position);
            if (row == null) {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                row = layoutInflater.inflate(R.layout.topartist_singlerow, parent, false);
            }
            final ImageView imageView = (ImageView) row.findViewById(R.id.favouriteA);
            if (fav[position]) {
                imageView.setImageResource(R.drawable.favourite_yes);
            } else {
                imageView.setImageResource(R.drawable.favourite_no);
            }

            String pos = getposition();
            //  Log.d("SHARED", pos);
            StringTokenizer savePosition = new StringTokenizer(pos, ",");
            for (int i = 0; i < pos.length(); i++) {
                while (savePosition.hasMoreTokens()) {
                    if (position == Integer.parseInt(savePosition.nextToken())) {
                        imageView.setImageResource(R.drawable.favourite_yes);
                        fav[position] = true;
                    }
                }
            }


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fav[position]) {
                        imageView.setImageResource(R.drawable.favourite_no);
                        fav[position] = false;
                    } else {
                        imageView.setImageResource(R.drawable.favourite_yes);

                        fav[position] = true;
                    }

                    String pos = getposition();

                    StringTokenizer savePosition = new StringTokenizer(pos, ",");
                    for (int i = 0; i < pos.length(); i++) {
                        while (savePosition.hasMoreTokens()) {

                            if (position == Integer.parseInt(savePosition.nextToken())) {
                                flag[0] = 1;
                                break;
                            }
                        }
                    }
                    StringBuilder genre = new StringBuilder();
                    List<MusicGenreList> list = artistList.getArtist().getPrimaryGenres().getMusicGenreList();

                    for (int i = 0; i < list.size(); i++) {
                        if (i == list.size() - 1)
                            genre.append(list.get(i).getMusicGenre().getMusicGenreName());
                        else
                            genre.append(list.get(i).getMusicGenre().getMusicGenreName() + ",");

                    }
                    if (list.size() <= 0) {
                        genre.append("Genre unavailable");
                    }
                    if (flag[0] == 1) {
                        Toast.makeText(getActivity(), "ALready exist", Toast.LENGTH_LONG).show();
                        //  AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        //dialog.setMessage("Already exist");
                        //dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        //@Override
                        //  public void onClick(DialogInterface dialog, int which) {store(position,1);
                        //  Log.d("Checking name", tracklist.getTrack().getTrackName());
                        //  databaseHelper.deleteData(tracklist.getTrack().getTrackName());
                        //int del = position;
                        // }
                        //});
                        //dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        //@Override
                        //  public void onClick(DialogInterface dialog, int which) {

                        //        }
                        //});
                        //  dialog.show();

                    } else {
                        databaseArtist.addData(artistList.getArtist().getArtistName(), String.valueOf(artistList.getArtist().getArtistRating()), genre.toString());
                        store(position);

                    }
                }
            });

            TextView artisttext = (TextView) row.findViewById(R.id.individual_artist);
            String artistName = artistList.getArtist().getArtistName();
            artisttext.setText(artistName);
            return row;
        }
    }

    public void store(int position) {
        StringBuilder str = new StringBuilder();


        str.append(getposition());
        //    if (flag == 1) {
        // String string = str.toString();
        //   char a[] = string.toCharArray();
        //     for (int i = 0; i < a.length; i++)
        //if (a[i] == position || a[i] == ',')
        //      a[i] = 'e';
        //string = String.valueOf(a);
        //Log.d("Checking string",string);
        //  str.append(string);
        //    Log.d("Checking yra", String.valueOf(str));

        //  }
        str.append(position + ",");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Save file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Position", String.valueOf(str));
        editor.commit();
    }


    public String getposition() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Save file", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Position", "");
    }


}