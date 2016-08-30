package com.xp.xppiechart.view;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.xp.xppiechart.R;

import java.util.List;

public class CustomPieChartView extends View{
    private String tag = this.getClass().getSimpleName();
    private ValueAnimator valueAnimator;
    private static int DURATION = 1000;
    private Paint mPaint,piePaintIn;
    private RectF pieOval;
    private RectF pieOvalIn;
    private int pieWith = -1 ; //圆弧的宽度
    private float startAngle = -180; //-360~360
    private List<CakeValue> cakeValues;
    private float curSingle = 0;
    private int viewWidth;
    private int backgroundColor;
    private float pieinterval =0;

    public CustomPieChartView(Context context) {
        this(context,null);
    }

    public CustomPieChartView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomPieChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomAttribute(context,attrs);
        init();
    }

    private void initCustomAttribute(Context context,AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomPieChartView);
        pieWith = (int)a.getDimension(R.styleable.CustomPieChartView_innerToOuterWidth,-1);
        startAngle = a.getFloat(R.styleable.CustomPieChartView_startAngle,0);
        try {
            backgroundColor = Color.parseColor(a.getString(R.styleable.CustomPieChartView_piebackground));
        }catch (Exception e){
            backgroundColor = Color.parseColor("#ffffff");
        }
        pieinterval = a.getFloat(R.styleable.CustomPieChartView_pieinterval,1);
        if(pieinterval<=0){
            pieinterval =1;
        }
        a.recycle();
    }

    private void init(){
        initAnimation();
    }

    /**
     * 初始化动画
     */
    private void initAnimation(){
        PropertyValuesHolder angleValues = PropertyValuesHolder.ofFloat("angle", startAngle, startAngle+360);
        valueAnimator = ValueAnimator.ofPropertyValuesHolder(angleValues);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                curSingle = ((Number) valueAnimator.getAnimatedValue()).floatValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(DURATION);
        valueAnimator.setRepeatCount(0);
        valueAnimator.setInterpolator(new LinearInterpolator()); //速率为匀速
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureWidth(widthMeasureSpec);
    }

    private void measureWidth(int widthMeasureSpec){
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if(specMode == MeasureSpec.EXACTLY){
            initDraw(specSize);
        }else{
            initDraw(0);
        }
    }

    private void initDraw(int viewWidth){
        this.viewWidth = viewWidth;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        if(pieWith>viewWidth/2||pieWith<0){
            Log.e(tag,"innerToOuterWidth is wrong");
            pieWith  = viewWidth/4;
        }

        //外圆
        pieOval = new RectF();
        pieOval.left = 0;
        pieOval.right = viewWidth;
        pieOval.top = 0;
        pieOval.bottom = viewWidth;
        //内圆
        pieOvalIn = new RectF();
        pieOvalIn.left = pieOval.left + pieWith;
        pieOvalIn.top = pieOval.top + pieWith;
        pieOvalIn.right = pieOval.right - pieWith;
        pieOvalIn.bottom = pieOval.bottom - pieWith;
        //设置画笔
        piePaintIn = new Paint();
        piePaintIn.setAntiAlias(true);
        piePaintIn.setStyle(Paint.Style.FILL);
        piePaintIn.setColor(backgroundColor);
    }

    public void setData(List<CakeValue> cakeValues){
        if(cakeValues == null) return;
        float sum =0;
        for(int i=0;i<cakeValues.size();i++){
            sum += cakeValues.get(i).cakePie;
        }
        float sumSingle = 0;
        for(int i =0;i<cakeValues.size();i++){
            float pie = cakeValues.get(i).cakePie;
            CakeValue cakeValue = cakeValues.get(i);
            cakeValue.setProportion(pie/sum);
            if(i ==  0){
                sumSingle = startAngle;
            }else{
                sumSingle = cakeValues.get(i-1).end;
            }
            if(cakeValues.size()==1){
                cakeValue.setStart(sumSingle);
            }else{
                cakeValue.setStart(sumSingle+pieinterval);
            }
            if(i == cakeValues.size() -1){
                cakeValue.setEnd(startAngle+360);
            }else{
                cakeValue.setEnd(cakeValue.start + pie* (360- pieinterval*cakeValues.size())/sum);
            }
        }
        this.cakeValues = cakeValues;
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(new Rect(0, 0, viewWidth, viewWidth), piePaintIn);
        if(cakeValues == null) return;
        for(CakeValue cakeValue:cakeValues){
            mPaint.setColor(Color.parseColor(cakeValue.getColor()));
            if(curSingle>=cakeValue.start-pieinterval&&curSingle<=cakeValue.end){
                canvas.drawArc(pieOval, cakeValue.start,curSingle-cakeValue.start, true,mPaint);
                break;
            }
            canvas.drawArc(pieOval, cakeValue.start,cakeValue.end-cakeValue.start, true,mPaint);
        }
        canvas.drawArc(pieOvalIn, 0, 360, true, piePaintIn);
    }

    public static class CakeValue{
        public CakeValue(String color,float cakePie){
            this.color = color;
            this.cakePie = cakePie;
        }

        private String color;
        private float start,end,proportion,cakePie;
        public float getCakePie(){
            return cakePie;
        }

        public void setCakePie(float cakePie){
            this.cakePie = cakePie;
        }
        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public float getStart() {
            return start;
        }

        public void setStart(float start) {
            this.start = start;
        }

        public float getEnd() {
            return end;
        }

        public void setEnd(float end) {
            this.end = end;
        }

        public float getProportion() {
            return proportion;
        }

        public void setProportion(float proportion) {
            this.proportion = proportion;
        }
    }
}
