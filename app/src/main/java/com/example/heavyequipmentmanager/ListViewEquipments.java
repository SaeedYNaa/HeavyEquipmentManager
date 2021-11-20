package com.example.heavyequipmentmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.heavyequipmentmanager.Administration.ImageManager.ImageManager;
import com.example.heavyequipmentmanager.Engine.EngineTool;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;


public class ListViewEquipments extends ArrayAdapter<EngineTool> implements Filterable {
    public static Context con;
    ArrayList<EngineTool> enginesList;
    ArrayList<EngineTool> searchEnginesList;


    public ListViewEquipments(Context context, ArrayList<EngineTool> enginesList){
        super(context, R.layout.listview_tiem, enginesList);
        this.con = context;
        this.enginesList = new ArrayList<>(enginesList);
        searchEnginesList = enginesList;
    }

    public ArrayList<EngineTool> getEnginesList() {
        return searchEnginesList;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return engineFilter;
    }

    private Filter engineFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constrains) {
            FilterResults results = new FilterResults();
            List<EngineTool> suggestions = new ArrayList<>();

            if(constrains == null || constrains.length() == 0) {
                suggestions.addAll(enginesList);
            }
            else{
                String filterPattern = constrains.toString().toLowerCase().trim();

                for(EngineTool en: enginesList){
                    if(en.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(en);
                    }
                }
            }

            results.values = suggestions;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            searchEnginesList.clear();
            searchEnginesList.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((EngineTool) resultValue).getName();
        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        EngineTool en = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_tiem, parent, false);
        }

        TextView name = convertView.findViewById(R.id.engineName);
        TextView date = convertView.findViewById(R.id.engineDate);
        CircleImageView image = (CircleImageView) convertView.findViewById(R.id.engineImage);

        String nextTreatment = "הטיפול הבא: " + en.getNextTreatment();
        name.setText(en.getName());
        date.setText(nextTreatment);
        Bitmap bmImg = ImageManager.readImage(en.getImagePath());
        image.setImageBitmap(bmImg);

        return convertView;
    }

}
