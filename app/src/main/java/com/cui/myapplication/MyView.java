package com.cui.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Cui on 2017/3/8.
 *
 * @Description 自定义View
 */


public class MyView extends View {
    private Context mContext;
    private Surface mSurface;
    private Calendar mCalendar;
    private List<TimeBean> mTimeBeanList;
    private float mRecordTop;

    public MyView(Context context) {
        super(context);
        init(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mSurface = new Surface();
        mSurface.init();
        mCalendar = Calendar.getInstance();
        mTimeBeanList = new ArrayList<>();
        getClassInfo(getFromAssets("json"));
    }

    /**
     * 重写onMeasure设置宽度始终为屏幕宽度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mSurface.mWidth, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRecordTop = 0;
        drawTitle(canvas);
        drawWeekText(canvas);
        drawContent(canvas);
//        for (int i = 0; i < mSurface.mTexts.length; i++) {
//            String text = mSurface.mTexts[i];
//            float y = mSurface.mPaint.measureText("黑");
//            float width = mSurface.mPaint.measureText(text);
//            //超出宽度，需要换行
//            if (width > mSurface.mCellWidth) {
//                drawTextOverCellWidth(text, canvas, y, i);
//            } else {//没有超出长度直接绘制
//                canvas.drawText(text, mSurface.mCellWidth * i + (mSurface.mCellWidth - width) / 2, y, mSurface.mPaint);
//            }
//        }
    }

    /**
     * 绘制标题
     *
     * @param canvas
     */
    private void drawTitle(Canvas canvas) {
        float paddingTop = getResources().getDimension(R.dimen.dp_6);
        float paddingLeft = getResources().getDimension(R.dimen.dp_8);
        //绘制背景
        mSurface.mBluePaint.setTextSize(getResources().getDimension(R.dimen.text_size_10));
        mRecordTop = mSurface.mBluePaint.measureText("年") + paddingTop * 2;
        canvas.drawRect(0, 0, mSurface.mWidth, mRecordTop, mSurface.mBacPaint);
        String date = getDateText(new Date());
        canvas.drawText(date, paddingLeft, mRecordTop - paddingTop, mSurface.mBluePaint);

        mSurface.mGreyPaint.setTextSize(getResources().getDimension(R.dimen.text_size_8));
        float textHeight = mSurface.mGreyPaint.measureText("年");
        float margin = getResources().getDimension(R.dimen.dp_16);
        float viewMargin = getResources().getDimension(R.dimen.dp_4);
        float viewWidth = getResources().getDimension(R.dimen.dp_8);
        float recordLeft = mSurface.mWidth;
        for (int i = 0; i < mSurface.mTitles.length; i++) {
            String text = mSurface.mTitles[i];
            recordLeft = recordLeft - margin - mSurface.mGreyPaint.measureText(text);
            canvas.drawText(text, recordLeft,
                    mRecordTop / 2 + textHeight / 2, mSurface.mGreyPaint);
            recordLeft = recordLeft - viewMargin - viewWidth;
            canvas.drawRect(recordLeft, mRecordTop / 2 - viewWidth / 2, recordLeft + viewWidth, mRecordTop / 2 + viewWidth / 2, mSurface.mPaints[i]);
        }
        drawRowLine(canvas, 0, mRecordTop);
        mRecordTop = mRecordTop + mSurface.mLinePaint.getStrokeWidth();
    }

    /**
     * 绘制周次信息
     * @param canvas 画布
     */
    private float drawWeekText(Canvas canvas){
        float paddingTop = getResources().getDimension(R.dimen.dp_4);
        mSurface.mBlackPaint.setTextSize(getResources().getDimension(R.dimen.text_size_12));
        float startX = mSurface.mCellWidth * 2;
        float textHeight = mSurface.mBlackPaint.measureText("年");
        for (int i = 0;i < 6;i ++) {
            float x = startX + i * mSurface.mCellWidth * 3;
            canvas.drawLine(x, mRecordTop, x, mRecordTop + textHeight * 2 + paddingTop * 2, mSurface.mLinePaint);
            String week = mSurface.mWeeks[i];
            if (i < 5) {
                drawTextCenter(canvas, mSurface.mBlackPaint, x, x + mSurface.mCellWidth * 3, mRecordTop + textHeight + paddingTop, week);
                mCalendar.set(Calendar.DAY_OF_WEEK, i + 2);
                int month = mCalendar.get(Calendar.MONTH) + 1;
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                String date = month + "." + day;
                drawTextCenter(canvas, mSurface.mBlackPaint, x, x + mSurface.mCellWidth * 3, mRecordTop + textHeight * 2 + paddingTop, date);
            } else {
                drawTextCenter(canvas, mSurface.mBlackPaint, x, mSurface.mWidth, mRecordTop + textHeight * 3 / 2 + paddingTop, week);
            }
        }
        mRecordTop = mRecordTop + textHeight * 2 + paddingTop * 2;
        drawRowLine(canvas, 0, mRecordTop);
        mRecordTop = mRecordTop + mSurface.mLinePaint.getStrokeWidth();
        return mRecordTop;
    }

    /**
     * 绘制内容部分
     * @param canvas
     */
    private void drawContent(Canvas canvas){
        for (int i = 0;i < mTimeBeanList.size();i ++) {
            TimeBean timeBean = mTimeBeanList.get(i);
            if (timeBean.getList().size() > 1) {
                List<SectionBean> sectionBeanList = timeBean.getList();
                int size = sectionBeanList.size();
                for (int j = 0;j < size;j ++) {
                    drawSectionsContent(canvas, timeBean, j);
                }
            } else {
                drawOneSectionContent(canvas, timeBean);
            }
        }
    }

    /**
     * 绘制一节课的时间段
     * @param canvas
     * @param timeBean
     */
    private void drawOneSectionContent(Canvas canvas, TimeBean timeBean){
        String text = timeBean.getName();
        if (timeBean.getList().size() > 0) {
            SectionBean sectionBean = timeBean.getList().get(0);
            canvas.drawRect(0, mRecordTop, mSurface.mWidth, mRecordTop + mSurface.mContentMinHeight, mSurface.mBacPaint);
            drawColumnLine(canvas);
            mRecordTop = mRecordTop + mSurface.mContentMinHeight;
            canvas.drawRect(0, mRecordTop, mSurface.mWidth, mRecordTop, mSurface.mLinePaint);
            mRecordTop = mRecordTop + mSurface.mLinePaint.getStrokeWidth();
        }
    }

    /**
     * 绘制多节课的时间段
     * @param canvas
     * @param timeBean
     * @param index
     */
    private void drawSectionsContent(Canvas canvas, TimeBean timeBean, int index){
        SectionBean sectionBean = timeBean.getList().get(index);
        canvas.drawRect(0, mRecordTop, mSurface.mWidth, mRecordTop + mSurface.mContentMinHeight, mSurface.mWhitePaint);
        drawColumnLine(canvas);
        mRecordTop = mRecordTop + mSurface.mContentMinHeight;
        canvas.drawRect(0, mRecordTop, mSurface.mWidth, mRecordTop, mSurface.mLinePaint);
        mRecordTop = mRecordTop + mSurface.mLinePaint.getStrokeWidth();
    }

    /**
     * 根据记录的顶部位置绘制竖直分隔线
     * @param canvas
     */
    private void drawColumnLine(Canvas canvas){
        float startX = mSurface.mCellWidth * 2;
        for (int i = 0;i < 6;i ++) {
            canvas.drawLine(startX + i * 3 * mSurface.mCellWidth, mRecordTop, startX + i * 3 * mSurface.mCellWidth,
                    mRecordTop + mSurface.mContentMinHeight, mSurface.mLinePaint);
        }
    }

    /**
     * 给出指定文字和左右边缘值以及底部值，将文字画在指定位置中间
     * @param canvas 画布
     * @param paint 画笔
     * @param left 左边缘
     * @param right 右边缘
     * @param bottom 底部
     * @param text 文字
     */
    private void drawTextCenter(Canvas canvas, Paint paint, float left, float right, float bottom, String text){
        float textWidth = paint.measureText(text);
        canvas.drawText(text, left + (right - left - textWidth) / 2, bottom, paint);
    }

    /**
     * 绘制灰色横线
     *
     * @param canvas 画布
     * @param left   横线X坐标
     * @param top    横线Y坐标
     */
    private void drawRowLine(Canvas canvas, float left, float top) {
        canvas.drawLine(left, top, mSurface.mWidth, top, mSurface.mLinePaint);
    }

    /**
     * 超出视图长度之后的绘制方法，换行绘制
     *
     * @param text
     * @param canvas 画布
     * @param y      每一行的高度
     * @param index  文字在数组中的位置
     */
    private void drawTextOverCellWidth(String text, Canvas canvas, float y, int index) {
        char[] chars = text.toCharArray();
        float width = 0;
        String newText = "";
        int lines = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            width = width + mSurface.mBlackPaint.measureText(String.valueOf(c));
            if (width > mSurface.mCellWidth) {
                canvas.drawText(newText, index * mSurface.mCellWidth, y * (lines + 1), mSurface.mBlackPaint);
                width = 0;
                newText = "";
                lines++;
                i--;
            } else {
                newText = newText + c;
            }
        }
        float newTextWidth = mSurface.mBlackPaint.measureText(newText);
        canvas.drawText(newText, index * mSurface.mCellWidth + (mSurface.mCellWidth - newTextWidth) / 2, y * (lines + 1), mSurface.mBlackPaint);
    }

    /**
     * 从assets 文件夹中获取文件并读取数据
     * @param fileName
     * @return
     */
    public String getFromAssets(String fileName){
        String result = "";
        try {
            InputStream in = getResources().getAssets().open(fileName);
            //获取文件的字节数
            int length = in.available();
            //创建byte数组
            byte[]  buffer = new byte[length];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            result = new String(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void getClassInfo(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject Goodo = jsonObject.getJSONObject("Goodo");
            JudgeIsJsonArray.judge(Goodo, "Time", new JudgeIsJsonArray.OnJudged() {
                @Override
                public void judged(JSONObject jsonObject) throws JSONException {
                    final TimeBean timeBean = new TimeBean();
                    timeBean.setName(jsonObject.getString("Name"));
                    timeBean.setList(new ArrayList<SectionBean>());
                    mTimeBeanList.add(timeBean);
                    JudgeIsJsonArray.judge(jsonObject, "Section", new JudgeIsJsonArray.OnJudged() {
                        @Override
                        public void judged(JSONObject jsonObject) throws JSONException {
                            SectionBean sectionBean = new SectionBean();
                            sectionBean.setName(jsonObject.getString("Name"));
                            sectionBean.setBegin(jsonObject.getString("Begin"));
                            sectionBean.setEnd(jsonObject.getString("End"));
                            sectionBean.setTime(mTimeBeanList.size() - 1);
                            sectionBean.setTheClass(timeBean.getList().size());//设置属于哪个时间段的第几节课
                            timeBean.getList().add(sectionBean);
                        }
                    });
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 用于存储相关变量，避免在onDraw实例化对象
     */
    public class Surface {
        int mWidth;
        float mPadding;
        float mLength;
        float mCellWidth;
        float mContentMinHeight;

        int[] mTitleColors;
        String[] mTitles;
        String[] mTexts;
        public String[] mWeeks;
        String mDate;
        SimpleDateFormat mFormat;

        Paint mBlackPaint;
        Paint mBluePaint;
        Paint mBacPaint;
        Paint mGreyPaint;
        Paint mRedPaint;
        Paint mYellowPaint;
        Paint mWhitePaint;
        Paint mLinePaint;
        Paint[] mPaints;

        public void init() {
            mWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            mTitles = new String[]{"课程表", "个人安排", "部门安排", "学校安排"};
            mTitleColors = new int[]{R.color.black, R.color.yellow_person, R.color.blue_department, R.color.red_school};
            mTexts = new String[]{"黑", "黑白", "黑白黑", "黑白黑白", "黑白黑白黑白",
                    "黑白黑白黑吧黑白黑白", "黑白黑白黑吧黑白黑白黑白黑白黑吧黑白黑白"};
            mWeeks = new String[]{"一","二","三","四","五","六&日"};
            mLength = 20;
            mCellWidth = mWidth / mLength;
            mFormat = new SimpleDateFormat("yyyy年mm月dd日");
            mDate = getDateText(new Date());
            mPadding = getResources().getDimension(R.dimen.dp_2);
            mContentMinHeight = getResources().getDimension(R.dimen.dp_48);

            mBlackPaint = new Paint();
            mBlackPaint.setColor(getResources().getColor(R.color.black));
            mBlackPaint.setAntiAlias(true);

            mBluePaint = new Paint();
            mBluePaint.setColor(getResources().getColor(R.color.blue_white));
            mBluePaint.setAntiAlias(true);

            mBacPaint = new Paint();
            mBacPaint.setColor(getResources().getColor(R.color.grey_by_week_top));

            mGreyPaint = new Paint();
            mGreyPaint.setColor(getResources().getColor(R.color.gray));
            mGreyPaint.setAntiAlias(true);

            mRedPaint = new Paint();
            mRedPaint.setColor(getResources().getColor(R.color.red));
            mRedPaint.setAntiAlias(true);

            mYellowPaint = new Paint();
            mYellowPaint.setColor(getResources().getColor(R.color.yellow_person));
            mYellowPaint.setAntiAlias(true);

            mWhitePaint = new Paint();
            mWhitePaint.setColor(getResources().getColor(R.color.white));
            mWhitePaint.setAntiAlias(true);

            mPaints = new Paint[]{mBlackPaint, mYellowPaint, mBluePaint, mRedPaint};

            mLinePaint = new Paint();
            mLinePaint.setColor(getResources().getColor(R.color.grey));
            mLinePaint.setStrokeWidth(getResources().getDimension(R.dimen.dp_0_5));
            mLinePaint.setAntiAlias(true);
        }
    }

    /**
     * 根据日期返回yyyy年mm月dd日格式
     *
     * @param date
     * @return 返回日期字符串
     */
    public String getDateText(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "年" + month + "月" + day + "日";
    }


}

