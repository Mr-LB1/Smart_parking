package com.example.smart_parking;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

public class SuggestAdapter extends BaseAdapter implements Filterable {
   private Context context;
   private List<SuggestInfo> suggestInfos;
   private int layout;
   public SuggestAdapter(Context context, List<SuggestInfo> suggestInfos, int layout){
       this.context = context;
       this.suggestInfos = suggestInfos;
       this.layout = layout;
   }
    @Override
    public int getCount() {
        return suggestInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return suggestInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       if (convertView == null){
           convertView =  LayoutInflater.from(context).inflate(layout, parent, false);
       }
        TextView Show_Place = convertView.findViewById(R.id.suggestion_detail);
       Show_Place.setText(suggestInfos.get(position).getDestination());
       return convertView;
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
