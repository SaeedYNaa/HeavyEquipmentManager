package com.example.heavyequipmentmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heavyequipmentmanager.Engine.EngineTool;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

public class ListViewEquipments extends ArrayAdapter<EngineTool> {
    Context con = null;


    public ListViewEquipments(Context context, ArrayList<EngineTool> enginesList){
        super(context, R.layout.listview_tiem, enginesList);
        this.con = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        EngineTool en = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_tiem, parent, false);
        }

        ImageView im = convertView.findViewById(R.id.engineImage);
        TextView name = convertView.findViewById(R.id.engineName);
        TextView date = convertView.findViewById(R.id.engineDate);

        String nextTreatment = "הטיפול הבא: " + en.getNextTreatment();
        im.setImageResource(en.getImageId());
        name.setText(en.getName());
        date.setText(nextTreatment);

        return convertView;
    }
}
