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
     * 页面 Id
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
     * 是否有返回键
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
        //用于显示哪个activity
         Log.d("APP-ACTIVITY",this.getClass().getName());
    }

    /**
     * 设置滑动后退的区块
     * @return
     */
    public float getSwipeEdgePercent() {
        return 0.05f;
    }

    /**
     * 设置侧滑回退
     */
    public  void setTouchBack(){
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                //.setSwipeEdge(200)//可滑动的范围。px。200表示为左边200px的屏幕
                .setSwipeEdgePercent(getSwipeEdgePercent())//可滑动的范围。百分比。0.2表示为左边20%的屏幕
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
     * 检查屏幕 横竖屏或者锁定就是固定
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
     *  设置屏幕不固定，绕过检查
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
     * 结束当前activity 并跳转新的 activity
     * @param cls
     */
    public void directToActivity(final Class<?> cls){
        directToActivity(cls,null);
    }

    /**
     * 结束当前activity 并跳转新的 activity
     * @param cls
     * @param bundle
     */
    public void directToActivity(final Class<?> cls,final Bundle bundle){
        directToActivity(this,cls,bundle);
    }
    /**
     * 结束当前activity 并跳转新的 activity
     * @param activity
     * @param cls
     */
    public static void directToActivity(Activity activity,final Class<?> cls){
        directToActivity(activity,cls,null);
    }

    /**
     * 结束当前activity 并跳转新的 activity
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
     * 跳转新页面
     * @param cls
     */
    public void openWindow(final Class<?> cls){
        openWindow(cls, R.anim.slide_left_in, R.anim.slide_left_out,0);
    }

    /**
     * 跳转新页面
     * @param cls
     * @param bundle
     */
    public void openWindow(final Class<?> cls,Bundle bundle){
        openWindow(cls, R.anim.slide_left_in, R.anim.slide_left_out,0,bundle);
    }

    /**
     * 跳转新页面
     * @param cls
     * @param result
     */
    public void openWindow(final Class<?> cls,final int result){
        openWindow(cls,R.anim.slide_left_in, R.anim.slide_left_out,result);
    }

    /**
     * 跳转新页面
     * @param cls
     * @param result
     * @param bundle
     */
    public void openWindow(final Class<?> cls,final int result,Bundle bundle){
        openWindow(cls,R.anim.slide_left_in, R.anim.slide_left_out,result,bundle);
    }

    /**
     * 跳转新页面
     * @param cls
     * @param enterAnim
     * @param exitAnim
     */
    public void openWindow(final Class<?> cls,final int enterAnim,final int exitAnim){
        openWindow(this,cls,enterAnim,exitAnim,0,null);
    }

    /**
     * 跳转新页面
     * @param cls
     * @param enterAnim
     * @param exitAnim
     * @param result
     */
    public void openWindow(final Class<?> cls,final int enterAnim,final int exitAnim,final int result){
        openWindow(this,cls,enterAnim,exitAnim,result,null);
    }

    /**
     * 跳转新页面
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
     * 跳转新页面
     * @param activity
     * @param cls
     * @param result
     * @param bundle
     */
    public static void openWindow(Activity activity, final Class<?> cls,int result,Bundle bundle){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,result,bundle);
    }

    /**
     * 跳转新页面
     * @param activity
     * @param cls
     * @param bundle
     */
    public static void openWindow(Activity activity, final Class<?> cls,Bundle bundle){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,0,bundle);
    }

    /**
     * 跳转新页面
     * @param activity
     * @param cls
     */
    public static void openWindow(Activity activity, final Class<?> cls){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,0,null);
    }

    /**
     * 跳转新页面
     * @param activity
     * @param cls
     * @param result
     */
    public static void openWindow(Activity activity,final Class<?> cls,final int result){
        openWindow(activity,cls,R.anim.slide_left_in, R.anim.slide_left_out,result,null);
    }

    /**
     * 跳转新页面
     * @param activity
     * @param cls
     * @param enterAnim
     * @param exitAnim
     */
    public static void openWindow(Activity activity,final Class<?> cls,final int enterAnim,final int exitAnim){
        openWindow(activity,cls,enterAnim, exitAnim,0,null);
    }

    /**
     * 跳转新页面
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
     * 点击某个 ID 返回
     * @param layout
     */
    public void closeWindow(int layout){
        closeWindow(layout,R.anim.slide_right_in, R.anim.slide_right_out);
    }

    /**
     * 点击某个 ID 返回（动画效果设置）
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
     * 返回
     */
    public void closeWindow(){
        closeWindow(R.anim.slide_right_in, R.anim.slide_right_out);
    }

    /**
     * 返回(设置动画)
     * @param enterAnim
     * @param exitAnim
     */
    public void closeWindow(final int enterAnim,final int exitAnim){
        closeWindow(this,enterAnim,exitAnim);
    }

    /**
     * 返回
     * @param activity
     */
    public static void closeWindow(Activity activity){
        closeWindow(activity,R.anim.slide_right_in, R.anim.slide_right_out);
    }

    /**
     * 返回
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
     * 检测网络
     */
    public void checkNetwork(){
        checkNetwork(this);
    }

    /**
     * 检测网络
     * @param activity
     */
    public static void checkNetwork(Context activity){
        if(!NetUtils.isNetworkAvailable(activity)){
            toast(activity,activity.getString(R.string.errornetwork));
        }
    }
    private int statusBarHeight;
    /**
     * 得到状态栏高度
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
     * 文本框光标在文字后面闪动
     * @param nn
     */
    public void selectionLast(EditText nn){
        //光标位置
        CharSequence text = nn.getText();
        if (text instanceof Spannable) {
            Spannable spanText = (Spannable)text;
            Selection.setSelection(spanText, text.length());
        }
    }

    /**
     * 显示输入法
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
     * 隐藏输入法
     * @param v
     */
    public  static   void hiddenIMM(View  v){
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive( ) ) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );

        }
    }

    /**
     * 隐藏输入法
     * @param activity
     */
    public  static   void hiddenIMM(Activity  activity){
        if(activity.getCurrentFocus()!=null)
            ((InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }



    /**
     * 实现文本复制功能
     * ic_add by shijiufeng
     * @param content
     */
    public static void copy(String content, Context context)
    {
// 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }
    /**
     * 实现粘贴功能
     * ic_add by shijiufeng
     * @param context
     * @return
     */
    public static String paste(Context context)
    {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText()+"";
    }

    /**
     * 检查当前系统是否已开启暗黑模式
     * @param context
     * @return
     */
    public static boolean isDarkMode(Context context) {
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return mode == Configuration.UI_MODE_NIGHT_YES;
    }


    /**
     * 震动
     * @param milliseconds
     */
    public void vibrate(long milliseconds) {
        vibrate(this,milliseconds);
    }

    /**
     * 震动
     */
    public void vibrate() {
        vibrate(this,100);
    }


    /**
     * 震动
     * @param activity
     * @param milliseconds
     */
    public static void vibrate(final Context activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * 震动（是否开启重复震动）
     * @param activity
     * @param pattern
     * @param isRepeat
     */
    public static void vibrate(final Context activity, long[] pattern,boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }

    /**
     * 提示消息
     * @param message
     */
    public void toast(String message){
        toast(this,message);
    }

    /**
     * 提示消息
     * @param message
     */
    public void toast(int message){
        toast(getString(message));
    }

    /**
     * 提示消息
     * @param context
     * @param message
     */
    public static void toast(Context context,int message){
        toast(context,context.getString(message));
    }
    /**
     * 提示消息
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
     * 加载框
     * @param loadmsg
     * @return
     */
    public Dialog showWaiting(Object loadmsg){
        return showWaiting(loadmsg,true);
    }

    /**
     * 加载框
     * @param loadmsg
     * @param isCancelable
     * @return
     */
    public Dialog showWaiting(Object loadmsg,boolean isCancelable){
        return showWaiting(this,loadmsg,isCancelable);
    }

    /**
     * 加载框
     * @param context
     * @param loadmsg
     * @return
     */
    public static Dialog showWaiting(Context context,int resId, Object loadmsg){
        return showWaiting(context,resId,loadmsg,true);
    }

    /**
     * 加载框
     * @param context
     * @param loadmsg
     * @return
     */
    public static Dialog showWaiting(Context context, Object loadmsg){
        return showWaiting(context,loadmsg,true);
    }

    /**
     * 加载框
     * @param context
     * @param loadmsg
     * @param isCancelable
     * @return
     */
    public static Dialog showWaiting(Context context,Object loadmsg, boolean isCancelable){
        return showWaiting(context,R.layout.common_dialog_loading,loadmsg,isCancelable);
    }
    /**
     * 加载框
     * @param context
     * @param loadmsg
     * @param isCancelable
     * @return
     */
    public static Dialog showWaiting(Context context,int resId, Object loadmsg, boolean isCancelable){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(resId, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
//        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
//        // 加载动画
//        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
//                context, R.anim.loading_animation);
//        // 使用ImageView显示动画
//        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if(loadmsg instanceof Integer) {
            tipTextView.setText(Integer.parseInt(loadmsg+""));// 设置加载信息
        }else{
            tipTextView.setText(loadmsg+"");// 设置加载信息
        }

        loadingDialog = new Dialog(context, R.style.Dialog);// 创建自定义样式dialog


        loadingDialog.setCancelable(isCancelable);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.show();
        return loadingDialog;
    }

    /**
     * 关闭加载框
     */
    public static void closeWaiting(){
        if(loadingDialog!=null)
            loadingDialog.dismiss();
    }

    /**
     * 确认框
     * @param title
     * @param confirmDialogCallback
     */
    public Dialog confirm(int title,final ConfirmCallback confirmDialogCallback){
        return confirm(this,title,confirmDialogCallback);
    }

    /**
     * 确认框
     * @param title
     * @param confirmDialogCallback
     */
    public Dialog confirm(String title,final ConfirmCallback confirmDialogCallback){
        return confirm(this,title,confirmDialogCallback);
    }

    /**
     * 确认框
     * @param activity
     * @param title
     * @param confirmDialogCallback
     */
    public static Dialog confirm(Activity activity, Object title, final ConfirmCallback confirmDialogCallback){
        return confirm(activity,title,R.layout.common_dialog_confirm,confirmDialogCallback);
    }

    /**
     * 确认框
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
        //width = dm.widthPixels;//宽度height = dm.heightPixels ;//高度
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
                customDialog.cancel();//注，此语句加上会执行setOnDismissListener
                if(confirmDialogCallback!=null) confirmDialogCallback.doCancel();
            }
        });
        View submitBtn=view1.findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                customDialog.cancel();//注，此语句加上会执行setOnDismissListener
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
     * 标题对话框
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
     * 标题对话框
     * @param title
     * @param callback
     * @return
     */
    public DialogPlus alert(Object title,TitleCallback callback) {
        return alert(this,title,callback);
    }

    /**
     * 标题对话框
     * @param context
     * @param title
     * @param callback
     * @return
     */
    public static DialogPlus alert(Activity context,Object title,TitleCallback callback) {
        return alert(context,R.layout.common_dialog_alert,title,callback);
    }

    /**
     * 标题对话框
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
     * 选择日期对话框
     * @param dialogCallback
     * @return
     */
    public DialogPlus showDateDialog(Date initDate, Date maxDate,DateDialogCallback dialogCallback){
        return showDateDialog(this,initDate,maxDate,dialogCallback);
    }

    /**
     * 选择日期对话框
     * @param resId
     * @param dialogCallback
     * @return
     */
    public DialogPlus showDateDialog(int resId,Date initDate, Date maxDate,DateDialogCallback dialogCallback){
        return showDateDialog(this,resId,initDate,maxDate,dialogCallback);
    }

    /**
     * 选择日期对话框
     * @param context
     * @param resId
     * @param dialogCallback
     * @return
     */
    public static DialogPlus showDateDialog(Activity context,int resId,Date initDate, Date maxDate, DateDialogCallback dialogCallback){
        return showDateDialog(context,resId,null,initDate,maxDate,dialogCallback);
    }

    /**
     * 选择日期对话框
     * @param context
     * @param dialogCallback
     * @return
     */
    public static DialogPlus showDateDialog(Activity context,Date initDate, Date maxDate, DateDialogCallback dialogCallback){
        return showDateDialog(context,R.layout.widget_date_picker_dialog,null,initDate,maxDate,dialogCallback);
    }
    /**
     * 选择日期对话框
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
     * 显示自定义对话框
     * @param resID
     * @param dialogCallback
     * @return
     */
    public DialogPlus showDialog(int resID,final DialogCallback  dialogCallback){
        return BaseActivity.showDialog(BaseActivity.this,resID,dialogCallback);
    }

    /**
     * 显示自定义对话框
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
//        //width = dm.widthPixels;//宽度height = dm.heightPixels ;//高度
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
     * 完成框回调事件
     */
    public interface TitleCallback{
        void doSubmit();
    }

    /**
     * 自定义对框回调事件
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
     * 确认框回调事件
     */
    public interface ConfirmCallback extends TitleCallback{
        void doCancel();
    }





    private static long lastClickTime=System.currentTimeMillis();
    /**
     * 防止双击点击两次activity
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
     * 设置滑动条参数
     * @param context
     * @param slidingTabLayout
     * @param selectedColorInt
     * @param unselectedColorInt
     * @param resID
     */
    public static void setSlidingTabLayoutParams(Context context,SlidingTabLayout slidingTabLayout,int selectedColorInt,int unselectedColorInt,int resID){
        int selectedColor=context.getResources().getColor(selectedColorInt);
        int unselectedColor=context.getResources().getColor(unselectedColorInt);
        slidingTabLayout.setTitleTextColor(selectedColor,unselectedColor);//标题字体颜色
        //slidingTabLayout.setTabStripWidth(110);//滑动条宽度
        slidingTabLayout.setSelectedIndicatorColors(selectedColor);//滑动条颜色
        //slidingTabLayout.setDistributeEvenly(true); //均匀平铺选项卡
        slidingTabLayout.setCustomTabView(resID, android.R.id.text1);
    }


























    
    
    




    
    
    //以下是权限6.0申明功能
    private PermissionsResultListener mListener;
    //界面传递过来的权限列表,用于二次申请
    private String[] mPermissionsList;
    //拒绝权限后是否关闭界面或APP
    protected boolean mNeedFinish = false;
    //申请标记值
    public static final int REQUEST_CODE_ASK_PERMISSIONS = 100;
    //手动开启权限requestCode
    public static final int SETTINGS_REQUEST_CODE = 200;

    /**
     * 权限允许或拒绝对话框
     *
     * @param permissions 需要申请的权限
     * @param needFinish  如果必须的权限没有允许的话，是否需要finish当前 Activity
     * @param callback    回调对象
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
            //获取未通过的权限列表
            List<String> newPermissions = checkEachSelfPermission(permissions);
            if (newPermissions.size() > 0) {// 是否有未通过的权限
                requestEachPermissions(newPermissions.toArray(new String[newPermissions.size()]));
            } else {// 权限已经都申请通过了
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
     * 申请权限前判断是否需要声明
     *
     * @param permissions
     */
    private void requestEachPermissions(String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {// 需要再次声明
            showRationaleDialog(permissions);
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    REQUEST_CODE_ASK_PERMISSIONS);
        }
    }
    protected boolean isRequestPermissions=false;

    /**
     * 弹出声明的 Dialog
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
     * 检察每个权限是否申请
     *
     * @param permissions
     * @return newPermissions.size > 0 表示有权限需要申请
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
     * 再次申请权限时，是否需要声明
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
     * 申请权限结果的回调
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
            } else {// 用户拒绝申请权限
                if (mListener != null) {
                    mListener.onPermissionDenied();
                }
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 检查回调结果
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
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限  false-表示权限已开启
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
     * 判断是否缺少权限
     */
    public static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果需要跳转系统设置页后返回自动再次检查和执行业务 如果不需要则不需要重写onActivityResult
        if (requestCode == SETTINGS_REQUEST_CODE) {
            requestPermission(mPermissionsList, mNeedFinish, mListener);
        }
    }




    /**
     * 数据加载接口
     */
    public interface DataLoadable{
        /**
         * 重新加载容器数据
         */
        void reloadDataContainer();

        /**
         * 隐藏容器数据
         */
        void hiddenDataContainer();
    }


    /**
     * 设置为全屏，状态栏覆盖
     */
    protected void setFullScreen(){
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
    }


    //protected int statusBarColor=R.color.colorPrimary;
    protected boolean statusFontBlack=true;
    //导航栏样式
    protected int systemUiVisibility;
    protected boolean isNotchScreen=false;

    /**
     * 设置默认成挖孔屏模式
     */
    protected void setNotchScreen(){
        setNotchScreen(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    /**
     * 设置自定义成挖孔屏模式
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
     * 设置为全屏
     */
    protected void setNewFullScreen(){
        if(systemUiVisibility==0) this.systemUiVisibility=View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        Window window = getWindow();
        //沉浸式状态栏
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
