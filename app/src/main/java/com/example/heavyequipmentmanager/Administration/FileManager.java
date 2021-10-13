package com.example.heavyequipmentmanager.Administration;

import com.example.heavyequipmentmanager.Engine.EngineTool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class FileManager {
    static String basePath = Constants.CONTEXT.getFilesDir().getAbsolutePath();

    static boolean firstSave = true;

    /**
     * Save and Load the manager state
     * */
    public static void save() throws IOException {
        FileOutputStream fileOutputStrem = new FileOutputStream(basePath + "/" + Constants.manager_File_name);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStrem);
//        for(Integer index: Constants.manager.getEngines().keySet()){
//            objectOutputStream.writeObject(Constants.manager);
//            objectOutputStream.flush();
//        }
        objectOutputStream.writeObject(Constants.manager.getEngines());
        objectOutputStream.flush();
        objectOutputStream.close();
        fileOutputStrem.close();
        if(!firstSave)
            firstSave = false;
    }

    public static void load() throws IOException, ClassNotFoundException {
        if(!firstSave) {
            try {
                FileInputStream fileInputStream = new FileInputStream(basePath + "/" + Constants.manager_File_name);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//                Constants.manager = (Manager) objectInputStream.readObject();
                ConcurrentHashMap<Integer, EngineTool> enginesLoaded = (ConcurrentHashMap<Integer, EngineTool>) objectInputStream.readObject();
                Constants.manager = Manager.init();
                Constants.manager.setEngines(enginesLoaded);
                objectInputStream.close();
                fileInputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
