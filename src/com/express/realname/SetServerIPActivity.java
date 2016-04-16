/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.express.realname;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class SetServerIPActivity extends Activity {
    // Debugging

    private EditText ip_deviceA;
    private Button button_ok;
    private static String remoteIPA = "";

    private static String DB_NAME;
    private static final int DB_VERSION = 2;
    private static DatabaseHelper mOpenHelper;
    private static SQLiteDatabase db;

    public static String DB_CREATE_TABLE_IPCONFIG = "CREATE TABLE IF NOT EXISTS [setipconfig] " + "([ID] INTEGER PRIMARY KEY,[IP] VARCHAR)";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_TABLE_IPCONFIG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    public void DbCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE_TABLE_IPCONFIG);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.ip_seta);
        ip_deviceA = (IpEditer) findViewById(R.id.ip_deviceA);
        DB_NAME = "/sdcard/yishu/usingservice.db";
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        DbCreate(db);
        String sql = "select IP from setipconfig where ID=1;";
        Cursor cur;
        cur = db.rawQuery(sql, null);
        if (cur.moveToFirst() == true) {
            while (!cur.isAfterLast()) {
                remoteIPA = cur.getString(0).trim();
                break;
            }
        }
        cur.close();

        ip_deviceA.setText(remoteIPA);
        button_ok = (Button) findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                remoteIPA = ip_deviceA.getText().toString();
                String sql = "select IP from setipconfig where ID=1;";
                int p = 0;
                Cursor cur;
                cur = db.rawQuery(sql, null);
                if (cur.moveToFirst() == true) {
                    while (!cur.isAfterLast()) {
                        p++;
                        break;
                    }
                }
                cur.close();

                if (p == 0) {
                    sql = "insert into setipconfig (ID,IP) values(1,'" + remoteIPA + "');";
                    db.execSQL(sql);
                } else {
                    sql = "update setipconfig set IP='" + remoteIPA + "'" + " where ID=1" + ";";
                    db.execSQL(sql);
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        mOpenHelper.close();
    }
}
