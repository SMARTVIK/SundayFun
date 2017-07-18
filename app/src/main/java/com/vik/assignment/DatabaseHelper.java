package com.vik.assignment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
 
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "read_text_from_image";

    // Table Names
    private static final String TABLE_IMAGES = "images";

    // Common column names
    private static final String KEY_CREATED_DATE = "created_at";
 
    // NOTES Table - column nmaes
    private static final String KEY_IMAGE_PATH = "image_path";
    private static final String KEY_TAGS = "tags";

    // Table Create Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_IMAGES + "(" + KEY_IMAGE_PATH
            + " TEXT," + KEY_TAGS + " TEXT," + KEY_CREATED_DATE
            + " DATETIME" + ")";

    private static final String KEY_USER_ID = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "db created");
        // creating required tables
        db.execSQL(CREATE_TABLE_USERS);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
        onCreate(db);
    }

    public long saveImageToDb(ImageTagModel imageTagModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_PATH, imageTagModel.getImagePath());
        values.put(KEY_TAGS, imageTagModel.getTags());
        values.put(KEY_CREATED_DATE, getDateTime());
        long imageId = db.insert(TABLE_IMAGES, null, values);
        return imageId;
    }

    /**
     * get single todo
     */
    public ArrayList<ImageTagModel> getImageWithTags(String userId) {
        ArrayList<ImageTagModel> imageTagModels = new ArrayList<ImageTagModel>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM TABLE " + TABLE_IMAGES + " WHERE " + KEY_TAGS + " LIKE " + "'%" + userId + "%'";
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                ImageTagModel imageTagModel = new ImageTagModel();
                imageTagModel.setImagePath((c.getString(c.getColumnIndex(KEY_IMAGE_PATH))));
                imageTagModel.setTags((c.getString(c.getColumnIndex(KEY_TAGS))));
                imageTagModel.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_DATE)));
                imageTagModels.add(imageTagModel);
            } while (c.moveToNext());
        }
        return imageTagModels;
    }


    /**
     * getting all users
     * */
    public ArrayList<ImageTagModel> getAllImages() {
        ArrayList<ImageTagModel> imageTagModels = new ArrayList<ImageTagModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGES;
        Log.e(LOG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                ImageTagModel imageTagModel = new ImageTagModel();
                imageTagModel.setImagePath((c.getString(c.getColumnIndex(KEY_IMAGE_PATH))));
                imageTagModel.setTags((c.getString(c.getColumnIndex(KEY_TAGS))));
                imageTagModel.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_DATE)));
                imageTagModels.add(imageTagModel);
            } while (c.moveToNext());
        }

        return imageTagModels;
    }

    /**
     * getting images count
     */
    public int getImagesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_IMAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
