package com.example.smart_parking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.List;

import Utils.AppManager;
import javaBean.UserInfo;

public class Search_View extends AppCompatActivity implements OnGetSuggestionResultListener , View.OnTouchListener, AdapterView.OnItemClickListener, TextWatcher {
    private AutoCompleteTextView SearchInfo;
    private ListView             Suggestion;
    private SuggestionSearch     suggestionSearch;
    private String               city;
    private Drawable             drawableright;
    private SuggestInfo          suggestInfo;
    private List<SuggestInfo>    suggestInfos;
    private UserInfo             userInfo;
    private InputMethodManager   inputMethodManager;
    private String               SearchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__view);
        AppManager.addActivity(this);
        initView();
        setlistener();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setlistener() {
        SearchInfo.setOnTouchListener(this::onTouch);
        suggestionSearch.setOnGetSuggestionResultListener(this);
        Suggestion.setOnItemClickListener(this);
        SearchInfo.addTextChangedListener(this);
    }

    private void initView() {
        SearchInfo = findViewById(R.id.SearchInfo);
        Suggestion = findViewById(R.id.suggestinfo);
        drawableright = SearchInfo.getCompoundDrawables()[2];
        suggestionSearch = SuggestionSearch.newInstance();
        city = getIntent().getStringExtra("city");
        SearchInfo.setCompoundDrawables(SearchInfo.getCompoundDrawables()[0], SearchInfo.getCompoundDrawables()[1], null, SearchInfo.getCompoundDrawables()[3]);
        userInfo = (UserInfo) getIntent().getSerializableExtra("userinfo");
//        SearchText = getIntent().getStringExtra("SearchText");
//        SearchInfo.setText(SearchText);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }
//Suggestion回调函数,设置listview适配器
    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {

        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }
        List<SuggestionResult.SuggestionInfo> result = suggestionResult.getAllSuggestions();
        suggestInfos = new ArrayList<SuggestInfo>();
        for (int i = 0; i < result.size(); i++) {
            if (!(result.get(i).pt == null)) {
                suggestInfo = new SuggestInfo();
                Double lat = result.get(i).pt.latitude;
                Double lon = result.get(i).pt.longitude;
                String Destination = result.get(i).key;
                suggestInfo.setLat(lat);
                suggestInfo.setLon(lon);
                suggestInfo.setDestination(Destination);
//                    Double lng=result.get(i).pt.longitude;
//                    Double distance= DistanceUtil.getDistance(location,new LatLng(lat,lng));
//                    placeInfo.setName(result.get(i).address);
//                    placeInfo.setDistance(distance);
//                    list.add(placeInfo);
                suggestInfos.add(suggestInfo);
            }
        }
        SuggestAdapter adapter = new SuggestAdapter(this,suggestInfos,R.layout.suggest_list_item);
        SearchInfo.setThreshold(1);
        Suggestion.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    //设置清除键效果
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (drawableright != null && event.getRawX() >= SearchInfo.getRight() - drawableright.getBounds().width()) {
                SearchInfo.setText(null);
                SearchInfo.clearFocus();
                Suggestion.setVisibility(View.GONE);
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        suggestionSearch.destroy();
        super.onDestroy();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        suggestInfo = suggestInfos.get(position);
        Intent intent = new Intent(this,Baidu_Map.class);
        intent.putExtra("userinfo",userInfo);
        intent.putExtra("destination", suggestInfo.getDestination());
        intent.putExtra("lat", suggestInfo.getLat());
        intent.putExtra("lon", suggestInfo.getLon());
        intent.putExtra("isfirstloc",false);
        startActivity(intent);
        AppManager.finishCurrentActivity();
        AppManager.finishActivity(Baidu_Map.class);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

                if (s.length() <= 0) {
                    SearchInfo.setCompoundDrawables(SearchInfo.getCompoundDrawables()[0], SearchInfo.getCompoundDrawables()[1], null, SearchInfo.getCompoundDrawables()[3]);
                    Suggestion.setVisibility(View.GONE);
                    return;
                }
                else {
                    Suggestion.setVisibility(View.VISIBLE);
                    SearchInfo.setCompoundDrawables(SearchInfo.getCompoundDrawables()[0], SearchInfo.getCompoundDrawables()[1],drawableright , SearchInfo.getCompoundDrawables()[3]);
                }
                suggestionSearch.requestSuggestion((new SuggestionSearchOption())
                        .keyword(s.toString())
                        .city(city));
            }
}

