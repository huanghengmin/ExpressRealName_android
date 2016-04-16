package com.express.realname;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.express.realname.domain.Person;
import com.express.realname.request.PostRequest;
import com.zxing.activity.CaptureActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class QRCodeActivity extends Activity implements OnClickListener {

    private static final int PHOTO_PIC = 1;
    private Person person = null;
    private static final int SETTING_SERVER_IP = 11;
    private static String DB_NAME;
    private static final int DB_VERSION = 2;
    private static DatabaseHelper mOpenHelper;
    private static SQLiteDatabase db;
    private TextView qr_code;

    public static String remoteIPB="";
    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");


    public static String DB_CREATE_TABLE_IPCONFIG = "CREATE TABLE IF NOT EXISTS [setipconfig] " +
            "([ID] INTEGER PRIMARY KEY,[IP] VARCHAR)";
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e("ERROR", "test here DBOperation this channel onCreate");
            db.execSQL(DB_CREATE_TABLE_IPCONFIG);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code);
        //    得到跳转到该Activity的Intent对象
        Intent intent = getIntent();
        person = (Person)intent.getSerializableExtra("person");
//        findViewById(R.id.read_camera).setOnClickListener(this);
        findViewById(R.id.success).setOnClickListener(this);
        qr_code = (TextView)findViewById(R.id.qr_code);
        DB_NAME = "/sdcard/yishu/usingservice.db";
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        Cursor paramBundle = db.rawQuery("select IP from setipconfig where ID=2;", null);
        if ((paramBundle.moveToFirst()) && (!paramBundle.isAfterLast())) {
            remoteIPB = paramBundle.getString(0).trim();
        }
        paramBundle.close();
        db.close();
        mOpenHelper.close();

        person.setShapeCode(null);
        qr_code.setText("");
        //跳转到拍照界面扫描二维码
        Intent i = new Intent(this, CaptureActivity.class);
        startActivityForResult(i, PHOTO_PIC);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.option_menu_readcode, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setserveripcode:
                Intent intent = new Intent(this, SetAdminServerIPActivity.class);
                startActivityForResult(intent, SETTING_SERVER_IP);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_PIC:
                    String result = data.getExtras().getString("result");
//                    Toast.makeText(this, result, Toast.LENGTH_LONG);
                    person.setShapeCode(result);
                    String date = format.format(new Date());
                    person.setSendTime(date);
                    qr_code.setText(result);
                    break;
                default:
                    break;
            }
        }else  if(requestCode==SETTING_SERVER_IP){
            mOpenHelper = new DatabaseHelper(this);
            db = mOpenHelper.getWritableDatabase();
            String sql = "select IP from setipconfig where ID=2;";
            Cursor cur;
            cur = db.rawQuery(sql, null);
            if (cur.moveToFirst()==true)
            {
                while (!cur.isAfterLast()) {
                    remoteIPB = cur.getString(0).trim();
                    break;
                }
            }
            cur.close();

            db.close();
            mOpenHelper.close();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
          /*  case R.id.read_camera:
                    person.setShapeCode(null);
                    qr_code.setText("");
                //跳转到拍照界面扫描二维码
                    Intent intent = new Intent(this, CaptureActivity.class);
                    startActivityForResult(intent, PHOTO_PIC);
                break;*/
            case R.id.success:
                SharedPreferences sPreferences = this.getSharedPreferences("com.express.realname", Context.MODE_PRIVATE);
                String express = sPreferences.getString("express.express", "张三");
                String express_company = sPreferences.getString("express.express_company", "申通快递");
                String express_number = sPreferences.getString("express.express_number", "ABCD007");

                person.setExpress(express);
                person.setExpress_company(express_company);
                person.setExpress_number(express_number);
                if(person.getShapeCode()!=null){
                    //提交数据到后台服务器
                    if(remoteIPB==null||"".equals(remoteIPB)){
                        new AlertDialog.Builder(this).setTitle("提示")//设置对话框标题
                                .setMessage("请先配置管理服务器地址！")//设置显示的内容
                                .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//响应事件
                                        Log.i("alert dialog", " 请先配置管理服务器地址！");
                                    }
                                }).show();//在按键响应事件中显示此对话框
                        break;
                    }
                    PostRequest postRequest = new PostRequest(this,remoteIPB,person);
                    postRequest.postData(new PostRequest.OnPostListener() {
                        @Override
                        public void onPostOk(String msg) {
                            Toast.makeText(QRCodeActivity.this,msg,Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onPostErr(String msg) {
                            Toast.makeText(QRCodeActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    });
                    QRCodeActivity.this.finish();
                }else {
                    new AlertDialog.Builder(this).setTitle("提示")//设置对话框标题
                            .setMessage("请重新扫描条形码信息！")//设置显示的内容
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//响应事件
                                    Log.i("alert dialog", " 请重新扫描条形码信息！");
                                }
                            }).show();//在按键响应事件中显示此对话框
                }
                break;
            default:
                break;
        }

    }
}
