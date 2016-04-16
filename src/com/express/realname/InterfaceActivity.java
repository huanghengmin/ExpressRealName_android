package com.express.realname;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.content.DialogInterface;
import android.location.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import com.express.realname.account.AccountActivity;
import com.express.realname.domain.Person;
import com.zxing.activity.CaptureActivity;
import yishu.nfc.YSnfcCardReader.NFCardReader;
import yishu.nfc.YSnfcCardReader.IdentityCard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.OnTouchListener;

public class InterfaceActivity extends Activity {
    private TextView title;
    private TextView name;
    private TextView nametext;
    private TextView sex;
    private TextView sextext;
    private TextView mingzu;
    private TextView mingzutext;
    private TextView birthday;
    private TextView birthdaytext;
    private TextView address;
    private TextView addresstext;
    private TextView number;
    private TextView numbertext;
    private TextView qianfa;
    private TextView qianfatext;
    private TextView start;
    private TextView starttext;
    //    private TextView end;
//    private TextView endtext;
    private TextView dncodetext;
    private TextView dncode;
    private Person person;
    private TextView Readingtext;
    private ImageView idimg;
    private ImageButton imageButton;
    private static Button onredo;
    private Context tcontext;
    private static String DB_NAME;
    private static final int DB_VERSION = 2;
    private static DatabaseHelper mOpenHelper;
    private static SQLiteDatabase db;
    private com.express.realname.gps.Location location = null;
    private static final String TAG = "GPS Services";
    public LocationManager lm;

    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    private static final int SETTING_SERVER_IP = 11;
    private static final int SETTING_BT = 22;
    //    private ArrayList<String> IPArray = null;
    public static String remoteIPA = "";
    private int mode = 2;//2, NFC;
//    public static int AssignPort = 19158;
    public static int AssignPort = 9018;

    private NfcAdapter mAdapter = null;
    private NFCardReader nfcReadCardAPI;

    private PendingIntent pi = null;
    //滤掉组件无法响应和处理的Intent
    private IntentFilter tagDetected = null;
    private String[][] mTechLists;
    private Intent inintent = null;

    private int readflag = 0;
    private static final int PHOTO_PIC = 1;

    public static final int MESSAGE_VALID_NFCBUTTON = 16;
    public static final int MESSAGE_VALID_PROCESS = 1001;
    /**
     * 服务器无法连接
     */
    public final static int SERVER_CANNOT_CONNECT = 90000001;
    /**
     * 开始读卡
     */
    public final static int READ_CARD_START = 10000001;
    /**
     * 读卡进度
     */
    public final static int READ_CARD_PROGRESS = 20000002;
    /**
     * 读卡成功
     */
    public final static int READ_CARD_SUCCESS = 30000003;

    public final static int READ_CARD_RESULT = 30000004;

    /**
     * 读照片成功
     */
    public final static int READ_PHOTO_SUCESS = 40000004;
    /**
     * 读卡失败
     */
    public final static int READ_CARD_FAILED = 90000009;

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

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainyishu);
        tcontext = InterfaceActivity.this;
//        IPArray = new ArrayList<String>();
        onredo = (Button) findViewById(R.id.scale);
        onredo.setOnTouchListener(new OnTouchListener() {

            //			@Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                }
                return true;
            }
        });

        title = (TextView) findViewById(R.id.title);
        name = (TextView) findViewById(R.id.name);
        sex = (TextView) findViewById(R.id.sex);
        nametext = (TextView) findViewById(R.id.nametext);
        sextext = (TextView) findViewById(R.id.sextext);
        mingzu = (TextView) findViewById(R.id.mingzu);
        mingzutext = (TextView) findViewById(R.id.mingzutext);
        birthday = (TextView) findViewById(R.id.birthday);
        birthdaytext = (TextView) findViewById(R.id.birthdaytext);
        address = (TextView) findViewById(R.id.address);
        addresstext = (TextView) findViewById(R.id.addresstext);
        number = (TextView) findViewById(R.id.number);
        numbertext = (TextView) findViewById(R.id.numbertext);
        qianfa = (TextView) findViewById(R.id.qianfa);
        qianfatext = (TextView) findViewById(R.id.qianfatext);
        start = (TextView) findViewById(R.id.start);
        starttext = (TextView) findViewById(R.id.starttext);
//        end = (TextView) findViewById(R.id.end);
//        endtext = (TextView) findViewById(R.id.endtext);
        Readingtext = (TextView) findViewById(R.id.Readingtext);
        dncodetext = (TextView) findViewById(R.id.dncodetext);
        dncode = (TextView) findViewById(R.id.dncode);
        imageButton = (ImageButton) findViewById(R.id.next);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (person != null) {
                    Intent i = new Intent(InterfaceActivity.this, QRCodeActivity.class);
                    i.putExtra("person", person);
                    startActivity(i);
                } else {
                    new AlertDialog.Builder(InterfaceActivity.this).setTitle("提示")//设置对话框标题
                            .setMessage("请重新读取身份信息！")//设置显示的内容
                            .setNegativeButton("返回", new DialogInterface.OnClickListener() {//添加返回按钮
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//响应事件
                                    Log.i("alert dialog", " 请重新读取身份信息！");
                                }
                            }).show();//在按键响应事件中显示此对话框
                }
            }
        });
        if (mode == 2) title.setText("身份证识别NFC模式");
        name.setText("姓名：");
        sex.setText("性别：");
        mingzu.setText("民族：");
        birthday.setText("出生年月：");
        address.setText("地址：");
        number.setText("身份证号码：");
        qianfa.setText("签发机关：");
        start.setText("有效时间：");
//        end.setText("生效时间：");
        dncode.setText("DN码：");
//        end.setVisibility(View.GONE);
        idimg = (ImageView) findViewById(R.id.idimg);
        Readingtext.setVisibility(View.GONE);
        Readingtext.setText("      正在读卡，请稍候...");
        Readingtext.setTextColor(Color.RED);

        DB_NAME = "/sdcard/yishu/usingservice.db";
        mOpenHelper = new DatabaseHelper(this);
        db = mOpenHelper.getWritableDatabase();
        Cursor paramBundle = db.rawQuery("select IP from setipconfig where ID=1;", null);
        if ((paramBundle.moveToFirst()) && (!paramBundle.isAfterLast())) {
            remoteIPA = paramBundle.getString(0).trim();
        }
        paramBundle.close();
        db.close();
        mOpenHelper.close();

//        IPArray.add(remoteIPA);

        nfcReadCardAPI = new NFCardReader(mHandler, this);
//		nfcReadCardAPI.setlogflag(0);//0, nolog; //1, with log;
        nfcReadCardAPI.setTheServer(remoteIPA, AssignPort);
        registerGPS();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        nfcReadCardAPI.writeFile("test 101");
        if (mode == 1) {
            new AlertDialog.Builder(InterfaceActivity.this) .setTitle("提示").setMessage("目前处于OTG模式！").setPositiveButton("确定", null).show();
            return;
        }
        if (mode == 3) {
            new AlertDialog.Builder(InterfaceActivity.this).setTitle("提示").setMessage("目前处于蓝牙模式！").setPositiveButton("确定", null).show();
            return;
        }
        nfcReadCardAPI.writeFile("test 102");
        if (readflag == 1) {
//			return;
        }
        nfcReadCardAPI.writeFile("test 103");
        inintent = intent;
        readflag = 1;
        nametext.setText("");
        sextext.setText("");
        mingzutext.setText("");
        birthdaytext.setText("");
        addresstext.setText("");
        numbertext.setText("");
        qianfatext.setText("");
        starttext.setText("");
//        endtext.setText("");
        dncodetext.setText("");
        person = null;
        idimg.setImageBitmap(null);
        Readingtext.setText("      正在读卡，请稍候...");
        Readingtext.setVisibility(View.VISIBLE);
        nfcReadCardAPI.writeFile("test 104");
        mHandler.sendEmptyMessageDelayed(MESSAGE_VALID_NFCBUTTON, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mode == 1) return;
        if (mode == 3) return;
//		if (mAdapter!=null)	stopNFC_Listener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Boolean enablenfc = nfcReadCardAPI.EnableSystemNFCMessage();
        if (enablenfc == true) {
        } else {
            new AlertDialog.Builder(InterfaceActivity.this).setTitle("提示").setMessage("NFC初始化失败！").setPositiveButton("确定", null).show();
            Readingtext.setVisibility(View.GONE);
            onredo.setEnabled(true);
            onredo.setFocusable(true);
            onredo.setBackgroundResource(R.drawable.sfz_dq);
        }
//		if (mAdapter!=null) startNFC_Listener();
    }

    /*private void startNFC_Listener() {
        mAdapter.enableForegroundDispatch(this, pi, new IntentFilter[] { tagDetected }, mTechLists);
    }

    private void stopNFC_Listener() {
        mAdapter.disableForegroundDispatch(this);
    }

    private void init_NFC() {
        pi = PendingIntent.getActivity(this, 0, new Intent(this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);//.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        mTechLists = new String[][] { new String[] { NfcB.class.getName() } };
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setip:
                Intent serverIntent1 = new Intent(this, SetServerIPActivity.class);
                startActivityForResult(serverIntent1, SETTING_SERVER_IP);
                return true;
            case R.id.setocr:
                this.title.setText("目前处于OCR模式");
                return true;
            case R.id.setusenfc:
                this.title.setText("目前处于NFC模式");
                return true;
            case R.id.setserverip:
                Intent intent = new Intent(this, SetAdminServerIPActivity.class);
                startActivityForResult(intent, SETTING_SERVER_IP);
                return true;
            case R.id.user:
                Intent i = new Intent(this, AccountActivity.class);
                startActivity(i);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_PIC:
                    String result = data.getExtras().getString("result");
//                    Toast.makeText(this, result, Toast.LENGTH_LONG);
                    person.setShapeCode(result);
                    String date = format.format(new Date());
                    person.setSendTime(date);
                    break;
                default:
                    break;
            }
        } else if (requestCode == SETTING_SERVER_IP) {
//            IPArray.clear();
            mOpenHelper = new DatabaseHelper(this);
            db = mOpenHelper.getWritableDatabase();
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

            db.close();
            mOpenHelper.close();
//            IPArray.add(remoteIPA);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void registerGPS() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //判断GPS是否正常启动
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "请开启GPS导航...", Toast.LENGTH_SHORT).show();

            //返回开启GPS导航设置界面
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
            return;
        }

        //为获取地理位置信息时设置查询条件
        String bestProvider = lm.getBestProvider(getCriteria(), true);
        //获取位置信息
        //如果不设置查询要求，getLastKnownLocation方法传人的参数为LocationManager.GPS_PROVIDER
        Location location = lm.getLastKnownLocation(bestProvider);
//        Location location= lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        updateView(location);
        //监听状态
        lm.addGpsStatusListener(listener);
        //绑定监听，有4个参数
        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        //参数2，位置信息更新周期，单位毫秒
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息
        //参数4，监听
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新

        // 1秒更新一次，或最小位移变化超过1米更新一次；
        //注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
//        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);
    }

    //位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }
    };

    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i(TAG, "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i(TAG, "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        iters.next();
                        count++;
                    }
                    Log.i(TAG, "搜索到：" + count + "颗卫星");
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i(TAG, "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i(TAG, "定位结束");
                    break;
            }
        }

        ;
    };

    /**
     * 实时更新文本内容
     *
     * @param location
     */
    private void updateView(Location location) {
        if (location != null) {
            this.location = new com.express.realname.gps.Location();
            this.location.setLongitude(String.valueOf(location.getLongitude()));
            this.location.setLatitude(String.valueOf(location.getLatitude()));
        }
    }

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(locationListener);
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SERVER_CANNOT_CONNECT:
//                    new AlertDialog.Builder(InterfaceActivity.this).setTitle("提示").setMessage("没有连接到服务器！").setPositiveButton("确定", null).show();
                    Readingtext.setText("      没有连接到服务器，请重新配置服务器地址...");
                    onredo.setEnabled(true);
                    onredo.setFocusable(true);
                    onredo.setBackgroundResource(R.drawable.sfz_dq);
                    break;
                case READ_CARD_START:
                    Readingtext.setVisibility(View.VISIBLE);
                    break;
                case READ_CARD_PROGRESS:
                    Readingtext.setText("      正在读卡，请稍候...");
                    break;
                case READ_CARD_SUCCESS:
                    IdentityCard identityCard = (IdentityCard) msg.obj;
                    if (person == null) {
                        person = new Person();
                    }
                    nametext.setText(identityCard.name);
                    person.setName(identityCard.name);
                    sextext.setText(identityCard.sex);
                    person.setSex(identityCard.sex);
                    mingzutext.setText(identityCard.ethnicity);
                    person.setNation(identityCard.ethnicity);
                    birthdaytext.setText(identityCard.birth);
                    person.setBirthday(identityCard.birth);
                    addresstext.setText(identityCard.address);
                    person.setAddress(identityCard.address);
                    numbertext.setText(identityCard.cardNo);
                    person.setIdCard(identityCard.cardNo);
                    qianfatext.setText(identityCard.authority);
                    person.setSignDepart(identityCard.authority);
                    starttext.setText(identityCard.period);
                    person.setValidTime(identityCard.period);
                    dncodetext.setText(identityCard.DN);
                    person.setDN(identityCard.DN);
                    if (location != null) {
                        person.setLatitude(String.valueOf(location.getLatitude()));
                        person.setLongitude(String.valueOf(location.getLongitude()));
                    }
//                    endtext.setText(null);
                    onredo.setEnabled(true);
                    onredo.setFocusable(true);
                    onredo.setBackgroundResource(R.drawable.sfz_dq);
                    readflag = 0;
                    Readingtext.setVisibility(View.GONE);
                    break;
                case READ_PHOTO_SUCESS:
                    byte[] cardbmp = (byte[]) msg.obj;
                    Bitmap bm = BitmapFactory.decodeByteArray(cardbmp, 0, cardbmp.length);
                    idimg.setImageBitmap(bm);
                    if (person == null)
                        person = new Person();
                    person.setBitmap(cardbmp);
                    break;
                case READ_CARD_FAILED:
                    int error = msg.arg1;
//                    new AlertDialog.Builder(InterfaceActivity.this).setTitle("提示").setMessage((String) msg.obj + "  " + error).setPositiveButton("确定", null).show();
                    Readingtext.setText("      "+(String) msg.obj);
                    onredo.setEnabled(true);
                    onredo.setFocusable(true);
                    onredo.setBackgroundResource(R.drawable.sfz_dq);
//                    Readingtext.setVisibility(View.GONE);
                    break;
                case MESSAGE_VALID_NFCBUTTON:
                    nfcReadCardAPI.setTheServer(remoteIPA, AssignPort);
                    Boolean enablenfc = nfcReadCardAPI.EnableSystemNFCMessage();
                    if (enablenfc == true) {
                        Boolean judgenfc = nfcReadCardAPI.isNFC(inintent);
                        if (judgenfc == true) {
                            nfcReadCardAPI.readCardWithIntent(inintent);
                        } else {
//                            new AlertDialog.Builder(InterfaceActivity.this) .setTitle("提示").setMessage("读卡失败！").setPositiveButton("确定", null).show();
                            Readingtext.setText("      读卡失败！");
//                            Readingtext.setVisibility(View.GONE);
                            onredo.setEnabled(true);
                            onredo.setFocusable(true);
                            onredo.setBackgroundResource(R.drawable.sfz_dq);
                        }
                    } else {
//                        new AlertDialog.Builder(InterfaceActivity.this).setTitle("提示").setMessage("读卡失败！").setPositiveButton("确定", null).show();
                        Readingtext.setText("      读卡失败！");
//                        Readingtext.setVisibility(View.GONE);
                        onredo.setEnabled(true);
                        onredo.setFocusable(true);
                        onredo.setBackgroundResource(R.drawable.sfz_dq);
                    }
//                    Readingtext.setVisibility(View.GONE);
                    onredo.setEnabled(true);
                    onredo.setFocusable(true);
                    onredo.setBackgroundResource(R.drawable.sfz_dq);
                    break;
            }
        }
    };


}