package com.mitlab.zusliu.User.Interface;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.app.Notification;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ListView;
import android.view.View;


import com.THLight.USBeacon.App.Lib.BatteryPowerData;
import com.THLight.USBeacon.App.Lib.iBeaconData;
import com.THLight.USBeacon.App.Lib.iBeaconScanManager;
import com.mitlab.zusliu.R;
import com.mitlab.zusliu.Update.List.View.ListAdapter;
import com.mitlab.zusliu.Update.List.View.ListItem;

import android.os.Vibrator;


import system.config.Setup;

//////////////////////////////////
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;
///////////////////////////////////

public class MainActivity extends Activity implements iBeaconScanManager.OniBeaconScan {


    // 初始旗標
    boolean[] flag = new boolean[5];

    // 初始化人機介面物件
    public ListView l = null;

    // 初始化裝置掃描管理器物件
    public iBeaconScanManager scanner = null;

    // 初始化清單配置器物件
    public ListAdapter ListAdapter = null;

    // 初始化藍牙配置器物件
    public BluetoothAdapter BluetoothAdapter = null;

    // 參數：裝置清單(資料清單)
    public ArrayList<ScannedBeacon> beacons = new ArrayList<ScannedBeacon>();


    //public Button button;
    public FrameLayout frame_1;    //Floor 1 frame
    public FrameLayout frame_2;    //按鍵frame

    public LinearLayout side_layout_1;     //sidebar linear_layout

    public Button btn_1,btn_2,btn_3;  //商家總覽 地點標記 按鍵

    public ToggleButton toggle_btn_1,toggle_btn_2;  //側滑選單 樓層選取

    public ImageButton [] img_btn_mark = new ImageButton[15];   //地標按鍵
    public static TextView mark_state;

    static boolean btn_1_flag = false;  //"+"按鍵狀態
    static boolean btn_2_flag = false;  //地點標記按鍵狀態
    static boolean btn_3_flag = false;  //網站導覽按鍵狀態

    static boolean [] flag_mark_btn =  {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};  //地標被點選狀態  靠近
    static boolean [] flag_mark_btn2 = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};  //地點標記狀態
    static boolean [] flag_mark_btn3 = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};  //網站導覽狀態
    // 儲存地標位置
    /////////////////////////////////0   1   2   3   4   5   6   7   8   9   10
    static int [] mark_position_X = {  5,130,270,410,550,560, 5,130,270,410,550};
    static int [] mark_position_Y = {100,100,100,100,100,200,350,350,350,350,350};
    static int [] beacon_num      = {  5, 81, 30, 41, 42, 43, 82,  6, 91, 92, 93,  3,773,772,  1,  771,  2};//第一根最後對調過 10不要
    static int [] img_btn_state   = {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0};
    static String [] web_info     = {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17"};
    static int [] flag_stable_state = {0,  0,  0,  0,  0,  0,  0,  0,  0,   0,  0,  0,      0,0,0,0,0};  //判斷beacon穩定

    static int choose_one = 0;    //只讓一個點顯示
    static int pre_beacon = 0;
    static int my_floor = 1;
    static int beacon_in  = 0;
    static int poumadon = 0;
    public static int user_place = 100;
    public static int beacon_amount_total = 17; //12
    static int beacon_amount = 11;
    static String result = "";
    Timer timer = new Timer();;    //宣告一個時間函示

    static String mark_state_text = "";

    Intent intent_1 = new Intent(); //要傳往floor_1的intent

    private static MainActivity instance;///////////////////////////////////////////////////////////

    // TODO 方法：主程式
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout layout=(FrameLayout) findViewById(R.id.floor1_frame);
        final DrawView view=new DrawView(this);
        view.setMinimumHeight(500);
        view.setMinimumWidth(300);
        setTitle("Floor 1");
        // Step.01(初始化流程) 取得人機介面物件
        this.l = (ListView) findViewById(R.id.myListView);
        // Step.02(初始化流程) 取得裝置掃描管理器(iBeaconScanManager)物件
        this.scanner = new iBeaconScanManager(this, this);
        // Step.03(初始化流程) 取得清單配置器(ListAdapter)物件
        // Step.04(初始化流程) 取得藍牙(bluetooth)服務
        this.ListAdapter = new ListAdapter(this);
        this.BluetoothAdapter = this.BluetoothAdapter.getDefaultAdapter();
        // Step.05(初始化流程) 檢查藍牙(bluetooth)狀態
        if (!this.BluetoothAdapter.isEnabled()) {
            // turn on bluetooth device
            Intent intent = new Intent(this.BluetoothAdapter.ACTION_REQUEST_ENABLE);
            MainActivity.this.startActivityForResult(intent, Setup.REQ_ENABLE_BT);
        }
        // Step.06(初始化流程) 處理器(handler)物件執行

        this.handler.sendEmptyMessageDelayed(Setup.REQ_SCAN_BEACON, 0);
        this.handler.sendEmptyMessageDelayed(Setup.REQ_UPDATE_BEACON, Setup.TIME_BEACON_UPDATE);
        // Step.07(初始化流程) 設定清單配置器
        this.l.setAdapter(this.ListAdapter);
///////////////////////////////////////////////////////////////////////////////////
        frame_1 = new FrameLayout(this);
        frame_1 = (FrameLayout)findViewById(R.id.floor1_frame);

        frame_2 = new FrameLayout(this);
        frame_2 = (FrameLayout)findViewById(R.id.frame_layout_2);

        side_layout_1 = new LinearLayout(this);
        side_layout_1 = (LinearLayout)findViewById(R.id.sidebar);

        toggle_btn_1 = (ToggleButton)findViewById(R.id.sidebar_toggle_1);
        toggle_btn_2 = (ToggleButton)findViewById(R.id.sidebar_toggle_2);

        btn_1 = (Button)findViewById(R.id.button3);
        btn_2 = (Button)findViewById(R.id.button4);
        btn_3 = (Button)findViewById(R.id.button5);

        mark_state = new TextView(this);

        frame_2.addView(mark_state);
        mark_state.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        display_mark_btn(beacon_amount,mark_position_X,mark_position_Y,img_btn_mark,frame_1,this);


        Thread thread = new Thread(mutiThread);     //從網路抓資料
        thread.start();
        mark_state.setText(result); //將網站上的資料顯示
        mark_state.setTextSize(50);

        ///////////////////////////////////////////////////////////跑馬燈效果
        mark_state.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mark_state.setSingleLine(true);
        mark_state.setSelected(true);
        ///////////////////////////////////////////////////////////

        toggle_btn_1.setOnClickListener(new View.OnClickListener() {    //選擇顯示地圖1

            public void onClick(View v) {       //選擇樓層按鍵事件_1
                // 當按鈕第一次被點擊時候響應的事件
                if (toggle_btn_1.isChecked()) {
                    my_floor = 1;
                    toggle_btn_2.setChecked(false);
                    Toast.makeText(MainActivity.this, "選擇樓層1", Toast.LENGTH_SHORT).show();
                }
                // 當按鈕再次被點擊時候響應的事件
                else {
                    toggle_btn_1.setChecked(true);
                    Toast.makeText(MainActivity.this, "取消選擇樓層1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toggle_btn_2.setOnClickListener(new View.OnClickListener() {    //選擇顯示地圖2
            public void onClick(View v) {       //選擇樓層按鍵事件_2
                // 當按鈕第一次被點擊時候響應的事件
                if (toggle_btn_2.isChecked()) {
                    my_floor = 2;
                    toggle_btn_1.setChecked(false);
                    Toast.makeText(MainActivity.this, "選擇樓層2", Toast.LENGTH_SHORT).show();
                    intent_1.setClass(MainActivity.this, Floor_choosen.class);
                    startActivity(intent_1);
                }
                // 當按鈕再次被點擊時候響應的事件
                else {
                    Toast.makeText(MainActivity.this, "取消選擇樓層2", Toast.LENGTH_SHORT).show();
                }
            }
        });




///////////////////////////////////////////////////////////////////////////////////
    }


    public static MainActivity getInstance() {
        return instance;
    }




    ///////////////////////////////////////////////////////////////////////////////////測試
    //TODO Beacon編號對應
    public static int correspod_beacon(int major,int minor){
        beacon_in = (major * 10) + minor;
        int num;
        for(num = 0;num < beacon_amount_total;num++){
            if(beacon_in == beacon_num[num]) break;
        }
        return num;
    }

    //TODO "+"按鍵
    public void buttonOnClick(View v) {
        // 寫要做的事...

        if(btn_2_flag==false && btn_3_flag==false){
            btn_1_flag = !btn_1_flag;
            if(btn_1_flag == true){
                btn_1.setVisibility(View.VISIBLE);
                btn_2.setVisibility(View.VISIBLE);
                btn_3.setVisibility(View.VISIBLE);
            }else{
                btn_1.setVisibility(View.INVISIBLE);
                btn_2.setVisibility(View.INVISIBLE);
                btn_3.setVisibility(View.INVISIBLE);
                btn_2_flag = false;
                btn_2.getBackground().setColorFilter(null);
                btn_3_flag = false;
                btn_3.getBackground().setColorFilter(null);
            }
        }

    }
    //TODO 商家資訊按鍵
    public void buttonOnClick_1(View v) {
        // 寫要做的事...
        if(btn_2_flag == false && btn_3_flag == false) {
            connect_to_web(0);
        }
    }
    //TODO 地點標記按鍵
    public void buttonOnClick_2(View v) {
        // 寫要做的事...
        if(btn_1_flag == true && btn_3_flag == false) btn_2_flag = !btn_2_flag;
        else {}
        if(btn_2_flag){
           // btn_2.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            btn_2.getBackground().setColorFilter(0xFFFFAA00, android.graphics.PorterDuff.Mode.MULTIPLY );
        }
        //else  btn_2.setBackgroundColor(getResources().getColor(android.R.color.white));
        else btn_2.getBackground().setColorFilter(null);

    }
    //TODO 網站導覽按鍵
    public void buttonOnClick_3(View v) {
        // 寫要做的事...
        if(btn_1_flag == true && btn_2_flag == false)btn_3_flag = !btn_3_flag;
        else {}
        if(btn_3_flag){
            for(int i = 0;i < beacon_amount;i++) flag_mark_btn3[i] = false;
           // btn_3.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            btn_3.getBackground().setColorFilter(0xFFFFAA00, android.graphics.PorterDuff.Mode.MULTIPLY );
        }else{
           // btn_3.setBackgroundColor(getResources().getColor(android.R.color.white));
            btn_3.getBackground().setColorFilter(null);
            for(int i = 0;i < beacon_amount;i++) {
                if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                    change_mark(i,2,img_btn_mark);
                }
                else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                    change_mark(i,1,img_btn_mark);
                }
                else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                    change_mark(i,3,img_btn_mark);
                }
                else {
                    change_mark(i, 0,img_btn_mark);
                }
            }
            for(int j = 0;j < beacon_amount;j++){
                if(flag_mark_btn3[j]==true) connect_to_web(j+1);
            }
        }
    }

    private Runnable mutiThread = new Runnable(){
        public void run(){

            try {
                URL url = new URL("http://140.118.122.242/test/test.php");
                // 開始宣告 HTTP 連線需要的物件，這邊通常都是一綑的
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 建立 Google 比較挺的 HttpURLConnection 物件
                connection.setRequestMethod("POST");
                // 設定連線方式為 POST
                connection.setDoOutput(true); // 允許輸出
                connection.setDoInput(true); // 允許讀入
                connection.setUseCaches(false); // 不使用快取
                connection.connect(); // 開始連線

                int responseCode =
                        connection.getResponseCode();
                // 建立取得回應的物件

                if(responseCode ==
                        HttpURLConnection.HTTP_OK){
                    // 如果 HTTP 回傳狀態是 OK ，而不是 Error
                    InputStream inputStream =
                            connection.getInputStream();
                    // 取得輸入串流
                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                    // 讀取輸入串流的資料
                    String box = ""; // 宣告存放用字串
                    String line = null; // 宣告讀取用的字串
                    while((line = bufReader.readLine()) != null) {
                        box += line + "\n";
                        // 每當讀取出一列，就加到存放字串後面
                    }
                    inputStream.close(); // 關閉輸入串流
                    result = box;// 把存放用字串放到全域變數

                    JSONArray jsonArray = new JSONArray(result);
                    String name;


                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String info = jsonObject.getString("info");
                        web_info[i] = info;
                        result = info;
                    }
                   // result = "***";
                    mark_state.setText(result); // 更改顯示文字

                }
                // 讀取輸入串流並存到字串的部分
                // 取得資料後想用不同的格式
                // 例如 Json 等等，都是在這一段做處理

            } catch(Exception e) {
               // result = e.toString();// 如果出事，回傳錯誤訊息
            }

            // 當這個執行緒完全跑完後執行
            runOnUiThread(new Runnable() {
                public void run() {
                    mark_state.setText(result); // 更改顯示文字
                }

            });
        }
    };


    //TODO 連結到網站
    public void connect_to_web(int a){
        Uri uri = Uri.parse("https://www.google.com");
        switch (a){
            case 0:
                uri = Uri.parse("http://140.118.122.242/test/beacon_test.php");
                break;
            case 1:
                uri = Uri.parse("https://www.nike.com/tw/");
                break;
            case 2:
                uri = Uri.parse("https://www.adidas.com.tw/");
                break;
            case 3:
                uri = Uri.parse("https://hk.puma.com/");
                break;
            case 4:
                uri = Uri.parse("https://www.ntust.edu.tw/home.php");
                break;
            case 5:
                uri = Uri.parse("https://www.apple.com/tw/");
                break;
            case 6:
                uri = Uri.parse("https://www.costco.com.tw/");
                break;
            case 7:
                uri = Uri.parse("https://www.fetnet.net/content/cbu/tw/index.html");
                break;
            case 8:
                uri = Uri.parse("https://shopee.tw/");
                break;
            case 9:
                uri = Uri.parse("https://shopping.pchome.com.tw/");
                break;
            case 10:
                uri = Uri.parse("https://www.uniqlo.com/tw/");
                break;
            case 11:
                uri = Uri.parse("https://www.cosmed.com.tw/");
                break;
            case 12:
                uri = Uri.parse("https://www.beddingworldbed.com.tw/");
                break;
            case 13:
                uri = Uri.parse("https://www.sushiexpress.com.tw/teishoku8/index/");
                break;
            case 14:
                uri = Uri.parse("https://www.9x9.com.tw/");
                break;
            case 15:
                uri = Uri.parse("https://mobile.mi.com/tw/");
                break;
            case 16:
                uri = Uri.parse("https://www.wangsteak.com.tw/");
                break;
            case 17:
                uri = Uri.parse("https://www.worldgymtaiwan.com");
                break;


        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public static void connect_to_web(int a,Context context){
        Uri uri = Uri.parse("https://www.google.com");
        switch (a){
            case 0:
                uri = Uri.parse("http://140.118.122.242/test/beacon_test.php");
                break;
            case 1:
                uri = Uri.parse("https://www.nike.com/tw/");
                break;
            case 2:
                uri = Uri.parse("https://www.adidas.com.tw/");
                break;
            case 3:
                uri = Uri.parse("https://hk.puma.com/");
                break;
            case 4:
                uri = Uri.parse("https://www.ntust.edu.tw/home.php");
                break;
            case 5:
                uri = Uri.parse("https://www.apple.com/tw/");
                break;
            case 6:
                uri = Uri.parse("https://www.costco.com.tw/");
                break;
            case 7:
                uri = Uri.parse("https://www.fetnet.net/content/cbu/tw/index.html");
                break;
            case 8:
                uri = Uri.parse("https://shopee.tw/");
                break;
            case 9:
                uri = Uri.parse("https://shopping.pchome.com.tw/");
                break;
            case 10:
                uri = Uri.parse("https://www.uniqlo.com/tw/");
                break;
            case 11:
                uri = Uri.parse("https://www.cosmed.com.tw/");
                break;
            case 12:
                uri = Uri.parse("https://www.beddingworldbed.com.tw/");
                break;
            case 13:
                uri = Uri.parse("https://www.sushiexpress.com.tw/teishoku8/index/");
                break;
            case 14:
                uri = Uri.parse("https://www.9x9.com.tw/");
                break;
            case 15:
                uri = Uri.parse("https://mobile.mi.com/tw/");
                break;
            case 16:
                uri = Uri.parse("https://www.wangsteak.com.tw/");
                break;
            case 17:
                uri = Uri.parse("https://www.worldgymtaiwan.com");
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }
    public static void display_mark_btn(int x,int [] pos_x,int [] pos_y,ImageButton [] mark,FrameLayout frame,Context context){
        for(int i = 0;i < x;i++){
            mark[i] = new ImageButton(context);
            mark[i].setImageResource(R.drawable.map_mark);
            //img_btn_mark[i].setX(mark_position_X[i]);
            mark[i].setX(getPixelsFromDp(pos_x[i],context)/2);
            //img_btn_mark[i].setY(mark_position_Y[i]);
            mark[i].setY(getPixelsFromDp(pos_y[i],context)/2);
            frame.addView(mark[i]);
            mark[i].getLayoutParams().height = getPixelsFromDp(65,context);
            mark[i].getLayoutParams().width = getPixelsFromDp(65,context);
            mark[i].setBackgroundColor(0xFFFFFF);
            //img_btn_click(i,mark,context);
            img_btn_click(i,mark,flag_mark_btn,flag_mark_btn2,flag_mark_btn3,context);

        }
    }


    //TODO Pixel 轉 DP
    public static int getPixelsFromDp(int px, Context context){
        int dp = (int) (px * getDensity(context));
        return dp;
    }
    public static float getDensity(Context context){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (float) metrics.density;
    }

    //TODO 地標按鍵觸發
    public static void img_btn_click(int x, final ImageButton [] mark,final boolean [] flag_1,final boolean [] flag_2,final boolean [] flag_3, final Context context){
        final int a = x;
        mark[a].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_2_flag){
                    flag_2[a] = !flag_2[a];
                    if(flag_2[a] == true){
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_2[i] = false;
                            if(flag_1[i] == true){
                                change_mark(i,1,mark);
                            }
                            else {
                                change_mark(i, 0,mark);
                            }
                        }
                        flag_2[a] = true;
                        change_mark(a,3,mark);
                        Toast.makeText(context,"number " + a, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        change_mark(a,0,mark);
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_2[i] = false;
                            if (flag_1[i] == true) {
                                change_mark(i,1,mark);
                            }
                            else {
                                change_mark(i, 0,mark);
                            }
                        }
                    }
                }
                else if(btn_3_flag){

                    flag_3[a] = !flag_3[a];
                    if(flag_3[a] == true){
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_3[i] = false;
                            if (flag_1[i] == true && flag_2[i] == true) {
                                change_mark(i,2,mark);
                            }
                            else if(flag_1[i] == true && flag_2[i] == false){
                                change_mark(i,1,mark);
                            }
                            else if(flag_1[i] == false && flag_2[i] == true){
                                change_mark(i,3,mark);
                            }
                            else {
                                change_mark(i, 0,mark);
                            }
                        }
                        flag_3[a] = true;
                        change_mark(a,4,mark);
                        Toast.makeText(context,"number " + a, Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i = 0;i < beacon_amount;i++) {
                            flag_3[i]=false;
                            if (flag_1[i] == true && flag_2[i] == true) {
                                change_mark(i,2,mark);
                            }else if(flag_1[i] == true && flag_2[i] == false){
                                change_mark(i,1,mark);
                            }else if(flag_1[i] == false && flag_2[i] == true){
                                change_mark(i,3,mark);
                            }else {
                                change_mark(i, 0,mark);
                            }
                        }
                    }

                }else if(!btn_2_flag && !btn_3_flag){
                    Toast.makeText(context,"number ==== " + a, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public static void change_mark(int x, int state,ImageButton [] mark){
        // TODO 方法：選擇第X個 改變圖標 0:黑色 1:紅色
        switch(state){
            case 0:
                //Log.v("s_h_b", String.valueOf(mark[x].getLayoutParams().height));
                mark[x].getLayoutParams().height = 65;
                mark[x].getLayoutParams().width = 65;
                mark[x].setImageResource(R.drawable.map_mark);
                Log.v("e_h_b", String.valueOf(mark[x].getLayoutParams().height));
                break;
            case 1:
                Log.v("s_h_r", String.valueOf(mark[x].getLayoutParams().height));
                mark[x].getLayoutParams().height = 65;
                mark[x].getLayoutParams().width = 65;
                mark[x].setImageResource(R.drawable.red_map_mark);
                Log.v("e_h_r", String.valueOf(mark[x].getLayoutParams().height));
                //mark_state.setText(web_info[x]);
                /*if(poumadon == 0){
                    //result = web_info[x];
                    //mark_state.setText(result);
                    mark_state.setText(web_info[a]);
                    Log.v("pouma", String.valueOf(a));
                    //mark_state.setText(web_info[x+11]);
                }*/
                break;
            case 2:
                mark[x].getLayoutParams().height = 65;
                mark[x].getLayoutParams().width = 65;
                mark[x].setImageResource(R.drawable.light_mark);
                break;
            case 3:
                mark[x].getLayoutParams().height = 65;
                mark[x].getLayoutParams().width = 65;
                mark[x].setImageResource(R.drawable.blue_mark);
                break;
            case 4:
                mark[x].getLayoutParams().height = 65;
                mark[x].getLayoutParams().width = 65;
                mark[x].setImageResource(R.drawable.pin);
                break;
            default:
                mark[x].getLayoutParams().height = 65;
                mark[x].getLayoutParams().width = 65;
                mark[x].setImageResource(R.drawable.map_mark);
                break;
        }
        img_btn_state[x] = state;
    }
    public int bitcount(int  n){
        int count = 0;
        if(n < 0){                //如果N<0,把最高位1去掉
            n += 2147483647;    //不能加超過範圍
            n += 1;
        }
        while(n != 0){
            count++;
            n &= (n - 1);
        }
        return count;
    }

    //TODO 判斷beacon距離改變狀態
    public void beacon_state(int major,int minor,int rssi){
        if(-rssi < 60){
            flag_stable_state[correspod_beacon(major, minor)] = flag_stable_state[correspod_beacon(major, minor)] *2 +1;  //update the flag
            if(user_place < beacon_amount_total ){
                if( bitcount(flag_stable_state[correspod_beacon(major, minor)]) > bitcount(flag_stable_state[user_place]) ){  //如果使用這位置改變
                    flag_stable_state[user_place] = 0;
                    if(correspod_beacon(major, minor) < beacon_amount){    //人在一樓
                        if(!flag_mark_btn2[user_place] && !flag_mark_btn3[user_place] /*&& (user_place<beacon_amount)*/){
                            if(user_place<beacon_amount)change_mark(user_place, 0,img_btn_mark);           //把原本位置圖片重製
                        }
                        flag_mark_btn[user_place] = false;                                         //把flag原先位置重置
                        user_place = correspod_beacon(major, minor);                              //把原先位置重置
                        if(!flag_mark_btn2[user_place] && !flag_mark_btn3[user_place])change_mark(user_place, 1,img_btn_mark);           //如果沒有被地點標記或網站導覽，改位置圖像
                        flag_mark_btn[user_place] = true;                                                                         //把flag位置更新
                        poumadon = 0;
                        mark_state.setText(web_info[user_place]);
                        Log.v("we", String.valueOf(user_place));
                        Log.v("s_h_b", "4");//跑馬燈更新
                    }
                    //else if(correspod_beacon(major, minor) >= beacon_amount){      //人在二樓
                    else{
                        Log.v("floor0", "人在爾樓");//跑馬燈更新
                        flag_mark_btn[user_place] = false;                                         //把flag原先位置重置
                        if(!flag_mark_btn2[user_place] && !flag_mark_btn3[user_place]/* && (user_place<beacon_amount)*/ ){
                            if(user_place<beacon_amount)change_mark(user_place, 0,img_btn_mark);           //把原本位置圖片重製
                        }
                        user_place = correspod_beacon(major, minor);                              //把原先位置重置
                        mark_state.setText(web_info[user_place]);

                        Log.v("we", String.valueOf(user_place));
                        //flag_mark_btn[user_place] = true;
                    }
                }
                else if(bitcount(flag_stable_state[correspod_beacon(major, minor)]) < bitcount(flag_stable_state[user_place])){    //位置沒變
                    poumadon = 1;
                    if(!flag_mark_btn2[user_place] && !flag_mark_btn3[user_place] && (user_place<beacon_amount))change_mark(user_place, 1,img_btn_mark);           //把原本位置圖片重製
                    Log.v("floor0", "位置沒變");//跑馬燈更新
                }
                else{
                    Log.v("floor0", "位置沒變");//跑馬燈更新
                }
            }
            else{    //地一次掃描
                if(correspod_beacon(major, minor) < beacon_amount){    //人在一樓
                    if(my_floor == 1){     //介面在一樓
                        if(!flag_mark_btn2[correspod_beacon(major, minor)] && !flag_mark_btn3[correspod_beacon(major, minor)]){
                            user_place = correspod_beacon(major, minor);                              //把原先位置重置
                            Log.v("s_h_b", "5");//跑馬燈更新
                            change_mark(user_place, 1,img_btn_mark);           //如果沒有被地點標記或網站導覽，改位置圖像
                            flag_mark_btn[user_place] = true;                                                                         //把flag位置更新
                            poumadon = 0;                                                                                               //跑馬燈更新
                        }
                    }
                }
                else if(correspod_beacon(major, minor) >= beacon_amount){      //人在二樓
                    if(my_floor == 2){
                        user_place = correspod_beacon(major, minor);                              //把原先位置重置
                        //intent_1.putExtra("Beacon_number",user_place - beacon_amount);       //要傳往另一個activity的資料
                      //  f1.my_place = correspod_beacon(major, minor) - beacon_amount;
                    }
                }
            }
        }else{
            //flag_stable_state[correspod_beacon(major, minor)] = flag_stable_state[correspod_beacon(major, minor)] * 2;
            flag_stable_state[correspod_beacon(major, minor)] *= 2;
            //if( flag_stable_state[correspod_beacon(major, minor)] == 0 && !flag_mark_btn2[correspod_beacon(major, minor)] && !flag_mark_btn3[correspod_beacon(major, minor)]) change_mark(correspod_beacon(major, minor),0,img_btn_mark);
        }
        Log.v("floor0", String.valueOf(poumadon));//跑馬燈更新
    //Floor_choosen.user_place = user_place;
    }

    ////////////////////////////////////////////////////////////////////////////////////
// TODO 方法：活動處理器
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            // turn on bluetooth device
            case Setup.REQ_ENABLE_BT:{
                if (resultCode == RESULT_OK) {/***/}
                break;
            }
        }
    }

    // TODO 方法：處理器(背景執行)
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                // start beacon scanner
                case Setup.REQ_SCAN_BEACON:{
                    synchronized (ListAdapter){
                        // Step.01(啟動裝置背景執行流程) 呼叫方法：裝置掃描(掃描裝置訊號)(掃描裝置電池量)
                        scanner.startScaniBeacon(Setup.TIME_BEACON_START);
                        handler.sendEmptyMessageDelayed(Setup.REQ_SCAN_BEACON, Setup.TIME_BEACON_RESTART);
                        break;
                    }
                }

                // update beacon status
                case Setup.REQ_UPDATE_BEACON:{
                    synchronized (ListAdapter){
                        // Step.01(更新裝置背景執行流程) 呼叫方法：裝置掃描(掃描裝置變動)
                        VerifyBeacons();

                        ListAdapter.notifyDataSetChanged();
                        handler.sendEmptyMessageDelayed(Setup.REQ_UPDATE_BEACON, Setup.TIME_BEACON_UPDATE);
                        break;
                    }
                }
            }
        }
    };

    // TODO 方法：裝置掃描(掃描裝置變動)
    public void VerifyBeacons(){

        for(int i = this.beacons.size() - 1; i >= 0; i--){
            ScannedBeacon beacon = this.beacons.get(i);

            if((System.currentTimeMillis() - beacon.lastUpdate) > Setup.TIME_BEACON_TIMEOUT){
                // Step.02(更新裝置背景執行流程) 移除清單
                //////////////////////////////////////////////////////////////////////////////////////
                //change_mark(correspod_beacon(beacon.major,beacon.minor),0);
                flag_stable_state[correspod_beacon(beacon.major,beacon.minor)] -= 1;
                //choose_one = 0;
                flag_mark_btn[correspod_beacon(beacon.major,beacon.minor)] = false;
                /////////////////////////////////////////////////////////////////////////////////////
                this.beacons.remove(i);

            }
        }

        this.ListAdapter.clearItem();
        // Step.03(更新裝置背景執行流程) 清除項目

        for(ScannedBeacon beacon : this.beacons){
            // Step.03(更新裝置背景執行流程) 重新新增項目
            ListItem item = new ListItem();

            item.setText1(String.valueOf(beacon.beaconUuid));
            item.setText2(String.valueOf(beacon.major));
            item.setText3(String.valueOf(beacon.minor));
            item.setText4(String.valueOf(beacon.rssi));
            item.setText5(String.valueOf(beacon.batteryPower));

            //////////////////////////////////////////////
            beacon_state(beacon.major,beacon.minor,beacon.rssi);
            //////////////////////////////////////////////

            this.ListAdapter.addItem(item);
        }

        // show beacons list for debug
        if(Setup.FLAG_DEBUG){
            Setup._tb.ShowBeaconsList(this.beacons);
        }
    }

    // TODO 方法：裝置掃描(掃描裝置訊號)
    @Override
    public void onScaned(iBeaconData BeaconData){
        ScannedBeacon beacon = null;

        for(ScannedBeacon _beacon : this.beacons){
            if(_beacon.equals(BeaconData, false)) {
                beacon = _beacon;
                break;
            }
        }

        if(beacon == null){
            // Step.02(啟動裝置背景執行流程) 新增清單
            beacon = ScannedBeacon.copyOf(BeaconData);
            this.beacons.add(beacon);
        }else{
            beacon.rssi = BeaconData.rssi;
        }
        check(BeaconData.major, BeaconData.minor, BeaconData.rssi);

        beacon.lastUpdate = System.currentTimeMillis();
    }

    // TODO 方法：裝置掃描(掃描裝置電池量)
    @Override
    public void onBatteryPowerScaned(BatteryPowerData batteryPowerData) {
        for (int i = 0; i < this.beacons.size(); i++) {
            ScannedBeacon beacon = this.beacons.get(i);
            if (beacon.macAddress.equals(batteryPowerData.macAddress)) {
                beacon.batteryPower = batteryPowerData.batteryPower;
                this.beacons.set(i, beacon);
            }
        }
    }

    //TODO 方法：確認編號與距離
    public void check(int major, int minor, int rssi) {
        Map map = new HashMap();

        int ele = major * 10 + minor;
        switch (ele) {
            case 1:
                map.put(1, rssi);
                detector(rssi, 70, 80, "1");
                break;
            case 2:
                map.put(2, rssi);
                detector(rssi, 70, 80, "2");
                break;
            case 3:
                map.put(3, rssi);
                detector(rssi, 70, 80, "3");
                break;
            case 4:
                map.put(4, rssi);
                detector(rssi, 70, 80, "4");
                break;
            default:
                break;
        }
    }

    //TODO 方法：通知震動,推播,回到app
    public void detector(int distance, int upper, int lower, String num){


        NotificationManager NM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        callback();

        notification(num, callback());

        if (flag[Integer.parseInt(num)] == false){
            if (distance > -upper){
                myVibrator();
                flag[Integer.parseInt(num)] = true;
                NM.notify(Integer.parseInt(num), notification(num, callback()));

            }
        }else if(distance <= -lower){
            flag[Integer.parseInt(num)] = false;
            NM.cancel(Integer.parseInt(num));

        }
    }

    //TODO 方法：回app
    public PendingIntent callback() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, 0);
        return pendingIntent;
    }

    //TODO 方法：狀態列
    public Notification notification(String num, PendingIntent callback) {

        Notification notification = new Notification.Builder(this)

                .setSmallIcon(R.drawable.ntust)
                .setContentTitle("MIT LAB beacon " + num)
                .setContentText("MIT LAB beacon " + num)
                .setContentIntent(callback)
                .build();

        return notification;
    }

    //TODO 方法：震動
    public void myVibrator() {
        Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(100);
    }

    static class DrawView extends View {
        public DrawView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // 建立初始畫布
            Paint p = new Paint();                                // 創建畫筆
            p.setAntiAlias(true);                                    // 設置畫筆的鋸齒效果。 true是去除。
            p.setColor(Color.RED);                                // 設置紅色
            p.setTextSize(16);                                        // 設置文字的大小為 16。
            canvas.drawText("圓形：", 10, 20, p);        // 寫一段文字
            canvas.drawCircle(80, 20, 20, p);                // 畫一個小圓

            // 直線繪畫
            p.setColor(Color.GREEN);                            // 設置綠色
            canvas.drawText("直線：", 110, 20, p);            // 寫一段文字
            canvas.drawLine(10, 200, 200, 200, p);        // 畫線 (起點X, 起點Y, 終點X, 終點Y, 線條型態)

            // 斜線繪畫
            p.setStrokeWidth(20);
            p.setColor(Color.parseColor("#DC143C"));                            // 設置crimson
            canvas.drawText("斜線：", 210, 20, p);
            canvas.drawLine(60, 30, 80, 30, p);
        }
    }
}
