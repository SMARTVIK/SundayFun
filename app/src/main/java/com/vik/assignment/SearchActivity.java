package com.vik.assignment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity {

    private SearchImageAdapter searchImageAdapter;
    private Timer timer;
    private ArrayList<ImageTagModel> mainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mainList = new ArrayList<>();
        initViews();
    }

    private void initViews() {
        searchImageAdapter = new SearchImageAdapter();
        EditText editText = (EditText) findViewById(R.id.search);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(searchImageAdapter);
        mainList = DataController.getInstance().getDataBaseInstance().getAllImages();
        searchImageAdapter.setSearchData(mainList);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (s.length() > 0) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            postData(s);
                        }
                    }, 500);
                } else {
                    searchImageAdapter.setSearchData(mainList);
                }
            }
        });
    }

    private void postData(final Editable s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchImageAdapter.setSearchData(DataController.getInstance().getDataBaseInstance().getImageWithTags(s.toString().trim()));
            }
        });
    }
}
