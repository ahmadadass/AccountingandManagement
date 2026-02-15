package com.example.accountingandmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class LocalBackup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_backup);

        ListView lv_drive_backup = findViewById(R.id.lv_drive_backup);
        ListView lv_local_backup = findViewById(R.id.lv_local_backup);
        Button btn_back_backup = findViewById(R.id.btn_back_backup);

        btn_back_backup.setOnClickListener( e -> {
            Intent intent = new Intent(LocalBackup.this,statisticsActivity.class);
            startActivity(intent);
        });

        ArrayList<ImageItem> driveList = new ArrayList<>();

        Bitmap ic_uploade_to_drive = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_action_backup);
        Bitmap ic_download_to_drive = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_action_download_from_cloud);

        driveList.add(new ImageItem(ic_uploade_to_drive,"Sign in to Google Drive","Tap to backup your data"));
        driveList.add(new ImageItem(ic_download_to_drive,"Restore","Select a backup"));

        ImageItemAdapter itemDriveAdapter = new ImageItemAdapter(this,driveList);

        lv_drive_backup.setAdapter(itemDriveAdapter);

        ArrayList<ImageItem> localList = new ArrayList<>();

        Bitmap ic_action_last_backup = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_action_last_backup);
        Bitmap ic_action_restore_from_device = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_action_restore_from_device);

        localList.add(new ImageItem(ic_action_last_backup,"Last Backup","Never"));
        localList.add(new ImageItem(ic_action_restore_from_device,"Restore","Select a backup"));

        ImageItemAdapter itemLocalAdapter = new ImageItemAdapter(this,localList);

        lv_local_backup.setAdapter(itemLocalAdapter);


    }
}