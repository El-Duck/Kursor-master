package com.example.cursor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.cursor.SubMainActivity.download_arr;
import static com.example.cursor.SubMainActivity.download_obj;

public class ListFragment extends Fragment {

    View v;
    EditText editText;
    TAdapter adapter;
    ArrayList<Teacher> teachers;
    ArrayList<Teacher> teachers1;
    ArrayList<String> strings;
    ArrayList<String> strings1;
    ListView listView;
    ImageButton imageButton;
    ArrayList<String> listItems;
    ArrayList<String> listItems1;
    String []week = {"monday","tuesday","wednesday", "thursday", "friday"};

    ListFragment(ArrayList<Teacher> teachers){
        strings = new ArrayList<>();
        strings1 = new ArrayList<>();
        for (int i = 0; i < teachers.size(); i++) {
            strings.add(teachers.get(i).name);
            strings1.add(teachers.get(i).subject);
        }
        this.teachers = teachers;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_list, container, false);

        editText = v.findViewById(R.id.txtsearch);
        listView = v.findViewById(R.id.listview);
        imageButton = v.findViewById(R.id.imageButton);
        imageButton.setOnClickListener(view -> {
            editText.setText("");
            initList();
        });

        adapter = new TAdapter((Activity) v.getContext(), strings, strings1);
        listView.setAdapter(adapter);
        initList();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                new Thread(() -> {
                    try {
                        load(position, false);
                        JSONArray jsonArray1 = download_arr("http://cursor.spb.ru/get_time").getJSONArray(0);
                        String [][] time = new String[10][2];
                        time[8][0] = "--";
                        time[8][1] = "--";
                        time[9][0] = "--";
                        time[9][1] = "--";
                        for (int i = 0; i < jsonArray1.length(); i++)
                            if (i % 2 == 0){
                                String s = jsonArray1.getString(i);
                                System.out.println(s);
                                int a = s.indexOf("(");
                                int b = s.indexOf("-");
                                int c = s.indexOf(")");
                                time[i/2][0] = s.substring(a+1,b);
                                time[i/2][0].replace('.', ':');
                                time[i/2][1] = s.substring(b+1,c);
                                time[i/2][1].replace('.', ':');
                                System.out.println(time[i/2][0] + ":" + time[i/2][1]);
                            }
                        System.out.println(jsonArray1);
                        JSONObject jsonObject = download_obj("http://cursor.spb.ru/schedule/" + teachers1.get(position).id);
                        Bitmap bitmap = DownloadImageFromPath( jsonObject.getString("photo"));
                        for (int i = 0; i < 5; i++) {
                            Day day = new Day();
                            JSONArray jsonArray = jsonObject.getJSONArray(week[i]);
                            for (int j = 0; j < jsonArray.length(); j+=2) {
                                Lesson lesson = new Lesson();
                                lesson.classroom = jsonArray.getString(j);
                                if(j+1 < jsonArray.length())
                                    lesson.name = jsonArray.getString(j+1);
                                lesson.start = time[j/2][0];
                                lesson.end = time[j/2][1];
                                day.lessons.add(lesson);
                            }
                            teachers1.get(position).week.add(day);
                        }
                        teachers1.get(position).url = jsonObject.getString("photo");
                        teachers1.get(position).bitmap = bitmap;
                        System.out.println(teachers1.get(position).url);
                        System.out.println(jsonObject.toString());
                        load(position, true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }).start();
            }catch (Exception e){}
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    initList();
                } else {
                    searchItem(s.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return v;
    }

    void load(int position, boolean ready){
        TeacherFragment fragment = new TeacherFragment(teachers1.get(position), ready);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.addToBackStack(null);
        if(ready)
            getFragmentManager().popBackStack();

        fragmentTransaction.commit();
    }
    public void searchItem(String textToSearch){
        listItems = new ArrayList<>(strings);
        listItems1 = new ArrayList<>(strings1);
        teachers1 = new ArrayList<>(teachers);
        for(String item:strings){
            String textToSearchlowercase = textToSearch.toLowerCase();
            if(!item.toLowerCase().contains(textToSearchlowercase) && listItems.indexOf(item) != -1){
                listItems1.remove(listItems.indexOf(item));
                teachers1.remove(listItems.indexOf(item));
                listItems.remove(item);
            }
        }
        for (int i = 0; i < teachers1.size(); i++) {
            System.out.println(teachers1.get(i).name);
        }
        adapter.notifyDataSetChanged();
        adapter = new TAdapter((Activity) v.getContext(), listItems, listItems1);
        listView.setAdapter(adapter);
    }



    public void initList(){
        listItems = new ArrayList<>(strings);
        listItems1 = new ArrayList<>(strings1);
        teachers1 = new ArrayList<>(teachers);
        adapter = new TAdapter((Activity) v.getContext(), listItems, listItems1);
        listView.setAdapter(adapter);
    }
    Bitmap DownloadImageFromPath(String path){
        InputStream in =null;
        Bitmap bmp=null;
        int responseCode = -1;
        try{

            URL url = new URL(path);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setDoInput(true);
            con.connect();
            responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                //download
                in = con.getInputStream();
                bmp = BitmapFactory.decodeStream(in);
                in.close();

            }

        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return bmp;
    }


}
