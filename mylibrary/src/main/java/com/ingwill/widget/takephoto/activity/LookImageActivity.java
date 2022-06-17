package com.ingwill.widget.takephoto.activity;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ingwill.mylibrary.R;
import com.ingwill.widget.takephoto.utils.AlbumBitmapCacheHelper;
import com.ingwill.widget.takephoto.utils.SingleImageModel;
import com.ingwill.widget.takephoto.view.ZoomImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 查看图片
 */
public class LookImageActivity extends InnerActivity  implements ViewPager.OnPageChangeListener, View.OnClickListener{
    protected Map<String,String> lang;
    public void setLang(Map<String,String> lang){
        this.lang=lang;
    }
    public Map<String,String> getLang(){
        if(lang==null){
            lang=new HashMap<>();
            lang.put("choose_pic_num_out_of_index",getString(R.string.takephoto_choose_pic_num_out_of_index));
            lang.put("choose_pic_finish_with_num",getString(R.string.takephoto_choose_pic_finish_with_num));
            lang.put("choose_pic_finish",getString(R.string.takephoto_choose_pic_finish));
        }
        return lang;
    }


    private ViewPager viewPager;

    private MyViewPagerAdapter adapter;

    private ArrayList<SingleImageModel> allimages;
    ArrayList<String> picklist;
    /** 当前选中的图片 */
    private int currentPic;

    private int last_pics;
    private int total_pics;


    /** 选择的照片文件夹 */
    public final static String EXTRA_DATA = "extra_data";
    /** 所有被选中的图片 */
    public final static String EXTRA_ALL_PICK_DATA = "extra_pick_data";
    /** 当前被选中的照片 */
    public final static String EXTRA_CURRENT_PIC = "extra_current_pic";
    /** 剩余的可选择照片 */
    public final static String EXTRA_LAST_PIC = "extra_last_pic";
    /** 总的照片 */
    public final static String EXTRA_TOTAL_PIC = "extra_total_pic";

    @Override
    public int getLayoutId() {
        return R.layout.takephoto_activity_look_image;
    }

    @Override
    public String getHeaderTitle() {
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        initFindView();
        initData();
        headertitle.setText((currentPic+1)+"/"+picklist.size());
    }

    @Override
    public Toolbar getToolbar() {
        return null;
    }

    protected void initFindView() {
        viewPager = (ViewPager) findViewById(R.id.vp_content);


    }

    protected void initData() {
        allimages = (ArrayList<SingleImageModel>) getIntent().getSerializableExtra(EXTRA_DATA);


        picklist = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_ALL_PICK_DATA);
        if (picklist == null)
            picklist = new ArrayList<String>();
        currentPic = getIntent().getIntExtra(EXTRA_CURRENT_PIC, 0);

        last_pics = getIntent().getIntExtra(EXTRA_LAST_PIC, 0);
        total_pics = getIntent().getIntExtra(EXTRA_TOTAL_PIC, 9);

        setTitle((currentPic + 1) + "/" + getImagesCount());


        adapter = new MyViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);
        viewPager.setCurrentItem(currentPic);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        currentPic = position;
        ((TextView)findViewById(R.id.headertitle)).setText((currentPic + 1) + "/" + getImagesCount());

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick(View view) {
        toggleChooseState(currentPic);
        //如果被选中
        if(getChooseStateFromList(currentPic)){
            if (last_pics <= 0){
                toggleChooseState(currentPic);
                Toast.makeText(this, String.format(getLang().get("choose_pic_num_out_of_index"), total_pics), Toast.LENGTH_SHORT).show();
                return ;
            }
            picklist.add(getPathFromList(currentPic));
            last_pics --;
            if(last_pics == total_pics-1){
                getOptBtn().setTextColor(getResources().getColor(R.color.colorAccent));
            }
            getOptBtn().setText(String.format(getLang().get("choose_pic_finish_with_num"), total_pics-last_pics, total_pics));
        }else{
            picklist.remove(getPathFromList(currentPic));
            last_pics ++;
            if(last_pics == total_pics){
                getOptBtn().setTextColor(getResources().getColor(R.color.found_description_color));
                getOptBtn().setText(getLang().get("choose_pic_finish"));
            }else{
                getOptBtn().setText(String.format(getLang().get("choose_pic_finish_with_num"), total_pics-last_pics, total_pics));
            }
        }
    }

    private class MyViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return getImagesCount();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(LookImageActivity.this).inflate(R.layout.takephoto_zoom_image, null);
            final ZoomImageView zoomImageView = (ZoomImageView) view.findViewById(R.id.zoom_image_view);

            String url=getPathFromList(position);




            AlbumBitmapCacheHelper.getInstance().addPathToShowlist(url);
            zoomImageView.setTag(url);
            Bitmap bitmap;
            if(url.startsWith(Environment.getExternalStorageDirectory()+"")) {
                bitmap = AlbumBitmapCacheHelper.getInstance().getBitmap(LookImageActivity.this, url, 0, 0, new AlbumBitmapCacheHelper.ILoadImageCallback() {
                    @Override
                    public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
                        ZoomImageView view = ((ZoomImageView) viewPager.findViewWithTag(path));
                        if (view != null && bitmap != null)
                            ((ZoomImageView) viewPager.findViewWithTag(path)).setSourceImageBitmap(LookImageActivity.this, bitmap, LookImageActivity.this);
                    }
                }, position);

                if (bitmap != null){
                    zoomImageView.setSourceImageBitmap(LookImageActivity.this,bitmap, LookImageActivity.this);
                }

            }else{
                //bitmap=getImageLoader().getBitmapImage(url);



                Glide.with(LookImageActivity.this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        zoomImageView.setSourceImageBitmap(LookImageActivity.this,resource,LookImageActivity.this); //显示图片
                    }
                });


//                Glide.with(LookImageActivity.this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                        zoomImageView.setSourceImageBitmap(LookImageActivity.this,resource,LookImageActivity.this); //显示图片
//                    }
//                    @Override
//                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
//                        super.onLoadFailed(e, errorDrawable);
//
//                    }
//                });

//                Glide.with(LookImageActivity.this).load(url)
//                        .crossFade()
//                        .into(zoomImageView);


            }

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
            //AlbumBitmapCacheHelper.getInstance().removePathFromShowlist(getPathFromList(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * 通过位置获取该位置图片的path
     */
    private String getPathFromList(int position){
        return allimages.get(position).path;
    }

    /**
     * 通过位置获取该位置图片的选中状态
     */
    private boolean getChooseStateFromList(int position){
        return allimages.get(position).isPicked;
    }

    /**
     * 反转图片的选中状态
     */
    private void toggleChooseState(int position){
        allimages.get(position).isPicked = !allimages.get(position).isPicked;
    }

    /**
     * 获得所有的图片数量
     */
    private int getImagesCount(){
        return allimages.size();
    }


}
