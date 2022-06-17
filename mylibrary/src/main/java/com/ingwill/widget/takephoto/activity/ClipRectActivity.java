/*
 * Copyright (C) 2014 zzl09
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License ic_at_gray
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ingwill.widget.takephoto.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import com.ingwill.mylibrary.R;
import com.ingwill.utils.BitmapUtil;
import com.ingwill.widget.clip.BitmapUtils;
import com.ingwill.widget.clip.ClipRectLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 剪切头像
 */
public class ClipRectActivity extends InnerActivity {

    protected Map<String,String> lang;
    public void setLang(Map<String,String> lang){
        this.lang=lang;
    }
    public Map<String,String> getLang(){
        if(lang==null){
            lang=new HashMap<>();
            lang.put("clip_photo_title",getString(R.string.takephoto_ClipPhotoTitle));
        }
        return lang;
    }

    ClipRectLayout mClipLayout;

    @Override
    public int getLayoutId() {
        return R.layout.clip_rect_activity_choosephoto;
    }

    @Override
    public String getHeaderTitle() {
        return getLang().get("clip_photo_title");
    }
    public  boolean isIconOpt(){
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        initView();
        initBitmap();
    }

    private void initView() {
        mClipLayout = (ClipRectLayout) findViewById(R.id.clip_layout);
        findViewById(R.id.optIconCtx).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clipBitmap();
            }
        });
    }
    public static final String EXTRA_KEY_IMAGE_PATH = "extra_key_image_path";

    private void initBitmap() {
        String imgPath = getIntent().getStringExtra(EXTRA_KEY_IMAGE_PATH);
        File file = new File(imgPath);
        if (file.exists()) {
            Window window = getWindow();

            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
            int height = wm.getDefaultDisplay().getHeight();

            mClipLayout.setSourceImage(BitmapUtils.createImageThumbnailScale(imgPath, height), window);
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClipLayout.onDestory();
    }
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private void clipBitmap() {
        Bitmap bitmap = mClipLayout.getBitmap();
        String fp=getPhotoFileName();



        File file= BitmapUtil.saveBmpToSd(bitmap,fp,100);


        if(file!=null){

            Intent intent = new Intent();
            intent.putExtra("filepath",file.getAbsolutePath());
            setResult(RESULT_OK, intent);
            finish();


        }
    }

}
