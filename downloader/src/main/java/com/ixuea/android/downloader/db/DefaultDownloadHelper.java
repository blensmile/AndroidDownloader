package com.ixuea.android.downloader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ixuea.android.downloader.config.Config;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 17/1/23.
 */

public class DefaultDownloadHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME_DOWNLOAD_INFO = "download_info";
    public static final String TABLE_NAME_DOWNLOAD_THREAD_INFO = "download_thread_info";
    public static final String TABLE_NAME_ITEM_INFO = "item_info";
    private static final String SQL_CREATE_DOWNLOAD_TABLE = String.format(
            "CREATE TABLE %s (_id varchar(255) PRIMARY KEY NOT NULL,supportRanges integer NOT NULL,createAt long NOT NULL,uri varchar(255) NOT NULL,path varchar(255) NOT NULL,size long NOT NULL, progress long NOT NULL,status integer NOT NULL,extra text,preview varchar(255),itemId varchar(255));",
            TABLE_NAME_DOWNLOAD_INFO);
    private static final String SQL_CREATE_DOWNLOAD_THREAD_TABLE = String.format(
            "CREATE TABLE %s (_id integer PRIMARY KEY NOT NULL,threadId integer NOT NULL,downloadInfoId varchar(255) NOT NULL,uri varchar(255) NOT NULL,start long NOT NULL,end long NOT NULL,progress long NOT NULL);",
            TABLE_NAME_DOWNLOAD_THREAD_INFO);
    private static final String SQL_CREATE_ITEM_TABLE = String.format(
            "CREATE TABLE %s (_id varchar(255) PRIMARY KEY NOT NULL," +
                    "itemId varchar(255) NOT NULL," +
                    "userId varchar(255) NOT NULL," +
                    "userName varchar(255) NOT NULL," +
                    "userAvatar varchar(255) NOT NULL," +
                    "brefText varchar(255) NOT NULL," +
                    "preview varchar(255) NOT NULL," +
                    "createAt long NOT NULL," +
                    "rating int," +
                    "status int," +
                    "origin text," +
                    "extra text);" ,
            TABLE_NAME_ITEM_INFO);


    public DefaultDownloadHelper(Context context, Config config) {
        super(context, config.getDatabaseName(), null, config.getDatabaseVersion());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_DOWNLOAD_TABLE);
        db.execSQL(SQL_CREATE_DOWNLOAD_THREAD_TABLE);
        db.execSQL(SQL_CREATE_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //version is changed in com.ixuea.android.downloader.config.Config
        //TODO upgrade database
        if(oldVersion < 3){
            db.execSQL(String.format("alter table %s add column extra text",TABLE_NAME_DOWNLOAD_INFO));
        }
        if(oldVersion < 4){
            db.execSQL(String.format("alter table %s add column preview varchar(255)",TABLE_NAME_DOWNLOAD_INFO));
            db.execSQL(String.format("alter table %s add column itemId varchar(255)",TABLE_NAME_DOWNLOAD_INFO));
            db.execSQL(SQL_CREATE_ITEM_TABLE);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    //  CREATE TABLE download_info (
    //      _id integer PRIMARY KEY   NOT NULL,
    //      id varchar(255)   NOT NULL,
    //      supportRanges integer   NOT NULL,
    //      createAt long   NOT NULL,
    //      url varchar(255)   NOT NULL,
    //      path varchar(255)  NOT NULL,
    //      size long  NOT NULL,
    //      progress long  NOT NULL,
    //      status integer  NOT NULL
    //  );

    //  CREATE TABLE download_thread (
    //      _id integer PRIMARY KEY  NOT NULL,
    //      downloadkey integer  NOT NULL,
    //      threadId integer  NOT NULL,
    //      url varchar(255)  NOT NULL,
    //      start long  NOT NULL DEFAULT(0),
    //      end long  NOT NULL,
    //      progress long  NOT NULL DEFAULT(0)
    //  );

}




