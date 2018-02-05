package com.ipati.dev.brochure.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipati.dev.brochure.R;

import java.util.ArrayList;

/**
 * Created by ipati on 5/6/2560.
 */

public class SpinnerBaseAdapter extends BaseAdapter {
    private Context mcontext;
    private ArrayList<String> list;

    public SpinnerBaseAdapter(Context applicationContext, ArrayList<String> listMenu) {
        this.mcontext = applicationContext;
        this.list = listMenu;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        LayoutInflater mInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_layout_spinner, parent, false);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name_menu_sp);
            holder.tvName.setText(list.get(position));
            convertView.setTag(holder);
        } else {
            convertView.getTag();
        }
        return convertView;
    }

    class Holder {
        TextView tvName;
        ImageView iconDropdown;
    }
}
