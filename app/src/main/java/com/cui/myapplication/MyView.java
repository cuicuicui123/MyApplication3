package com.cui.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.solidfire.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.ParseException;
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
    private List<ScheduleBean> mScheduleBeanList;
    private List<ScheduleBean>[][] mScheduleBeanLists;

    private float mRecordTop;
    private float mOldRecordTop;

    private Date mCurDate;

    private int mRowNum = 6;

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
        mCurDate = mCalendar.getTime();
        mTimeBeanList = new ArrayList<>();
        getClassInfo(getFromAssets("json"));
        getScheduleBeanLists();
        mScheduleBeanList = new ArrayList<>();
        getScheduleBeanInfo(getFromAssets("json2"));
    }

    /**
     * 根据timeBeanList获取scheduleBeanLists二维数组横向和纵向容量
     */
    private void getScheduleBeanLists() {
        int time = 0;
        for (TimeBean bean : mTimeBeanList) {
            List<SectionBean> list = bean.getList();
            if (list != null) {
                time += list.size();
            }

        }
        mScheduleBeanLists = new List[mRowNum][time];
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
        mCalendar.setTime(mCurDate);
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
        drawRowLine(mSurface.mGradientDrawable, canvas, 0, mRecordTop);
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
        canvas.drawRect(0, mRecordTop, mSurface.mWidth, mRecordTop + mRecordTop + textHeight * 2 + paddingTop * 2, mSurface.mWhitePaint);
        drawColumnLine(canvas);
        for (int i = 0;i < 6;i ++) {
            float x = startX + i * mSurface.mCellWidth * 3;
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
        drawRowLine(mSurface.mGradientDrawable, canvas, 0, mRecordTop);
        mRecordTop = mRecordTop + mSurface.mLinePaint.getStrokeWidth();
        return mRecordTop;
    }

    /**
     * 绘制内容部分
     * @param canvas
     */
    private void drawContent(Canvas canvas){
        int theClass = 0;
        for (int i = 0;i < mTimeBeanList.size();i ++) {
            TimeBean timeBean = mTimeBeanList.get(i);
            if (timeBean.getList().size() > 1) {
                mOldRecordTop = mRecordTop;
                List<SectionBean> sectionBeanList = timeBean.getList();
                int size = sectionBeanList.size();
                for (int j = 0;j < size;j ++) {
                    drawSectionsContent(canvas, timeBean, j, theClass);
                    theClass ++;
                }
            } else {
                drawOneSectionContent(canvas, timeBean, theClass);
                theClass ++;
            }
        }
    }

    /**
     * 绘制一节课的时间段
     * @param canvas
     * @param timeBean
     */
    private void drawOneSectionContent(Canvas canvas, TimeBean timeBean, int theClass){
        String text = timeBean.getName();
        if (timeBean.getList().size() > 0) {
            SectionBean sectionBean = timeBean.getList().get(0);
            canvas.drawRect(0, mRecordTop, mSurface.mWidth, mRecordTop + mSurface.mContentMinHeight, mSurface.mBacPaint);
            drawColumnLine(canvas);
            mSurface.mBlackPaint.setTextSize(getResources().getDimension(R.dimen.text_size_12));
            drawTextColumn(canvas, mSurface.mBlackPaint, text, 0, mSurface.mCellWidth * 2, mRecordTop, mRecordTop + mSurface.mContentMinHeight);
            for(int i = 0;i < mRowNum;i ++){
                List<ScheduleBean> list = mScheduleBeanLists[i][theClass];
                if (list != null) {
                    int size = list.size();
                    for (int j = 0;j < size;j ++) {
                        String work = list.get(j).getWork();
                        drawTextCenter(canvas, mSurface.mBlackPaint, mSurface.mCellWidth * (2 + i * 3), mSurface.mCellWidth * (2 + (i + 1) * 3), mRecordTop, work);
                    }
                }

            }
            mRecordTop = mRecordTop + mSurface.mContentMinHeight;
            drawRowLine(mSurface.mRedGradientDrawable, canvas, 0, mRecordTop);
            mRecordTop = mRecordTop + mSurface.mLinePaint.getStrokeWidth();
        }
    }

    /**
     * 绘制多节课的时间段
     * @param canvas
     * @param timeBean
     * @param index
     * @param theClass 第几节课
     */
    private void drawSectionsContent(Canvas canvas, TimeBean timeBean, int index, int theClass){
        SectionBean sectionBean = timeBean.getList().get(index);
        canvas.drawRect(0, mRecordTop, mSurface.mWidth, mRecordTop + mSurface.mContentMinHeight, mSurface.mWhitePaint);
        drawColumnLine(canvas);
        mSurface.mBlackPaint.setTextSize(getResources().getDimension(R.dimen.text_size_10));
        drawTextColumn(canvas, mSurface.mBlackPaint, sectionBean.getName(), mSurface.mCellWidth, 2 * mSurface.mCellWidth, mRecordTop, mRecordTop + mSurface.mContentMinHeight);

        for(int i = 0;i < mRowNum;i ++){
            List<ScheduleBean> list = mScheduleBeanLists[i][theClass];
            if (list != null) {
                int size = list.size();
                for (int j = 0;j < size;j ++) {
                    String work = list.get(j).getWork();
                    drawTextOverCellWidth(work, canvas, mRecordTop, i);
                }
            }
        }
        mRecordTop = mRecordTop + mSurface.mContentMinHeight;
        if (index == timeBean.getList().size() - 1) {
            drawRowLine(mSurface.mRedGradientDrawable, canvas, 0, mRecordTop);
            canvas.drawRect(0, mOldRecordTop, mSurface.mCellWidth, mRecordTop, mSurface.mBacPaint);
            mSurface.mBlackPaint.setTextSize(getResources().getDimension(R.dimen.text_size_12));
            drawTextColumn(canvas, mSurface.mBlackPaint, timeBean.getName(), 0, mSurface.mCellWidth, mOldRecordTop, mRecordTop);
        } else {
            drawRowLine(mSurface.mGradientDrawable, canvas, mSurface.mCellWidth, mRecordTop);
        }
        mRecordTop = mRecordTop + mSurface.mLinePaint.getStrokeWidth();
    }

    /**
     * 根据记录的顶部位置绘制竖直分隔线
     * @param canvas
     */
    private void drawColumnLine(Canvas canvas){
        float startX = mSurface.mCellWidth * 2;
        for (int i = 0;i < 6;i ++) {
            mSurface.mColumnGradientDrawable.setBounds((int) (startX + i * 3 * mSurface.mCellWidth), (int) mRecordTop,
                    (int)(startX + i * 3 * mSurface.mCellWidth + mSurface.mLineHeight), (int)(mRecordTop + mSurface.mContentMinHeight + mSurface.mLineHeight));
            mSurface.mColumnGradientDrawable.draw(canvas);
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

    private void drawTextColumn(Canvas canvas, Paint paint, String text, float left, float right, float top, float bottom){
        char[] chars = text.toCharArray();
        float textSize = paint.measureText("上");
        float x = left + (right - left - textSize) / 2;
        float y = (bottom - top - paint.measureText(text)) / 2;
        for(int i = 0;i < chars.length;i ++){
            String c = String.valueOf(chars[i]);
            canvas.drawText(c, x, top + y + (i + 1) * textSize, paint);
        }
    }

    /**
     * 绘制灰色横线
     *
     * @param canvas 画布
     * @param left   横线X坐标
     * @param top    横线Y坐标
     */
    private void drawRowLine(Drawable drawable, Canvas canvas, float left, float top) {
        drawable.setBounds((int) left, (int) top, mSurface.mWidth, (int) (top + mSurface.mLineHeight));
        drawable.draw(canvas);
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
        float startX = 2 * mSurface.mCellWidth;
        char[] chars = text.toCharArray();
        float width = 0;
        float height = mSurface.mBlackPaint.measureText("年");
        y = y + height;
        String newText = "";
        int lines = 0;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            width = width + mSurface.mBlackPaint.measureText(String.valueOf(c));
            if (width > mSurface.mCellWidth * 3) {
                canvas.drawText(newText, startX + index * 3 * mSurface.mCellWidth, y + height * (lines + 1), mSurface.mBlackPaint);
                width = 0;
                newText = "";
                lines++;
                i--;
            } else {
                newText = newText + c;
            }
        }
        float newTextWidth = mSurface.mBlackPaint.measureText(newText);
        canvas.drawText(newText, startX + index * 3 * mSurface.mCellWidth + (3 * mSurface.mCellWidth - newTextWidth) / 2, y * (lines + 1), mSurface.mBlackPaint);
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

    /**
     * 根据json获取timeBean信息
     * @param response
     */
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
     * 处理json获取scheduleBean，并根据timeBean时间获取对应的在数组中的位置
     * @param response
     */
    private void getScheduleBeanInfo(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject Goodo = jsonObject.getJSONObject("Goodo");
            final Gson gson = new Gson();
            JudgeIsJsonArray.judge(Goodo, "R", new JudgeIsJsonArray.OnJudged() {
                @Override
                public void judged(JSONObject jsonObject) throws JSONException {
                    ScheduleBean bean = gson.fromJson(jsonObject.toString(), ScheduleBean.class);
                    getScheduleBeanWeek(bean);
                    getScheduleBeanClass(bean);
                    List<ScheduleBean> list = mScheduleBeanLists[bean.getWeek()][bean.getTheClass()];
                    if (list == null) {
                        list = new ArrayList<>();
                        mScheduleBeanLists[bean.getWeek()][bean.getTheClass()] = list;
                    }
                    list.add(bean);
                    mScheduleBeanList.add(bean);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getScheduleBeanClass(ScheduleBean bean) {
        String beginTime = DataTransform.transformTime(bean.getBeginTime());
        if (bean.getIsAllDay() == 1) {
            bean.setTheClass(0);
        } else {
            int theClass = 0;
            int timeBeanSize = mTimeBeanList.size();
            for (int i = 0;i < timeBeanSize;i ++) {
                List<SectionBean> list = mTimeBeanList.get(i).getList();
                if (list != null) {
                    int sectionBeanSize = list.size();
                    for (int j = 0;j < sectionBeanSize;j ++) {
                        SectionBean sectionBean = list.get(j);
                        if (beginTime.compareTo(sectionBean.getBegin()) <= 0) {
                            bean.setTheClass(theClass);
                            return;
                        }
                        theClass ++;
                    }
                }
            }
            bean.setTheClass(theClass);
        }
    }

    private void getScheduleBeanWeek(ScheduleBean bean) {
        String dateStr = DataTransform.transform(bean.getDate());
        try {
            Date date = MyDateFormat.getDateFormat().parse(dateStr);
            mCalendar.setTime(date);
            int day = mCalendar.get(Calendar.DAY_OF_WEEK);
            //如果是周日就跟周六放在一起，其余的从周一开始位置是0，依次往后排
            bean.setWeek(day == 1 ? 5 : day - 2);
        } catch (ParseException e) {
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
        float mLineHeight;

        int[] mTitleColors;
        String[] mTitles;
        String[] mTexts;
        public String[] mWeeks;
        String mDate;
        SimpleDateFormat mFormat;

        GradientDrawable mGradientDrawable;
        GradientDrawable mColumnGradientDrawable;
        GradientDrawable mRedGradientDrawable;

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
            mBacPaint.setColor(getResources().getColor(R.color.light_grey));

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
            mLineHeight = getResources().getDimension(R.dimen.dp_0_5);
            mLinePaint.setStrokeWidth(mLineHeight);
            //新建一个线性渐变，前两个参数是渐变开始的点坐标，第三四个参数是渐变结束的点的坐标。连接这2个点就拉出一条渐变线了，玩过PS的都懂。
            // 然后那个数组是渐变的颜色。下一个参数是渐变颜色的分布，如果为空，每个颜色就是均匀分布的。最后是模式，这里设置的是循环渐变
            Shader mShader = new LinearGradient(0, 0, 0, 0,new int[] {0xffffffff, 0xff888888},null,Shader.TileMode.REPEAT);
            mLinePaint.setAntiAlias(true);
//            mLinePaint.setColor(getResources().getColor(R.color.grey));
            mLinePaint.setShader(mShader);

            mGradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.gradient_grey);
            mColumnGradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.gradient_grey_column);
            mRedGradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.gradient_red);
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

