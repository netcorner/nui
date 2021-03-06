package com.ingwill.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Vibrator;
import android.text.Selection;
import android.text.Spannable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ingwill.listener.PermissionsResultListener;
import com.ingwill.mylibrary.R;
import com.ingwill.utils.DateUtils;
import com.ingwill.utils.NetUtils;
import com.ingwill.utils.ScreenUtils;
import com.ingwill.utils.StringUtils;
import com.ingwill.widget.swipback.SwipeBackHelper;
import com.ingwill.widget.tab.SlidingTabLayout;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by netcorner on 16/10/18.
 */
public class BaseActivity extends AppCompatActivity {

    /**
     * ?????? Id
     * @return
     */
    public int getLayoutId(){
        return 0;
    }

    protected Toolbar toolbar;
    /**
     *
     * @return
     */
    public Toolbar getToolbar(){
        if(toolbar==null){
            toolbar=(Toolbar)findViewById(R.id.toolbar);
        }
        return toolbar;
    }
    /**
     * ??????????????????
     * @return
     */
    public boolean isSwipBack() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            boolean result = fixOrientation();
            Log.d("fixOrientation", "onCreate fixOrientation when Oreo, result = " + result);
        }
        super.onCreate(savedInstanceState);
        if(getLayoutId()!=0) {
            setContentView(getLayoutId());
        }
        if(isSwipBack()) {
            if(getToolbar()!=null) {
                getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        closeWindow();
                    }
                });
            }
            setTouchBack();
         }
        //??????????????????activity
         Log.d("APP-ACTIVITY",this.getClass().getName());
    }

    /**
     * ???????????????????????????
     * @return
     */
    public float getSwipeEdgePercent() {
        return 0.05f;
    }

    /**
     * ??????????????????
     */
    public  void setTouchBack(){
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                //.setSwipeEdge(200)//?????????????????????px???200???????????????200px?????????
                .setSwipeEdgePercent(getSwipeEdgePercent())//?????????????????????????????????0.2???????????????20%?????????
                .setSwipeBackEnable(true)
                .setSwipeSensitivity(0.5f)
                .setSwipeRelateEnable(true)
                .setSwipeRelateOffset(800);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(isSwipBack()) SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog=null;
        if(isSwipBack())  SwipeBackHelper.onDestroy(this);
    }
    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    /**
     * ???????????? ?????????????????????????????????
     * @return
     */
    private boolean isTranslucentOrFloating(){
        boolean isTranslucentOrFloating = false;
        try {
            int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    /**
     *  ????????????????????????????????????
     * @return
     */
    private boolean fixOrientation(){
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ????????????activity ??????????????? activity
     * @param cls
     */
    public void directToActivity(final Class<?> cls){
        directToActivity(cls,null);
    }

    /**
     * ????????????activity ??????????????? activity
     * @param cls
     * @param bundle
     */
    public void directToActivity(final Class<?> cls,final Bundle bundle){
        directToActivity(this,cls,bundle);
    }
    /**
     * ????????????activity ??????????????? activity
     * @param activity
     * @param cls
     */
    public static void directToActivity(Activity activity,final Class<?> cls){
        directToActivity(activity,cls,null);
    }

    /**
     * ????????????activity ??????????????? activity
     * @param activity
     * @param cls
     * @param bundle
     */
    public static void directToActivity(Activity activity,final Class<?> cls,final Bundle bundle){
        Intent intent = new Intent(activity, cls);
        if(bundle!=null) intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.overridePendingTransition(0,0);
        activity.finish();
    }

    /**
     * ???????????????
     * @param cls
     */
    public void openWindow(final Class<?> cls){
        openWindow(cls, R.anim.slide_left_in, R.anim.slide_left_out,0);
    }

    /**
     * ???????????????
     * @param cls
     * @param bundle
     */
    public void openWindow(final Class<?> cls,Bundle bundle){
        openWindow(cls, R.anim.slide_left_in, R.anim.slide_left_out,0,bundle);
    }

    /**
     * ???????????????
     * @param cls
     * @param result
     */
    public void openWindow(final Class<?> cls,final int result){
        openWindow(cls,R.anim.slide_left_in, R.anim.slide_left_out,result);
    }

    /**
     * ???????????????
     * @param cls
     * @param result
     * @param bundle
     */
    public void openWindow(final Class<?> cls,final int result,Bundle bundle){
        openWindow(cls,R.anim.slide_left_in, R.anim.slide_left_out,result,bundle);
    }

    /**
     * ???????????????
     * @param cls
     * @param enterAnim
     * @param exitAnim
     */
    public void openWindow(final Class<?> cls,final int enterAnim,final int exitAnim){
        openWindow(this,cls,enterAnim,exitAnim,0,null);
    }

    /**
     * ???????????????
     * @param cls
     * @param enterAnim
     * @param exitAnim
     * @param result
     */
    public void openWindow(final Class<?> cls,final int enterAnim,final int exitAnim,final int result){
        openWindow(this,cls,enterAnim,exitAnim,result,null);
    }

    /**
     * ???????????????
     * @param cls
     * @param enterAnim
     * @param exitAnim
     * @param result
     * @param bundle
     */
    public void openWindow(final Class<?> cls,final int enterAnim,final int exitAnim,final int result,Bundle bundle){
        openWindow(this,cls,enterAnim,exitAnim,result,bundle);
    }

    /**
     * ???????????????
     * @param activity
     * @param cls
     * @param result
     * @param bundle
     */
    public static void openWindow(Activity activity, final Class<?> cls,int result,Bundle bundle){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,result,bundle);
    }

    /**
     * ???????????????
     * @param activity
     * @param cls
     * @param bundle
     */
    public static void openWindow(Activity activity, final Class<?> cls,Bundle bundle){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,0,bundle);
    }

    /**
     * ???????????????
     * @param activity
     * @param cls
     */
    public static void openWindow(Activity activity, final Class<?> cls){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,0,null);
    }

    /**
     * ???????????????
     * @param activity
     * @param cls
     * @param result
     */
    public static void openWindow(Activity activity,final Class<?> cls,final int result){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,result,null);
    }

    /**
     * ???????????????
     * @param activity
     * @param cls
     * @param enterAnim
     * @param exitAnim
     */
    public static void openWindow(Activity activity,final Class<?> cls,final int enterAnim,final int exitAnim){
        openWindow(activity,cls,enterAnim, exitAnim,0,null);
    }

    /**
     * ???????????????
     * @param activity
     * @param cls
     * @param enterAnim
     * @param exitAnim
     * @param result
     * @param bundle
     */
    public static void openWindow(Activity activity,final Class<?> cls,final int enterAnim,final int exitAnim,final int result,final Bundle bundle){
        Intent intent = new Intent(activity, cls);
        if(bundle!=null){
            intent.putExtras(bundle);
        }
        if(result>0){
            activity.startActivityForResult(intent, result);
        }else {
            activity.startActivity(intent);
        }
        if(enterAnim>0) {
            activity.overridePendingTransition(
                    enterAnim, exitAnim);
        }

    }

    /**
     * ???????????? ID ??????
     * @param layout
     */
    public void closeWindow(int layout){
        closeWindow(layout,R.anim.slide_right_in, R.anim.slide_right_out);
    }

    /**
     * ???????????? ID ??????????????????????????????
     * @param layout
     * @param enterAnim
     * @param exitAnim
     */
    public void closeWindow(int layout,final int enterAnim,final int exitAnim){
        View v = findViewById(layout);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
                overridePendingTransition(
                        enterAnim, exitAnim);

            }
        });
    }

    /**
     * ??????
     */
    public void closeWindow(){
        closeWindow(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    /**
     * ??????(????????????)
     * @param enterAnim
     * @param exitAnim
     */
    public void closeWindow(final int enterAnim,final int exitAnim){
        closeWindow(this,enterAnim,exitAnim);
    }

    /**
     * ??????
     * @param activity
     */
    public static void closeWindow(Activity activity){
        closeWindow(activity,R.anim.slide_right_in, R.anim.slide_right_out);
    }

    /**
     * ??????
     * @param activity
     * @param enterAnim
     * @param exitAnim
     */
    public static void closeWindow(Activity activity,final int enterAnim,final int exitAnim){
        activity.finish();
        activity.overridePendingTransition(
                enterAnim, exitAnim);
    }

    /**
     * ????????????
     */
    public void checkNetwork(){
        checkNetwork(this);
    }

    /**
     * ????????????
     * @param activity
     */
    public static void checkNetwork(Context activity){
        if(!NetUtils.isNetworkAvailable(activity)){
            toast(activity,activity.getString(R.string.errornetwork));
        }
    }
    private int statusBarHeight;
    /**
     * ?????????????????????
     * @return
     */
    public int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * ????????????????????????????????????
     * @param nn
     */
    public void selectionLast(EditText nn){
        //????????????
        CharSequence text = nn.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable)text;
            Selection.setSelection(spanText, text.length());
        }
    }

    /**
     * ???????????????
     * @param v
     */
    public static void showIMM(final View v){
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager)
                        v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }, 100);
    }

    /**
     * ???????????????
     * @param v
     */
    public  static   void hiddenIMM(View  v){
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive( ) ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

        }
    }

    /**
     * ???????????????
     * @param activity
     */
    public  static   void hiddenIMM(Activity  activity){
        if(activity.getCurrentFocus()!=null)
            ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }



    /**
     * ????????????????????????
     * ic_add by shijiufeng
     * @param content
     */
    public static void copy(String content, Context context)
    {
// ????????????????????????
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
    /**
     * ??????????????????
     * ic_add by shijiufeng
     * @param context
     * @return
     */
    public static String paste(Context context)
    {
        // ????????????????????????
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText()+"";
    }

    /**
     * ?????????????????????????????????????????????
     * @param context
     * @return
     */
    public static boolean isDarkMode(Context context) {
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }


    /**
     * ??????
     * @param milliseconds
     */
    public void vibrate(long milliseconds) {
        vibrate(this,milliseconds);
    }

    /**
     * ??????
     */
    public void vibrate() {
        vibrate(this,100);
    }


    /**
     * ??????
     * @param activity
     * @param milliseconds
     */
    public static void vibrate(final Context activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * ????????????????????????????????????
     * @param activity
     * @param pattern
     * @param isRepeat
     */
    public static void vibrate(final Context activity, long[] pattern,boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    /**
     * ????????????
     * @param message
     */
    public void toast(String message){
        toast(this,message);
    }

    /**
     * ????????????
     * @param message
     */
    public void toast(int message){
        toast(getString(message));
    }

    /**
     * ????????????
     * @param context
     * @param message
     */
    public static void toast(Context context,int message){
        toast(context,context.getString(message));
    }
    /**
     * ????????????
     * @param context
     * @param message
     */
    public static void toast(Context context,String message){
        Toast toast = Toast.makeText(context,
                message, Toast.LENGTH_SHORT);
        //toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private static Dialog loadingDialog;
    /**
     * ?????????
     * @param loadmsg
     * @return
     */
    public Dialog showWaiting(Object loadmsg){
        return showWaiting(loadmsg,true);
    }

    /**
     * ?????????
     * @param loadmsg
     * @param isCancelable
     * @return
     */
    public Dialog showWaiting(Object loadmsg,boolean isCancelable){
        return showWaiting(this,loadmsg,isCancelable);
    }

    /**
     * ?????????
     * @param context
     * @param loadmsg
     * @return
     */
    public static Dialog showWaiting(Context context,int resId, Object loadmsg){
        return showWaiting(context,resId,loadmsg,true);
    }

    /**
     * ?????????
     * @param context
     * @param loadmsg
     * @return
     */
    public static Dialog showWaiting(Context context, Object loadmsg){
        return showWaiting(context,loadmsg,true);
    }

    /**
     * ?????????
     * @param context
     * @param loadmsg
     * @param isCancelable
     * @return
     */
    public static Dialog showWaiting(Context context,Object loadmsg, boolean isCancelable){
        return showWaiting(context,R.layout.common_dialog_loading,loadmsg,isCancelable);
    }
    /**
     * ?????????
     * @param context
     * @param loadmsg
     * @param isCancelable
     * @return
     */
    public static Dialog showWaiting(Context context,int resId, Object loadmsg, boolean isCancelable){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(resId, null);// ????????????view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// ????????????
//        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// ????????????
//        // ????????????
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                context, R.anim.loading_animation);
//        // ??????ImageView????????????
//        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if(loadmsg instanceof Integer) {
            tipTextView.setText(Integer.parseInt(loadmsg+""));// ??????????????????
        }else{
            tipTextView.setText(loadmsg+"");// ??????????????????
        }

        loadingDialog = new Dialog(context, R.style.Dialog);// ?????????????????????dialog


        loadingDialog.setCancelable(isCancelable);// ?????????????????????????????????
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// ????????????
        loadingDialog.show();
        return loadingDialog;
    }

    /**
     * ???????????????
     */
    public static void closeWaiting(){
        if(loadingDialog!=null)
            loadingDialog.dismiss();
    }

    /**
     * ?????????
     * @param title
     * @param confirmDialogCallback
     */
    public Dialog confirm(int title,final ConfirmCallback confirmDialogCallback){
        return confirm(this,title,confirmDialogCallback);
    }

    /**
     * ?????????
     * @param title
     * @param confirmDialogCallback
     */
    public Dialog confirm(String title,final ConfirmCallback confirmDialogCallback){
        return confirm(this,title,confirmDialogCallback);
    }

    /**
     * ?????????
     * @param activity
     * @param title
     * @param confirmDialogCallback
     */
    public static Dialog confirm(Activity activity, Object title, final ConfirmCallback confirmDialogCallback){
        return confirm(activity,title,R.layout.common_dialog_confirm,confirmDialogCallback);
    }

    /**
     * ?????????
     * @param activity
     * @param title
     * @param resId
     * @param confirmDialogCallback
     */
    public static Dialog confirm(Activity activity,Object title,int resId,final ConfirmCallback confirmDialogCallback){
        //final Window window=activity.getWindow();
        //final WindowManager.LayoutParams lp = window.getAttributes();
        final Dialog customDialog = new Dialog(activity, R.style.Dialog);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view1 = LayoutInflater.from(activity).inflate(resId, null);

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //width = dm.widthPixels;//??????height = dm.heightPixels ;//??????
        Window dialogWindow=customDialog.getWindow();
        WindowManager.LayoutParams localLayoutParams = dialogWindow.getAttributes();
        localLayoutParams.gravity = Gravity.CENTER;
        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
        localLayoutParams.width = (int)(dm.widthPixels*0.8);
        customDialog.setContentView(view1, localLayoutParams);
        customDialog.setCanceledOnTouchOutside(true);

        TextView dialogTitle=(TextView)view1.findViewById(R.id.dialogTitle);

        if(title instanceof Integer){
            dialogTitle.setText((int)title);
        }else{
            dialogTitle.setText(title+"");
        }

        View cancelBtn=view1.findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                customDialog.cancel();//??????????????????????????????setOnDismissListener
                if(confirmDialogCallback!=null) confirmDialogCallback.doCancel();
            }
        });
        View submitBtn=view1.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                customDialog.cancel();//??????????????????????????????setOnDismissListener
                if(confirmDialogCallback!=null) confirmDialogCallback.doSubmit();
            }
        });
//        customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                //if(confirmDialogCallback!=null) confirmDialogCallback.doCancel();
//            }
//        });
        customDialog.show();
        return customDialog;
    }

    /**
     * ???????????????
     * @param title
     * @return
     */
    public DialogPlus alert(Object title) {
        return alert(this, title, new TitleCallback() {
            @Override
            public void doSubmit() {

            }
        });
    }
    /**
     * ???????????????
     * @param title
     * @param callback
     * @return
     */
    public DialogPlus alert(Object title,TitleCallback callback) {
        return alert(this,title,callback);
    }

    /**
     * ???????????????
     * @param context
     * @param title
     * @param callback
     * @return
     */
    public static DialogPlus alert(Activity context,Object title,TitleCallback callback) {
        return alert(context,R.layout.common_dialog_alert,title,callback);
    }

    /**
     * ???????????????
     * @param context
     * @param title
     * @param callback
     * @return
     */
    public static DialogPlus alert(Activity context,int resId,final Object title,final TitleCallback callback) {
        return showDialog(context,resId, new DialogCallback() {
            @Override
            public void setDialog(final DialogPlus dialog) {
                TextView dialogTitle=(TextView)dialog.findViewById(R.id.dialogTitle);
                if(title instanceof Integer){
                    dialogTitle.setText((int)title);
                }else{
                    dialogTitle.setText(title+"");
                }
                dialog.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if(callback!=null) callback.doSubmit();
                    }
                });
            }
            public int getGravity(){
                return Gravity.CENTER;
            }
        });
    }

    public static abstract class DateDialogCallback extends DialogCallback implements DatePicker.OnDateChangedListener{
        @Override
        public void onDateChanged(DatePicker datePicker, int startYear, int startMonthOfYear, int startDayOfMonth){

        }
        public abstract void onSelected(int startYear, int startMonthOfYear, int startDayOfMonth);
    }

    /**
     * ?????????????????????
     * @param dialogCallback
     * @return
     */
    public DialogPlus showDateDialog(Date initDate, Date maxDate,DateDialogCallback dialogCallback){
        return showDateDialog(this,initDate,maxDate,dialogCallback);
    }

    /**
     * ?????????????????????
     * @param resId
     * @param dialogCallback
     * @return
     */
    public DialogPlus showDateDialog(int resId,Date initDate, Date maxDate,DateDialogCallback dialogCallback){
        return showDateDialog(this,resId,initDate,maxDate,dialogCallback);
    }

    /**
     * ?????????????????????
     * @param context
     * @param resId
     * @param dialogCallback
     * @return
     */
    public static DialogPlus showDateDialog(Activity context,int resId,Date initDate, Date maxDate, DateDialogCallback dialogCallback){
        return showDateDialog(context,resId,null,initDate,maxDate,dialogCallback);
    }

    /**
     * ?????????????????????
     * @param context
     * @param dialogCallback
     * @return
     */
    public static DialogPlus showDateDialog(Activity context,Date initDate, Date maxDate, DateDialogCallback dialogCallback){
        return showDateDialog(context,R.layout.widget_date_picker_dialog,null,initDate,maxDate,dialogCallback);
    }
    /**
     * ?????????????????????
     * @param context
     * @param resId
     * @param title
     * @param initDate
     * @param maxDate
     * @param dialogCallback
     * @return
     */
    public static DialogPlus showDateDialog(Activity context, int resId, final Object title,Date initDate, Date maxDate,DateDialogCallback dialogCallback){
        return BaseActivity.showDialog(context,resId,new DialogCallback(){
            public int getGravity(){
                return dialogCallback.getGravity();
            }
            @Override
            public void setDialog(DialogPlus dialog) {
                dialogCallback.setDialog(dialog);

                TextView dialogTitle=(TextView)dialog.findViewById(R.id.dialogTitle);
                if(title instanceof Integer){
                    dialogTitle.setText((int)title);
                }else{
                    if(!StringUtils.isNullOrEmpty(title)) {
                        dialogTitle.setText(title + "");
                    }
                }


                DatePicker mDatePicker_start = (DatePicker) dialog.findViewById(R.id.datePickerStart);
                if(maxDate!=null) mDatePicker_start.setMaxDate(maxDate.getTime());

                if(initDate!=null){
                    Calendar c = DateUtils.date2Calendar(initDate);
                    int cyear=c.get(Calendar.YEAR);
                    int cmonth=c.get(Calendar.MONTH);
                    int cday=c.get(Calendar.DAY_OF_MONTH);
                    mDatePicker_start.init(cyear, cmonth, cday,dialogCallback);
                }



                View currentDate=dialog.findViewById(R.id.currentDate);
                if(currentDate!=null) {
                    currentDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Date now=new Date();
                            dialogCallback.onSelected(Integer.parseInt(DateUtils.formatDate(now, "yyyy")), now.getMonth(), Integer.parseInt(DateUtils.formatDate(now, "dd")));
                            dialog.dismiss();
                        }
                    });
                }

                dialog.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogCallback.onSelected(mDatePicker_start.getYear(), mDatePicker_start.getMonth(),
                                mDatePicker_start.getDayOfMonth());
                        dialog.dismiss();
                    }
                });
            }
            @Override
            public void doCancel(DialogPlus dialog) {
                hiddenIMM(context);
                dialogCallback.doCancel(dialog);
            }
        });
    }


    /**
     * ????????????????????????
     * @param resID
     * @param dialogCallback
     * @return
     */
    public DialogPlus showDialog(int resID,final DialogCallback  dialogCallback){
        return BaseActivity.showDialog(BaseActivity.this,resID,dialogCallback);
    }

    /**
     * ????????????????????????
     * @param context
     * @param resID
     * @param dialogCallback
     */
    public static DialogPlus showDialog(Activity context, int resID,final DialogCallback  dialogCallback){
//        final Dialog customDialog = new Dialog(context, R.style.Dialog);
//        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        View view1 = LayoutInflater.from(context).inflate(resID, null);
//
//        DisplayMetrics dm = new DisplayMetrics();
//        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        //width = dm.widthPixels;//??????height = dm.heightPixels ;//??????
//        Window dialogWindow=customDialog.getWindow();
//        WindowManager.LayoutParams localLayoutParams = dialogWindow.getAttributes();
//        localLayoutParams.gravity = Gravity.CENTER;
//        dialogWindow.getDecorView().setPadding(0, 0, 0, 0);
//        localLayoutParams.width = (int)(dm.widthPixels*0.8);
//        customDialog.setContentView(view1, localLayoutParams);
//        customDialog.setCanceledOnTouchOutside(true);
//        dialogCallback.setDialog(customDialog);
//        dialogCallback.complete(customDialog);
//        return customDialog;






        Holder holder=new ViewHolder(resID);
        final DialogPlus dialog = DialogPlus.newDialog(context)
                .setContentHolder(holder)
                .setCancelable(true)
                .setContentBackgroundResource(R.drawable.ic_no)
                .setGravity(dialogCallback.getGravity())
                .setExpanded(false)
                .setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogPlus dialog) {
                        if(dialogCallback!=null)
                            dialogCallback.doCancel(dialog);
                    }
                })
                .create();

        dialogCallback.setDialog(dialog);
        dialogCallback.complete(dialog);
        return dialog;
    }






    /**
     * ?????????????????????
     */
    public interface TitleCallback{
        void doSubmit();
    }

    /**
     * ???????????????????????????
     */
    public static abstract class DialogCallback{
        public void setDialog(DialogPlus dialog){

        }
        public void complete(DialogPlus dialog){
            dialog.show();
        }
        public void doCancel(DialogPlus dialog){

        }
        public int getGravity(){
            return Gravity.BOTTOM;
        }
    }


    /**
     * ?????????????????????
     */
    public interface ConfirmCallback extends TitleCallback{
        void doCancel();
    }





    private static long lastClickTime=System.currentTimeMillis();
    /**
     * ????????????????????????activity
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 1000) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }







    /**
     * ?????????????????????
     * @param context
     * @param slidingTabLayout
     * @param selectedColorInt
     * @param unselectedColorInt
     * @param resID
     */
    public static void setSlidingTabLayoutParams(Context context,SlidingTabLayout slidingTabLayout,int selectedColorInt,int unselectedColorInt,int resID){
        int selectedColor=context.getResources().getColor(selectedColorInt);
        int unselectedColor=context.getResources().getColor(unselectedColorInt);
        slidingTabLayout.setTitleTextColor(selectedColor,unselectedColor);//??????????????????
        //slidingTabLayout.setTabStripWidth(110);//???????????????
        slidingTabLayout.setSelectedIndicatorColors(selectedColor);//???????????????
        //slidingTabLayout.setDistributeEvenly(true); //?????????????????????
        slidingTabLayout.setCustomTabView(resID, android.R.id.text1);
    }


























    
    
    




    
    
    //???????????????6.0????????????
    private PermissionsResultListener mListener;
    //?????????????????????????????????,??????????????????
    private String[] mPermissionsList;
    //????????????????????????????????????APP
    protected boolean mNeedFinish = false;
    //???????????????
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    //??????????????????requestCode
    public static final int SETTINGS_REQUEST_CODE = 200;

    /**
     * ??????????????????????????????
     *
     * @param permissions ?????????????????????
     * @param needFinish  ??????????????????????????????????????????????????????finish?????? Activity
     * @param callback    ????????????
     */
    public void requestPermission(final String[] permissions, final boolean needFinish,
                                     final PermissionsResultListener callback) {
        if (permissions == null || permissions.length == 0) {
            return;
        }
        mNeedFinish = needFinish;
        mListener = callback;
        mPermissionsList = permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //??????????????????????????????
            List<String> newPermissions = checkEachSelfPermission(permissions);
            if (newPermissions.size() > 0) {// ???????????????????????????
                requestEachPermissions(newPermissions.toArray(new String[newPermissions.size()]));
            } else {// ??????????????????????????????
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            }
        } else {
            if (mListener != null) {
                mListener.onPermissionGranted();
            }
        }
    }

    /**
     * ???????????????????????????????????????
     *
     * @param permissions
     */
    private void requestEachPermissions(String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {// ??????????????????
            showRationaleDialog(permissions);
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }
    protected boolean isRequestPermissions=false;

    /**
     * ??????????????? Dialog
     *
     * @param permissions
     */
    protected void showRationaleDialog(final String[] permissions) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.tips)
                .setMessage(R.string.app_open_power)
                .setPositiveButton(R.string.confirm,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(BaseActivity.this, permissions,
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (mNeedFinish) finish();
                            }
                        })
                .setCancelable(false)
                .show();
    }

    /**
     * ??????????????????????????????
     *
     * @param permissions
     * @return newPermissions.size > 0 ???????????????????????????
     */
    private List<String> checkEachSelfPermission(String[] permissions) {
        List<String> newPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                newPermissions.add(permission);
            }
        }
        return newPermissions;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ???????????????????????????
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        isRequestPermissions=true;
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (checkEachPermissionsGranted(grantResults)) {
                if (mListener != null) {
                    mListener.onPermissionGranted();
                }
            } else {// ????????????????????????
                if (mListener != null) {
                    mListener.onPermissionDenied();
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * ??????????????????
     *
     * @param grantResults
     * @return
     */
    private boolean checkEachPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * ??????????????????
     * permissions ????????????
     * return true-?????????????????????  false-?????????????????????
     */
    public static boolean lacksPermissions(Context mContexts,String[] permissions) {
        for (String permission : permissions) {
            if (lacksPermission(mContexts,permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ????????????????????????
     */
    public static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //??????????????????????????????????????????????????????????????????????????? ?????????????????????????????????onActivityResult
        if (requestCode == SETTINGS_REQUEST_CODE) {
            requestPermission(mPermissionsList, mNeedFinish, mListener);
        }
    }




    /**
     * ??????????????????
     */
    public interface DataLoadable{
        /**
         * ????????????????????????
         */
        void reloadDataContainer();

        /**
         * ??????????????????
         */
        void hiddenDataContainer();
    }


    /**
     * ?????????????????????????????????
     */
    protected void setFullScreen(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// ????????????
    }


    //protected int statusBarColor=R.color.colorPrimary;
    protected boolean statusFontBlack=true;
    //???????????????
    protected int systemUiVisibility;
    protected boolean isNotchScreen=false;

    /**
     * ??????????????????????????????
     */
    protected void setNotchScreen(){
        setNotchScreen(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * ?????????????????????????????????
     * @param systemUiVisibility
     */
    protected void setNotchScreen(int systemUiVisibility){
        this.systemUiVisibility=systemUiVisibility;
        statusFontBlack=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.N){
                isNotchScreen= ScreenUtils.hasNotchScreen(this);
                if(isNotchScreen){
                    setNewFullScreen();
                }else{
                    setFullScreen();
                }
            }else {
                setFullScreen();
            }

        }
    }

    /**
     * ???????????????
     */
    protected void setNewFullScreen(){
        if(systemUiVisibility==0) this.systemUiVisibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        Window window = getWindow();
        //??????????????????
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(lp);
        }
    }
}
