package com.example.heavyequipmentmanager.Administration;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.example.heavyequipmentmanager.Engine.EngineTool;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;

/**
 * This class is responsible for all the action in this application.
 * There is only one manager.
 * */
public class Manager implements Serializable {
        public static Manager manager = null;
        private ConcurrentHashMap<Integer, EngineTool> engines;


        public Manager(){
            engines = new ConcurrentHashMap<>();
        }


        public static Manager init(){
            if(manager == null)
                manager = new Manager();

            return manager;
        }

    public ConcurrentHashMap<Integer, EngineTool> getEngines() {
        return engines;
    }

    public void setEngines(ConcurrentHashMap<Integer, EngineTool> engines) {
        this.engines = engines;
    }

    public ArrayList<EngineTool> getTools(){
            ArrayList<EngineTool> arr = new ArrayList<>();
            for(Integer i : this.engines.keySet())
                arr.add(this.engines.get(i));

            return arr;
    }

    public void addEngine(int i, EngineTool en){
            if(!this.engines.containsKey(i)){
                this.engines.put(i, en);
                // save engine into database
                try {
                    Constants.db.saveEngine(en, i);
                }catch (SQLiteConstraintException e){
                    Log.d("MANAGER-addEngine(): ", "error inserting ID " + i + ": " + e.getMessage());
                }
            }
            else
                Log.d("addEngine", "Failed to add Engine, the key is already taken.");
    }

    public void editEngine(int i, String name, String treatment, String newTreatment, double workingHours){
            EngineTool edited = this.engines.get(i);
            if(edited != null){
                edited.setName(name);
                edited.setTreatment(treatment);
                edited.setNextTreatment(newTreatment);
                edited.setWorkingHours(workingHours);
                Constants.db.updateEngineInformation(i, edited);
            }
            else
                Log.d("editEngine", "Failed to edit engine, the key is already taken.");

    }

    public void deleteEngine(int index){
            if(engines.remove(index) == null){
                Log.d("MANAGER-deleteEngine()", " index: " + index + " not found..");
            }
            else{
                // remove from database
                Constants.db.deleteRecordID(index);
                //update the list indices:
//                Iterator<Integer> iterator = engines.keySet().iterator();
//                while(iterator.hasNext()){
//
//                }
                for(Integer i: engines.keySet()){
                    if(i > index){
                        // cuncurrentModifications issue..
                        EngineTool tmp = engines.remove(i);
                        engines.put(i - 1, tmp);
                    }
                }
                Constants.db.updateTable();
                Log.d("MANAGER", "deleteEngine(): index: " + index + " DELETED!");
            }
    }


    public HashMap<EngineTool, LinkedList<String>> checkEnginesDates(){
            HashMap<EngineTool, LinkedList<String>> expiredEngines = new HashMap<>();
            for(EngineTool en: engines.values()){
                String[] splited_current_date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()).split("/");
                LinkedList<String> expired = someDateExpired(en, splited_current_date);
                if(!expired.isEmpty()){
                    expiredEngines.put(en, expired);
                }
            }
            return expiredEngines;
    }

    private LinkedList<String> someDateExpired(EngineTool en, String[] splited_current_date) {
            LinkedList<String> expiredAsStrings = new LinkedList<>();
            int day = 0;
            int month = 1;
            int year = 2;
            int current_day = Integer.parseInt(splited_current_date[day]);
            int current_month = Integer.parseInt(splited_current_date[month]);
            int current_year = Integer.parseInt(splited_current_date[year]);
            String[] next_treatment_splited = en.getNextTreatment().split(Constants.regex);
            String[] test_splited = en.getTestDate().split(Constants.regex);
            String[] ensurence_splited = en.getEnsurenceDate().split(Constants.regex);

            /**
             * there is a little bug in this approach
             * ex current month x/1/2021 expired date x/12/2022 this will send notification while it should send
             * it at x/11/2021
             * */
            // check the upcoming Engine-treatment date
            if(Math.abs(Integer.parseInt(next_treatment_splited[month]) - current_month) < 1){
                expiredAsStrings.add("Engine Next Treatment");
            }
            if(Math.abs(Integer.parseInt(test_splited[month]) - current_month) < 1){
                expiredAsStrings.add("Engine Test");
            }
            if(Math.abs(Integer.parseInt(ensurence_splited[month]) - current_month) < 1){
                expiredAsStrings.add("Engine Ensurence");
            }

            return expiredAsStrings;
        }



        // HELPER
        public void logEngines(){
            for(Integer i: engines.keySet()){
                Log.d("MANAGER: logEngines()", "" + i + ": " + engines.get(i).getName());
            }
        }


}
