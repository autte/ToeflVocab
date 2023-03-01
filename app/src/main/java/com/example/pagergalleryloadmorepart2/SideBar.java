package com.example.pagergalleryloadmorepart2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class SideBar extends View {
    public static String[] characters = new String[]{"❤", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    private int position = -1;		//当前选中的位置
    private int defaultTextColor = Color.parseColor("#D2D2D2");   //默认拼音文字的颜色
    private int selectedTextColor = Color.parseColor("#2DB7E1");  //选中后的拼音文字的颜色

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();						//当前控件高度
        int width = getWidth();						 	//当前控件宽度
        int singleHeight = height / characters.length;    //每个字母占的长度

        for (int i = 0; i < characters.length; i++) {
            if (i == position) {                    //当前选中
                paint.setColor(selectedTextColor); 	//设置选中时的画笔颜色
            } else {                                //未选中
                paint.setColor(defaultTextColor);	//设置未选中时的画笔颜色
            }
            float textSize;
            textSize=15;
            paint.setTextSize(textSize);			//设置字体大小

            //设置绘制的位置
            float xPos = width / 2 - paint.measureText(characters[i]) / 2;
            float yPos = singleHeight * i + singleHeight;

            canvas.drawText(characters[i], xPos, yPos, paint);      //绘制文本
        }
    }

     //画笔
    private Paint paint;

    public SideBar(Context context) {
        super(context);
        init();
    }
    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //初始化画笔工具
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
    }

}
