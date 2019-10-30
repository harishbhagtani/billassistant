package com.android.example.billassistant.HelperClasses;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileHelper {
    public static void backupDatabaseFile(String databaseName, Context context) {
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Stocks/Databases/");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (!success) {
            Toast.makeText(context, "Error Backing Up", Toast.LENGTH_LONG).show();
        }
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//com.yourbusinessassistant.stocks//databases//" + databaseName;
                String backupDBPath = "Stocks/Databases/" + databaseName;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreDatabaseFile(String databaseName, Context context) {
        context.deleteDatabase(databaseName);
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canRead()) {
                String currentDBPath = "Stocks/Databases/" + databaseName;
                String backupPath = "//data//com.yourbusinessassistant.stocks//databases//" + databaseName;
                File currentDB = new File(sd, currentDBPath);
                File backupDB = new File(data, backupPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restoreAllDatabaseFiles(Context context) {

    }

    public static void backupAllDatabaseFiles(Context context) {

    }
}
