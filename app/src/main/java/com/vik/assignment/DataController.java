package com.vik.assignment;

/**
 * Created by vik on 19/7/17.
 */

public class DataController {
    private static DataController instance;
    private DatabaseHelper dataBaseInstance;

    private DataController(){

    }

    public static DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }

    public void setDataBaseInstance(DatabaseHelper dataBaseInstance) {
        this.dataBaseInstance = dataBaseInstance;
    }

    public DatabaseHelper getDataBaseInstance() {
        return dataBaseInstance;
    }
}
