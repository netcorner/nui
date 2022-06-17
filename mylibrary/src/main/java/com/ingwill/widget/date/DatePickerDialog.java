package com.ingwill.widget.date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.ingwill.mylibrary.R;
import com.ingwill.utils.AndroidUtils;
import com.ingwill.utils.DateUtils;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Created by netcorner on 15/10/3.
 */
public class DatePickerDialog extends Dialog implements OnClickListener, OnDateChangedListener {

    private static final String START_YEAR = "start_year";
    private static final String END_YEAR = "end_year";
    private static final String START_MONTH = "start_month";
    private static final String END_MONTH = "end_month";
    private static final String START_DAY = "start_day";
    private static final String END_DAY = "end_day";

    private final DatePicker mDatePicker_start;
    //private final DatePicker mDatePicker_end;
    private final OnDateSetListener mCallBack;

    /**
     * The callback used to indicate the ic_user_white is done filling in the date.
     */
    public interface OnDateSetListener {


        void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth
        );
    }

    /**
     * @param context
     *            The context the dialog is to run in.
     * @param callBack
     *            How the parent is notified that the date is set.
     * @param year
     *            The initial year of the dialog.
     * @param monthOfYear
     *            The initial month of the dialog.
     * @param dayOfMonth
     *            The initial day of the dialog.
     */
    public DatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth, true,null,0);
    }
    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth, Date date) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth, true,date,0);
    }
    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth, int resId) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth, true,null,resId);
    }
    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth,Date date, int resId) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth, true,date,resId);
    }

    /**
     * @param context
     *            The context the dialog is to run in.
     * @param theme
     *            the theme to apply to this dialog
     * @param callBack
     *            How the parent is notified that the date is set.
     * @param year
     *            The initial year of the dialog.
     * @param monthOfYear
     *            The initial month of the dialog.
     * @param dayOfMonth
     *            The initial day of the dialog.
     */
    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth, boolean isDayVisible, final Date date,int resId) {
        super(context, theme);

        mCallBack = callBack;

        Context themeContext = getContext();
        //setButton(BUTTON_POSITIVE, themeContext.getString(R.string.confirm), this);
        //setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), this);
        // setButton(BUTTON_POSITIVE,
        // themeContext.getText(android.R.string.date_time_done), this);
        //setIcon(0);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(resId==0) resId=R.layout.widget_date_picker_dialog;
        View view = inflater.inflate(resId, null);
        View commendDateLayout=view.findViewById(R.id.commendDateLayout);
        if(date!=null){
            commendDateLayout.setVisibility(View.VISIBLE);
            //((TextView)view.findViewById(R.id.currentDate)).setText(DateUtils.getDate(date,"yyyy-MM-dd"));
            view.findViewById(R.id.currentDate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallBack.onDateSet(mDatePicker_start, Integer.parseInt(DateUtils.formatDate(date,"yyyy")), date.getMonth(),Integer.parseInt(DateUtils.formatDate(date,"dd")));
                    dismiss();
                }
            });
        }else{
            commendDateLayout.setVisibility(View.GONE);
        }
        view.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCallBack != null) {

                    tryNotifyDateSet();
                    dismiss();
                    //if(date!=null)
                        //mCallBack.onDateSet(mDatePicker_start, Integer.parseInt(DateUtils.formatDate(date,"yyyy")), date.getMonth(),Integer.parseInt(DateUtils.formatDate(date,"dd")));
                }
                //getButton(BUTTON_NEGATIVE).performClick();
            }
        });



        mDatePicker_start = (DatePicker) view.findViewById(R.id.datePickerStart);
        //mDatePicker_end = (DatePicker) view.findViewById(R.id.datePickerEnd);
        mDatePicker_start.init(year, monthOfYear, dayOfMonth, this);
        //mDatePicker_end.init(year, monthOfYear, dayOfMonth, this);
        // updateTitle(year, monthOfYear, dayOfMonth);

        Resources systemResources = Resources.getSystem();
        int numberPickerId = systemResources.getIdentifier("year", "id", "android");
        NumberPicker numberPicker = (NumberPicker) mDatePicker_start.findViewById(numberPickerId);
        setNumberPickerDivider(numberPicker);

        numberPickerId = systemResources.getIdentifier("month", "id", "android");
        numberPicker = (NumberPicker) mDatePicker_start.findViewById(numberPickerId);
        setNumberPickerDivider(numberPicker);

        numberPickerId = systemResources.getIdentifier("day", "id", "android");
        numberPicker = (NumberPicker) mDatePicker_start.findViewById(numberPickerId);
        setNumberPickerDivider(numberPicker);

        // 如果要隐藏当前日期，则使用下面方法。
        if (!isDayVisible) {
            hidDay(mDatePicker_start);
            //hidDay(mDatePicker_end);
        }


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Window dialogWindow=getWindow();
        WindowManager.LayoutParams localLayoutParams = dialogWindow.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        //dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        localLayoutParams.width = (int)(AndroidUtils.getScreenWidth(context)*0.8);


        setCanceledOnTouchOutside(true);


        setContentView(view,localLayoutParams);
    }

    /**
     * 隐藏DatePicker中的日期显示
     *
     * @param mDatePicker
     */
    private void hidDay(DatePicker mDatePicker) {
        Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
        for (Field datePickerField : datePickerfFields) {
            if ("mDaySpinner".equals(datePickerField.getName())) {
                datePickerField.setAccessible(true);
                Object dayPicker = new Object();
                try {
                    dayPicker = datePickerField.get(mDatePicker);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                // datePicker.getCalendarView().setVisibility(View.GONE);
                ((View) dayPicker).setVisibility(View.GONE);
            }
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        // Log.d(this.getClass().getSimpleName(), String.format("which:%d",
        // which));
        // 如果是“取 消”按钮，则返回，如果是“确 定”按钮，则往下执行
        if (which == BUTTON_POSITIVE)
            tryNotifyDateSet();
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (view.getId() == R.id.datePickerStart)
            mDatePicker_start.init(year, month, day, this);
        //if (view.getId() == R.id.datePickerEnd)
        //    mDatePicker_end.init(year, month, day, this);
        // updateTitle(year, month, day);
    }

    /**
     * 获得开始日期的DatePicker
     *
     * @return The calendar view.
     */
    public DatePicker getDatePicker() {
        return mDatePicker_start;
    }

//    /**
//     * 获得结束日期的DatePicker
//     *
//     * @return The calendar view.
//     */
//    public DatePicker getDatePickerEnd() {
//        return mDatePicker_end;
//    }

    /**
     * Sets the start date.
     *
     * @param year
     *            The date year.
     * @param monthOfYear
     *            The date month.
     * @param dayOfMonth
     *            The date day of month.
     */
    public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker_start.updateDate(year, monthOfYear, dayOfMonth);
    }
//
//    /**
//     * Sets the end date.
//     *
//     * @param year
//     *            The date year.
//     * @param monthOfYear
//     *            The date month.
//     * @param dayOfMonth
//     *            The date day of month.
//     */
//    public void updateEndDate(int year, int monthOfYear, int dayOfMonth) {
//        mDatePicker_end.updateDate(year, monthOfYear, dayOfMonth);
//    }

    private void tryNotifyDateSet() {
        if (mCallBack != null) {
            mDatePicker_start.clearFocus();
            //mDatePicker_end.clearFocus();
            mCallBack.onDateSet(mDatePicker_start, mDatePicker_start.getYear(), mDatePicker_start.getMonth(),
                    mDatePicker_start.getDayOfMonth());
        }
    }

    @Override
    protected void onStop() {
        // tryNotifyDateSet();
        super.onStop();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(START_YEAR, mDatePicker_start.getYear());
        state.putInt(START_MONTH, mDatePicker_start.getMonth());
        state.putInt(START_DAY, mDatePicker_start.getDayOfMonth());
//        state.putInt(END_YEAR, mDatePicker_end.getYear());
//        state.putInt(END_MONTH, mDatePicker_end.getMonth());
//        state.putInt(END_DAY, mDatePicker_end.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int start_year = savedInstanceState.getInt(START_YEAR);
        int start_month = savedInstanceState.getInt(START_MONTH);
        int start_day = savedInstanceState.getInt(START_DAY);
        mDatePicker_start.init(start_year, start_month, start_day, this);

        int end_year = savedInstanceState.getInt(END_YEAR);
        int end_month = savedInstanceState.getInt(END_MONTH);
        int end_day = savedInstanceState.getInt(END_DAY);
        //mDatePicker_end.init(end_year, end_month, end_day, this);

    }

    private void setNumberPickerDivider(NumberPicker numberPicker) {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            try{
                Field dividerField = numberPicker.getClass().getDeclaredField("mSelectionDivider");
                dividerField.setAccessible(true);
                ColorDrawable colorDrawable = new ColorDrawable(
                        ContextCompat.getColor(getContext(), R.color.colorPrimarySel));
                dividerField.set(numberPicker,colorDrawable);
                numberPicker.invalidate();
            }
            catch(NoSuchFieldException | IllegalAccessException | IllegalArgumentException e){

            }
        }
    }



}

