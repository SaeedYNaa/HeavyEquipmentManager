package com.example.heavyequipmentmanager.Notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.heavyequipmentmanager.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListViewNotification extends ArrayAdapter<String> {



    public ListViewNotification(Context context, ArrayList<String> noti_strings) {
        super(context, R.layout.listview_item_notification, noti_strings);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item_notification, parent, false);
        }

        String not = getItem(position);
        TextView t = (TextView) convertView.findViewById(R.id.textView_notification);
        t.setText(not);

        return convertView;
    }
}
