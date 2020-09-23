package com.mitlab.zusliu.User.Interface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.Handler;
import android.os.Message;

import com.mitlab.zusliu.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class Floor_choosen extends Activity {
    MainActivity main = new MainActivity();
    ///////////////////////////////////////////////////////////////////////////////////主頁面內容物件
    public FrameLayout frame_1;    //Floor 1 frame
    public FrameLayout frame_2;    //跑馬燈frame
    public ImageButton [] img_btn_mark = new ImageButton[15];   //地標按鍵

    public static TextView mark_state;
    public Button btn_1,btn_2,btn_3;  //商家總覽 地點標記 按鍵
    static String result = "";
    // 儲存地標位置
    /////////////////////////////////0   1   2   3   4   5   6   7   8   9   10
    static int [] mark_position_X = { 10,130,270,410,550,295, 10,130,270,410,550};
    static int [] mark_position_Y = {100,100,100,100,100,360,350,350,350,350,350};

    static String [] web_info     = {"1","2","3","4","5","6","7","8","9","10","11"};
    static int poumadon = 0;



    ///////////////////////////////////////////////////////////////////////////////////旗標
    static boolean btn_1_flag = false;  //"+"按鍵狀態
    static boolean btn_2_flag = false;  //地點標記按鍵狀態
    static boolean btn_3_flag = false;  //網站導覽按鍵狀態

    static boolean [] flag_mark_btn =  {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};  //地標被點選狀態  靠近
    static boolean [] flag_mark_btn2 = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};  //地點標記狀態
    static boolean [] flag_mark_btn3 = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};  //網站導覽狀態
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    static int beacon_amount_f1 = 6;
    public static int user_place = 100;
    static int beacon_num = 100;
    ///////////////////////////////////////////////////////////////////////////////////側滑選單內容物件
    public ToggleButton toggle_btn_1,toggle_btn_2;  //側滑選單 樓層選取
    public LinearLayout side_layout_1;     //sidebar linear_layout

    ///////////////////////////////////////////////////////////定義timer  handler
    private final Timer timer = new Timer();
    private TimerTask task;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 要做的事情
            background();       //需要重複執行的部分
            super.handleMessage(msg);
        }
    };
    ///////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Floor 2");
        setContentView(R.layout.floor_1);   //設定layout檔

        ///////////////////////////////////////////////////////////////////////////////////

        frame_1 = new FrameLayout(this);
        frame_1 = (FrameLayout)findViewById(R.id.floor1_frame);

        frame_2 = new FrameLayout(this);
        frame_2 = (FrameLayout)findViewById(R.id.frame_layout_2);

        mark_state = new TextView(this);        //跑馬燈字樣
        frame_2.addView(mark_state);
        mark_state.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

        btn_1 = (Button)findViewById(R.id.button3);
        btn_2 = (Button)findViewById(R.id.button4);
        btn_3 = (Button)findViewById(R.id.button5);


        Thread thread = new Thread(mutiThread);     //從網路抓資料
        //thread.start();
        mark_state.setText("無活動"); //將網站上的資料顯示
        mark_state.setTextSize(50);


        user_place = MainActivity.user_place;

        Toast.makeText(this,String.valueOf(user_place) , Toast.LENGTH_LONG).show();
        ///////////////////////////////////////////////////////////跑馬燈效果
        mark_state.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mark_state.setSingleLine(true);
        mark_state.setSelected(true);////////////////////////////////////////

        //   mark_state.setText(beacon_num);

        MainActivity.getInstance().display_mark_btn(beacon_amount_f1,mark_position_X,mark_position_Y,img_btn_mark,frame_1,this);   //創建地標圖示


        ///////////////////////////////////////////////////////////設定timer任務
        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 2000, 2000);
        ///////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////

        for(int i = 0;i < beacon_amount_f1;i++){
            //MainActivity.getInstance().img_btn_click(i,img_btn_mark,flag_mark_btn,flag_mark_btn2,flag_mark_btn3,btn_2_flag,btn_3_flag,this);
            img_btn_click(i,img_btn_mark,flag_mark_btn,flag_mark_btn2,flag_mark_btn3,this);
        }




        ///////////////////////////////////////////////////////////////////////////////////側滑選單內容物件
        side_layout_1 = new LinearLayout(this);
        side_layout_1 = (LinearLayout)findViewById(R.id.sidebar);

        toggle_btn_1 = (ToggleButton)findViewById(R.id.sidebar_toggle_1);
        toggle_btn_2 = (ToggleButton)findViewById(R.id.sidebar_toggle_2);

        toggle_btn_1.setOnClickListener(new View.OnClickListener() {    //選擇顯示地圖1

            public void onClick(View v) {       //選擇樓層按鍵事件_1
                // 當按鈕第一次被點擊時候響應的事件
                if (toggle_btn_1.isChecked()) {
                    main.my_floor = 1;
                    toggle_btn_2.setChecked(false);
                    Toast.makeText(Floor_choosen.this, "選擇樓層1", Toast.LENGTH_SHORT).show();
                    ///////////////////////////////////////////////////////////////////////////////////切換頁面
                    Intent intent_1 = new Intent();
                    intent_1.setClass(Floor_choosen.this, MainActivity.class);
                    startActivity(intent_1);
                    ///////////////////////////////////////////////////////////////////////////////////
                }
                // 當按鈕再次被點擊時候響應的事件
                else {
                    Toast.makeText(Floor_choosen.this, "已選擇樓層1", Toast.LENGTH_SHORT).show();
                }
            }
        });

        toggle_btn_2.setOnClickListener(new View.OnClickListener() {    //選擇顯示地圖2
            public void onClick(View v) {       //選擇樓層按鍵事件_2
                // 當按鈕第一次被點擊時候響應的事件
                if (toggle_btn_2.isChecked()) {
                    toggle_btn_1.setChecked(false);
                    Toast.makeText(Floor_choosen.this, "選擇樓層2", Toast.LENGTH_SHORT).show();
                    main.my_floor = 2;
                }
                // 當按鈕再次被點擊時候響應的事件
                else {
                    toggle_btn_2.setChecked(true);
                    Toast.makeText(Floor_choosen.this, "已選擇樓層2", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////

        Log.v("2low", String.valueOf(beacon_num));//跑馬燈更新

    }


    public void background(){
        if(user_place < 17){  //比100小
            if(user_place != MainActivity.user_place){
                if(user_place >= 11) {
                    if(flag_mark_btn2[user_place - 11] == false && flag_mark_btn3[user_place - 11] == false)main.change_mark(user_place-11 ,0,img_btn_mark);
                    flag_mark_btn[user_place-11] = false;
                    Log.v("floor1","1");
                }
                user_place = MainActivity.user_place;
                if(user_place >= 11){
                    if(flag_mark_btn2[user_place - 11] == false && flag_mark_btn3[user_place - 11] == false)main.change_mark(user_place-11 ,1,img_btn_mark);
                    flag_mark_btn[user_place-11] = true;
                    poumadon = 0;   //跑跑馬燈
                    Log.v("floor1","2");
                }
            }
            else{
                if(user_place >= 11){
                    if(flag_mark_btn2[user_place - 11] == false && flag_mark_btn3[user_place - 11] == false)main.change_mark(user_place -11,1,img_btn_mark);
                    poumadon = 1;
                }
            }

        }
        else{  //第一次近來
            user_place = MainActivity.user_place;
            if(user_place >= 11 && user_place<17) {
                Log.v("floor1", String.valueOf(user_place));//跑馬燈更新
                main.change_mark(user_place - 11, 1, img_btn_mark);
                flag_mark_btn[user_place - 11] = true;
                poumadon = 0;   //跑跑馬燈
            }
        }
        //user_place = MainActivity.user_place;
        Log.v("floor14", String.valueOf(poumadon));//跑馬燈更新
        Log.v("TAG___","處理器執行");
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

    //TODO 商家總覽按鍵
    public void buttonOnClick_1(View v) {
        // 寫要做的事...
        if(btn_2_flag == false && btn_3_flag == false) {
            //MainActivity.getInstance().connect_to_web(0);
            //main.connect_to_web(this);
            main.connect_to_web(0,this);
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

            for(int i = 0;i < beacon_amount_f1;i++) flag_mark_btn3[i] = false;
            // btn_3.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            btn_3.getBackground().setColorFilter(0xFFFFAA00, android.graphics.PorterDuff.Mode.MULTIPLY );

        }else{
            // btn_3.setBackgroundColor(getResources().getColor(android.R.color.white));

            btn_3.getBackground().setColorFilter(null);

            for(int i = 11;i < 17;i++) {
                if (flag_mark_btn[i] == true && flag_mark_btn2[i] == true) {
                    main.change_mark(i-11,2,img_btn_mark);
                }
                else if(flag_mark_btn[i] == true && flag_mark_btn2[i] == false){
                    main.change_mark(i-11,1,img_btn_mark);
                }
                else if(flag_mark_btn[i] == false && flag_mark_btn2[i] == true){
                    main.change_mark(i-11,3,img_btn_mark);
                }
                else {
                    main.change_mark(i-11, 0,img_btn_mark);
                }
            }


            for(int j = 0;j < beacon_amount_f1;j++){
                if(flag_mark_btn3[j]==true) main.connect_to_web(j+12,this);

            }
        }
    }

    //TODO 地標按鍵觸發
    public static void img_btn_click(int x, final ImageButton [] mark,final boolean [] flag_1,final boolean [] flag_2,final boolean [] flag_3, final Context context){
        final int a = x;
        mark[a].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_2_flag){
                    //  if(btn_flag_1){
                    flag_2[a] = !flag_2[a];
                    if(flag_2[a] == true){
                        for(int i = 0;i < beacon_amount_f1;i++) {
                            flag_2[i] = false;
                            if(flag_1[i] == true){
                                MainActivity.getInstance().change_mark(i,1,mark);
                                //change_mark(i,1,mark);
                            }
                            else {
                                //change_mark(i, 0,mark);
                                MainActivity.getInstance().change_mark(i, 0,mark);
                            }
                        }
                        flag_2[a] = true;
                        //change_mark(a,3,mark);
                        MainActivity.getInstance().change_mark(a,3,mark);
                        Toast.makeText(context,"number " + a, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        MainActivity.getInstance().change_mark(a+11,0,mark);
                        //change_mark(a,0,mark);
                        for(int i = 0;i < beacon_amount_f1;i++) {
                            flag_2[i] = false;
                            if (flag_1[i] == true) {
                                MainActivity.getInstance().change_mark(i,1,mark);
                                //change_mark(i,1,mark);
                            }
                            else {
                                MainActivity.getInstance().change_mark(i, 0,mark);
                                //change_mark(i, 0,mark);
                            }
                        }
                    }
                }else if(btn_3_flag){
                    //else if(btn_flag_2){
                    flag_3[a] = !flag_3[a];
                    if(flag_3[a] == true){
                        for(int i = 0;i < beacon_amount_f1;i++) {
                            flag_3[i] = false;
                            if (flag_1[i] == true && flag_2[i] == true) {
                                MainActivity.getInstance().change_mark(i,2,mark);
                                //change_mark(i,2,mark);
                            }
                            else if(flag_1[i] == true && flag_2[i] == false){
                                MainActivity.getInstance().change_mark(i,1,mark);
                                //change_mark(i,1,mark);
                            }
                            else if(flag_1[i] == false && flag_2[i] == true){
                                MainActivity.getInstance().change_mark(i,3,mark);
                                //change_mark(i,3,mark);
                            }
                            else {
                                MainActivity.getInstance().change_mark(i, 0,mark);
                                //change_mark(i, 0,mark);
                            }
                        }
                        flag_3[a] = true;
                        MainActivity.getInstance().change_mark(a,4,mark);
                        //change_mark(a,4,mark);
                        Toast.makeText(context,"number " + a, Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i = 0;i < beacon_amount_f1;i++) {
                            flag_3[i]=false;
                            if (flag_1[i] == true && flag_2[i] == true) {
                                MainActivity.getInstance().change_mark(i,2,mark);
                                //change_mark(i,2,mark);
                            }else if(flag_1[i] == true && flag_2[i] == false){
                                MainActivity.getInstance().change_mark(i,1,mark);
                                //change_mark(i,1,mark);
                            }else if(flag_1[i] == false && flag_2[i] == true){
                                MainActivity.getInstance().change_mark(i,3,mark);
                                //change_mark(i,3,mark);
                            }else {
                                MainActivity.getInstance().change_mark(i, 0,mark);
                                //change_mark(i, 0,mark);
                            }
                        }
                    }
                }else if(!btn_2_flag && !btn_3_flag){
                    Toast.makeText(context,"number ==== " + a, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}
