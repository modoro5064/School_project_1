package com.mitlab.zusliu.User.Interface;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.widget.LinearLayout;

public class DrawLine extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        //LinearLayout layout=(LinearLayout) findViewById(R.id.root);
        final DrawView view=new DrawView(this);
        //view.setMinimumHeight(500);
        //view.setMinimumWidth(300);
        //通知view組件重繪
        //view.invalidate();
        //layout.addView(view);
    }

    public class DrawView extends View{

        public DrawView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // 建立初始畫布
            Paint p = new Paint();								// 創建畫筆
            p.setAntiAlias(true);									// 設置畫筆的鋸齒效果。 true是去除。
            p.setColor(Color.RED);								// 設置紅色
            p.setTextSize(16);										// 設置文字的大小為 16。
            canvas.drawText("圓形：",10,20,p);		// 寫一段文字
            canvas.drawCircle(80,20,20,p);				// 畫一個小圓

            // 直線繪畫
            p.setColor(Color.GREEN);							// 設置綠色
            canvas.drawText("直線：",110,20,p);			// 寫一段文字
            canvas.drawLine(160, 20, 200, 20, p);		// 畫線 (起點X, 起點Y, 終點X, 終點Y, 線條型態)

            // 斜線繪畫
            p.setColor(Color.parseColor("#DC143C"));							// 設置crimson
            canvas.drawText("斜線：",210,20,p);
            canvas.drawLine(260, 10, 350, 20, p);
        }
    }
}