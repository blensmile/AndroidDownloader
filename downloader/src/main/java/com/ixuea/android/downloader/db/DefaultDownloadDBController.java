package com.ixuea.android.downloader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ixuea.android.downloader.config.Config;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.domain.DownloadThreadInfo;
import com.ixuea.android.downloader.domain.ItemInfo;

import java.util.ArrayList;
import java.util.List;

import static com.ixuea.android.downloader.domain.DownloadInfo.STATUS_COMPLETED;
import static com.ixuea.android.downloader.domain.DownloadInfo.STATUS_PAUSED;
import static com.ixuea.android.downloader.domain.DownloadInfo.STATUS_REMOVED;

/**
 * Created by ixuea(http://a.ixuea.com/3) on 17/1/23.
 */

public final class DefaultDownloadDBController implements DownloadDBController {


    public static final String[] DOWNLOAD_INFO_COLUMNS = new String[]{"_id", "supportRanges",
            "createAt", "uri",
            "path", "size", "progress",
            "status","extra","preview","itemId"};

    public static final String[] DOWNLOAD_THREAD_INFO_COLUMNS = new String[]{"_id", "threadId",
            "downloadInfoId", "uri",
            "start", "end", "progress"};

    public static final String[] ITEM_INFO_COLUMNS = new String[]{"_id","itemId",
            "userId","userName","userAvatar", "brefText","preview",
            "createAt","rating", "status", "origin","extra"};

    public static final String SQL_UPDATE_DOWNLOAD_THREAD_INFO = String.format(
            "REPLACE INTO %s (_id,threadId,downloadInfoId,uri,start,end,progress) VALUES(?,?,?,?,?,?,?);",
            DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_THREAD_INFO);

    public static final String SQL_UPDATE_DOWNLOAD_INFO = String.format(
            "REPLACE INTO %s (_id,supportRanges,createAt,uri,path,size,progress,status,extra,preview,itemId) VALUES(?,?,?,?,?,?,?,?,?,?,?);",
            DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_INFO);

    public static final String SQL_UPDATE_DOWNLOADING_INFO_STATUS = String.format(
            "UPDATE %s SET status=? WHERE status!=? and status!=?;",
            DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_INFO);

    public static final String SQL_UPDATE_ITEM_INFO = String.format(
            "REPLACE INTO %s (_id,itemId,userId,userName,userAvatar,brefText,preview,createAt,rating,status,origin,extra) VALUES(?,?,?,?,?,?,?,?,?,?,?,?);",
            DefaultDownloadHelper.TABLE_NAME_ITEM_INFO);

    private final Context context;
    private final DefaultDownloadHelper dbHelper;
    private final SQLiteDatabase writableDatabase;
    private final SQLiteDatabase readableDatabase;

    public DefaultDownloadDBController(Context context, Config config) {
        this.context = context;
        dbHelper = new DefaultDownloadHelper(context, config);
        writableDatabase = dbHelper.getWritableDatabase();
        readableDatabase = dbHelper.getReadableDatabase();
    }

    @SuppressWarnings("No problem")
    @Override
    public List<DownloadInfo> findAllDownloading() {
        Cursor cursor = readableDatabase.query(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_INFO,
                DOWNLOAD_INFO_COLUMNS, "status!=? and status!=?", new String[]{
                        String.valueOf(STATUS_COMPLETED),String.valueOf(STATUS_REMOVED)}, null, null, "createAt desc");

        List<DownloadInfo> downloads = new ArrayList<>();
        Cursor downloadCursor;
        while (cursor.moveToNext()) {
            DownloadInfo downloadInfo = new DownloadInfo();
            downloads.add(downloadInfo);

            inflateDownloadInfo(cursor, downloadInfo);

            //query download thread info
            downloadCursor = readableDatabase.query(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_THREAD_INFO,
                    DOWNLOAD_THREAD_INFO_COLUMNS, "downloadInfoId=?", new String[]{
                            String.valueOf(downloadInfo.getId())}, null, null, null);
            List<DownloadThreadInfo> downloadThreads = new ArrayList<>();
            while (downloadCursor.moveToNext()) {
                DownloadThreadInfo downloadThreadInfo = new DownloadThreadInfo();
                downloadThreads.add(downloadThreadInfo);
                inflateDownloadThreadInfo(downloadCursor, downloadThreadInfo);
            }

            downloadCursor.close();
            downloadInfo.setDownloadThreadInfos(downloadThreads);
        }
        cursor.close();
        return downloads;
    }

    public List<ItemInfo> findItem(String itemId) {
        Cursor cursor = readableDatabase.query(DefaultDownloadHelper.TABLE_NAME_ITEM_INFO,
                ITEM_INFO_COLUMNS, "id=?", new String[]{
                        itemId}, null, null, "createAt desc");

        List<ItemInfo> infoList = new ArrayList<>();
        while(cursor.moveToNext()){
            ItemInfo info = new ItemInfo();
            inflateItemInfo(cursor,info);
            infoList.add(info);
        }
        cursor.close();
        return infoList;
    }

    @Override
    public List<DownloadInfo> findAllDownloaded() {
        Cursor cursor = readableDatabase.query(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_INFO,
                DOWNLOAD_INFO_COLUMNS, "status=?", new String[]{
                        String.valueOf(STATUS_COMPLETED)}, null, null, "createAt desc");

        List<DownloadInfo> downloads = new ArrayList<>();
        while (cursor.moveToNext()) {
            DownloadInfo downloadInfo = new DownloadInfo();
            downloads.add(downloadInfo);
            inflateDownloadInfo(cursor, downloadInfo);
        }
        cursor.close();
        return downloads;
    }

    @Override
    public List<DownloadInfo> findAllRemoved(){
        Cursor cursor = readableDatabase.query(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_INFO,
                DOWNLOAD_INFO_COLUMNS, "status=?", new String[]{
                        String.valueOf(STATUS_REMOVED)}, null, null, "createAt desc");

        List<DownloadInfo> downloads = new ArrayList<>();
        while (cursor.moveToNext()) {
            DownloadInfo downloadInfo = new DownloadInfo();
            downloads.add(downloadInfo);
            inflateDownloadInfo(cursor, downloadInfo);
        }
        cursor.close();
        return downloads;
    }

    private void inflateDownloadThreadInfo(Cursor cursor,
                                           DownloadThreadInfo downloadThreadInfo) {
        downloadThreadInfo.setId(cursor.getInt(0));
        downloadThreadInfo.setThreadId(cursor.getInt(1));
        downloadThreadInfo.setDownloadInfoId(cursor.getString(2));
        downloadThreadInfo.setUri(cursor.getString(3));
        downloadThreadInfo.setStart(cursor.getLong(4));
        downloadThreadInfo.setEnd(cursor.getLong(5));
        downloadThreadInfo.setProgress(cursor.getLong(6));
    }

    private void inflateDownloadInfo(Cursor cursor, DownloadInfo downloadInfo) {
        downloadInfo.setId(cursor.getString(0));
        downloadInfo.setSupportRanges(cursor.getInt(1));
        downloadInfo.setCreateAt(cursor.getLong(2));
        downloadInfo.setUri(cursor.getString(3));
        downloadInfo.setPath(cursor.getString(4));
        downloadInfo.setSize(cursor.getLong(5));
        downloadInfo.setProgress(cursor.getLong(6));
        downloadInfo.setStatus(cursor.getInt(7));
        downloadInfo.setExtra(cursor.getString(8));
        downloadInfo.setPreview(cursor.getString(9));
        downloadInfo.setItemId(cursor.getString(10));
    }

    private void inflateItemInfo(Cursor cursor,ItemInfo info){
        info.setId(cursor.getString(0))
                .setItemId(cursor.getString(1))
                .setUserId(cursor.getString(2))
                .setUserName(cursor.getString(3))
                .setUserAvatar(cursor.getString(4))
                .setBrefText(cursor.getString(5))
                .setPreview(cursor.getString(6))
                .setCreateAt(cursor.getLong(7))
                .setRating(cursor.getInt(8))
                .setStatus(cursor.getInt(9))
                .setOrigin(cursor.getString(10))
                .setExtra(cursor.getString(11));
    }

    @Override
    public DownloadInfo findDownloadedInfoById(String id) {
        Cursor cursor = readableDatabase
                .query(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_INFO, DOWNLOAD_INFO_COLUMNS, "_id=?",
                        new String[]{id},
                        null, null, "createAt desc");
        if (cursor.moveToNext()) {
            DownloadInfo downloadInfo = new DownloadInfo();

            inflateDownloadInfo(cursor, downloadInfo);
            cursor.close();
            return downloadInfo;
        }
        cursor.close();
        return null;
    }

    @Override
    public void pauseAllDownloading() {
        writableDatabase.execSQL(
                SQL_UPDATE_DOWNLOADING_INFO_STATUS,
                new Object[]{STATUS_PAUSED, STATUS_COMPLETED,STATUS_REMOVED});
    }

    @Override
    public void createOrUpdate(DownloadInfo downloadInfo) {
        writableDatabase.execSQL(
                SQL_UPDATE_DOWNLOAD_INFO,
                new Object[]{
                        downloadInfo.getId(), downloadInfo.getSupportRanges(),
                        downloadInfo.getCreateAt(), downloadInfo.getUri(), downloadInfo.getPath(),
                        downloadInfo.getSize(), downloadInfo.getProgress(), downloadInfo.getStatus(),
                        downloadInfo.getExtra(),downloadInfo.getPreview(),downloadInfo.getItemId()});
    }

    @Override
    public void createOrUpdate(DownloadThreadInfo downloadThreadInfo) {
        writableDatabase.execSQL(
                SQL_UPDATE_DOWNLOAD_THREAD_INFO,
                new Object[]{
                        downloadThreadInfo.getId(),
                        downloadThreadInfo.getThreadId(),
                        downloadThreadInfo.getDownloadInfoId(),
                        downloadThreadInfo.getUri(),
                        downloadThreadInfo.getStart(), downloadThreadInfo.getEnd(),
                        downloadThreadInfo.getProgress()});
    }

    @Override
    public void createOrUpdate(ItemInfo itemInfo) {
        writableDatabase.execSQL(
                SQL_UPDATE_ITEM_INFO,
                new Object[]{
                        itemInfo.getId(),itemInfo.getItemId(),
                        itemInfo.getUserId(),itemInfo.getUserName(),itemInfo.getUserAvatar(),
                        itemInfo.getBrefText(),itemInfo.getPreview(),itemInfo.getCreateAt(),
                        itemInfo.getRating(),itemInfo.getStatus(),itemInfo.getOrigin(),itemInfo.getExtra()});
    }

    @Override
    public void delete(DownloadInfo downloadInfo) {
        writableDatabase.delete(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_INFO, "_id=?",
                new String[]{String.valueOf(downloadInfo.getId())});
        writableDatabase
                .delete(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_THREAD_INFO, "downloadInfoId=?",
                        new String[]{String.valueOf(downloadInfo.getId())});
    }

    @Override
    public void remove(DownloadInfo downloadInfo) {
        writableDatabase
                .delete(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_THREAD_INFO, "downloadInfoId=?",
                        new String[]{String.valueOf(downloadInfo.getId())});
        downloadInfo.setDownloadThreadInfos(null);
        createOrUpdate(downloadInfo);
    }


    @Override
    public void delete(DownloadThreadInfo downloadThreadInfo) {
        writableDatabase
                .delete(DefaultDownloadHelper.TABLE_NAME_DOWNLOAD_THREAD_INFO, "_id=?",
                        new String[]{String.valueOf(downloadThreadInfo.getId())});
    }

    @Override
    public void delete(ItemInfo itemInfo) {

    }

}
