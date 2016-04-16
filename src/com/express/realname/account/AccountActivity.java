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

package com.express.realname.account;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.express.realname.R;


/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class AccountActivity extends Activity {
    private EditText express,express_company,express_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sPreferences = AccountActivity.this.getSharedPreferences("com.express.realname", Context.MODE_PRIVATE);
        String express_text = sPreferences.getString("express.express","");
        String express_company_text = sPreferences.getString("express.express_company","");
        String express_number_text = sPreferences.getString("express.express_number","");
        setContentView(R.layout.account);
        express = (EditText)findViewById(R.id.express);
        if(!express_text.equals("")){
            express.setText(express_text);
        }
        express_company = (EditText)findViewById(R.id.express_company);
        if(!express_company_text.equals("")){
            express_company.setText(express_company_text);
        }
        express_number = (EditText)findViewById(R.id.express_number);
        if(!express_number_text.equals("")){
            express_number.setText(express_number_text);
        }

        Button express_save = (Button)findViewById(R.id.express_save);
        express_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sPreferences = AccountActivity.this.getSharedPreferences("com.express.realname", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPreferences.edit();
                editor.putString("express.express", express.getText().toString());
                editor.putString("express.express_company", express_company.getText().toString());
                editor.putString("express.express_number", express_number.getText().toString());
                editor.commit();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
