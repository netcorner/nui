package com.ingwill.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.ingwill.widget.takephoto.utils.AlbumBitmapCacheHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Created by netcorner on 15/7/6.
 * 图像处理工具
 */
public class ImageTools {
    private static final String TAG="ImageTools";

    public static final String localPath= Environment.getExternalStorageDirectory()+"";

    public static Bitmap base64ToBitmap(String base64Data) {
        base64Data=base64Data.replace("data:image/png;base64,","");
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = "data:image/png;base64,"+Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public interface FileBitmapCallback{
        void setBitmap(Bitmap bitmap);
    }

    public static void getFileBitmap(Context context,String file,final FileBitmapCallback fileBitmapCallback){
        getFileBitmap(context,file,0,0,fileBitmapCallback);
    }


    public static void getFileBitmap(Context context,String file,int width,int height,final FileBitmapCallback fileBitmapCallback){
        AlbumBitmapCacheHelper.getInstance().addPathToShowlist(file);
        //得到缩略图
        Bitmap thumbnail = AlbumBitmapCacheHelper.getInstance().getBitmap(context,file, width, height, new AlbumBitmapCacheHelper.ILoadImageCallback() {
            @Override
            public void onLoadImageCallBack(Bitmap bitmap, String path, Object... objects) {
                fileBitmapCallback.setBitmap(bitmap);
            }
        });
        if(thumbnail!=null){
            fileBitmapCallback.setBitmap(thumbnail);
        }
    }


    public static void setImageViewBitmap(boolean iscenter, Context context, String url, ImageView imageView, int resid){
        if(StringUtils.isNullOrEmpty(url)){
            imageView.setImageResource(resid);
        }else {
            if (url.startsWith(localPath)) {
                url = "file://" + url;
            }
//            else if (url.startsWith("head")) {
//                imageView.setImageResource(ImageLoader.getHeadRes(url));
//                return;
//            } else if (!url.startsWith("http")) {
//                url = context.getString(R.string.serverURL) + url;
//            }
            DrawableCrossFadeFactory drawableCrossFadeFactory = new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build();
            if (iscenter) {
//                Glide.with(context).load(url)
//                        .crossFade()
//                        .centerCrop()
//                        .placeholder(resid)
//                        .into(imageView);

                Glide.with(context)
                        .load(url)
                        .placeholder(resid)
                        .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                        .centerCrop()
                        .into(imageView);
            } else {
//                Glide.with(context).load(url)
//                        .crossFade()
//                        .placeholder(resid)
//                        .into(imageView);
                Glide.with(context)
                        .load(url)
                        .placeholder(resid)
                        .transition(DrawableTransitionOptions.with(drawableCrossFadeFactory))
                        .into(imageView);
            }
        }
    }
    public static void setImageViewBitmap(Context context, String url, ImageView imageView, int resid){
        setImageViewBitmap(false,context,url,imageView,resid);
    }





    public static Bitmap res2Bitmap(Context context,int resID){
        Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(),resID);
        return bitmap;
    }




    public static int[] getImageWidthHeight(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         *options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth,options.outHeight};
    }


    public static Bitmap createBitmap(Context context, String str,int bgColor,int textColor){
        int h = AndroidUtils.dip2px(context, 15);
        Paint paint = new Paint();
        paint.setTextSize(h);
        int w = (int) paint.measureText(str);//计算文字实际占用的宽度
        int height = 10+h;//将高度+10防止绘制椭圆时左右的文字超出椭圆范围
        Bitmap bm = Bitmap.createBitmap(w+20, height, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(bm);

        Paint p2 = new Paint();
        RectF re = new RectF(5, 0, w+15, height);//矩形
        float roundPx = 14;
        p2.setAntiAlias(true);//设置Paint为无锯齿
        c.drawARGB(0, 0, 0, 0);// 透明色
        p2.setColor(context.getResources().getColor(bgColor));
        c.drawRoundRect(re, roundPx, roundPx, p2);//绘制圆角矩形

        Paint p1 = new Paint();
        p1.setColor(context.getResources().getColor(textColor));
        p1.setTextSize(AndroidUtils.dip2px(context, 15));

        c.drawText(str, 10,(h-4), p1);
        //c.save(Canvas.ALL_SAVE_FLAG);////28之前
        c.save();
        c.restore();
        return bm;
    }


    public static Bitmap decodeFileByOriginal(String filePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap img = BitmapFactory.decodeFile(filePath, options);
        return img;
    }

    public static Bitmap decodeFile(String filePath){
        return decodeFile(filePath,480,800,30);
    }
    public static Bitmap decodeFile(String filePath, int width, int height, int zipRate){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = computeScale(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if(bm == null){
            return  null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm,degree) ;
        ByteArrayOutputStream baos = null ;
        try{
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, zipRate, baos);





        }finally{
            try {
                if(baos != null)
                    baos.close() ;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm ;

    }




    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }



    public static Bitmap imageView2Bitmap(ImageView iv){
        return ((BitmapDrawable)iv.getDrawable()).getBitmap();
    }

    public static Bitmap ReadBitmapById(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;

    }
    

    public static Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap() ;
        }else if(drawable instanceof NinePatchDrawable){
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }else{
            return null ;
        }
    }

    public static Drawable bitampToDrawable(Bitmap bitmap)
    {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    public static Bitmap localImageToBitmap(String url){
        return BitmapFactory.decodeFile(url);
    }




    /**
     * 从服务器取图片
     *http://bbs.3gstdy.com
     * @param url
     * @return
     */
    public static Bitmap getHttpBitmap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setConnectTimeout(0);
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
//    public static Bitmap createQRImage(String url,int qr_width,int qr_height)
//    {
//        //判断URL合法性
//        if (url == null || "".equals(url) || url.length() < 1)
//        {
//            return null;
//        }
//        try
//        {
//
//            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
//            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
//            //图像数据转换，使用了矩阵转换
//            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, qr_width, qr_height, hints);
//            int[] pixels = new int[qr_width * qr_height];
//            //下面这里按照二维码的算法，逐个生成二维码的图片，
//            //两个for循环是图片横列扫描的结果
//            for (int y = 0; y < qr_height; y++)
//            {
//                for (int x = 0; x < qr_width; x++)
//                {
//                    if (bitMatrix.get(x, y))
//                    {
//                        pixels[y * qr_width + x] = 0xff000000;
//                    }
////                    else
////                    {
////                        pixels[y * qr_width + x] = 0xffffffff;
////                    }
//                }
//            }
//            //生成二维码图片的格式，使用ARGB_8888
//            Bitmap bitmap = Bitmap.createBitmap(qr_width, qr_height, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(pixels, 0, qr_width, 0, 0, qr_width, qr_height);
//            //显示到一个ImageView上面
//            return bitmap;
//        }
//        catch (WriterException e)
//        {
//            e.printStackTrace();
//        }
//        return null;
//    }

    public static String saveImageToGallery(Context context, Bitmap bmp) {
        String fp=Environment.getExternalStorageDirectory()+"/Pictures/"+context.getString(com.ingwill.mylibrary.R.string.app_name)+"/";
        return saveImageToGallery(context,fp,System.currentTimeMillis()+".jpg",bmp);
    }

    /**
     * 将bitmap文件存放于相册中
     * @param context
     * @param bmp
     */
    public static String saveImageToGallery(Context context,String filePath,String fileName, Bitmap bmp) {

        File appDir = new File(filePath);//通过这个Pictures文件夹可以解决相册通知的问题，只有像微信,QQ这样的APP才有优先权放在自己目录下会被发现
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        //String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            String result = MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        String saveFile=filePath+fileName;
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ saveFile)));
        return filePath;
    }





    /**
     * 根据uri得到相应的路径
     * @param context
     * @param uri
     * @return
     */
    public static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath  = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }





    public static File saveImageToSD(Bitmap bmp) {
        return saveImageToSD(bmp,100);
    }

    public static File saveImageToSD(Bitmap bmp,int compressRate) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "ingwill");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = UUID.randomUUID() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, compressRate, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 保存的图片背景是透明的
     * @param bmp
     * @return
     */
    public static File saveImageToSDPNG(Bitmap bmp) {
        return saveImageToSDPNG(bmp,100);
    }

    public static File saveImageToSDPNG(Bitmap bmp,int compressRate) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "ingwill");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = UUID.randomUUID() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, compressRate, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }



    /**
     * 按比例调整图片大小
     * @param bitmap
     * @param scale
     * @return
     */
    public static Bitmap ResizeBitmap(Bitmap bitmap, int scale)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(1/scale, 1/scale);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }



    public static Bitmap centerScaleBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        float fixR=0.8f;
        float r;
        if(w>=h){
            r=1f;
        }else{
            r=w*1.0f/h;
        }

        if(r<fixR){
            int wh = (int)(w/fixR);// 裁切后所取的正方形区域边长

            int retX =0;//基于原图，取正方形左上角x坐标
            int retY =(h-wh)/2;

            //下面这句是关键
            return Bitmap.createBitmap(bitmap, retX, retY, w, wh, null, false);


        }else{
            return bitmap;
        }
    }




    /**
     * 按正方形裁切图片
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;//基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        //下面这句是关键
        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    /**
     * 计算缩放比例
     */
    public static int computeScale(BitmapFactory.Options options, int width, int height) {
        if (options == null) return 1;
        int widthScale = (int)((float) options.outWidth / (float) width);
        int heightScale = (int)((float) options.outHeight / (float) height);
        //选择缩放比例较大的那个
        int scale = (widthScale > heightScale ? widthScale : heightScale);
        if (scale < 1) scale = 1;
        return scale;
    }

    private static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate){
        if(bitmap == null)
            return null ;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    //异步加载图片
    public  interface LoadImageCallback {
         void callback(Bitmap result);
    }

    //异步加载缩略图
    public static void asyncLoadSmallImage(String imageUri, LoadImageCallback callback) {
        new LoadSmallPicTask(imageUri, callback).execute();
    }


    private static class LoadSmallPicTask extends AsyncTask<Void, Void, Bitmap> {

        private final String imageUri;
        private LoadImageCallback callback;

        public LoadSmallPicTask(String imageUri, LoadImageCallback callback) {
            this.imageUri = imageUri;
            this.callback = callback;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return decodeFile(imageUri, 200, 200,15);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            callback.callback(result);
        }

    }

    /**
     * view转bitmap
     * @param addViewContent
     * @return
     */
    public static Bitmap getViewBitmap(View addViewContent) {

        addViewContent.setDrawingCacheEnabled(true);

        addViewContent.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        addViewContent.layout(0, 0,
                addViewContent.getMeasuredWidth(),
                addViewContent.getMeasuredHeight());

        addViewContent.buildDrawingCache();
        Bitmap cacheBitmap = addViewContent.getDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        return bitmap;
    }



    /**
     * 设置水印图片在左上角
     * @param Context
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskLeftTop(
            Context context, Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingTop) {
        return createWaterMaskBitmap(src, watermark,
                AndroidUtils.dip2px(context, paddingLeft), AndroidUtils.dip2px(context, paddingTop));
    }

    private static Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
                                                int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        //canvas.save(Canvas.ALL_SAVE_FLAG);//28之前
        canvas.save();
        // 存储
        canvas.restore();
        return newb;
    }

    /**
     * 设置水印图片在右下角
     * @param Context
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskRightBottom(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark,
                src.getWidth() - watermark.getWidth() - AndroidUtils.dip2px(context, paddingRight),
                src.getHeight() - watermark.getHeight() - AndroidUtils.dip2px(context, paddingBottom));
    }

    /**
     * 设置水印图片到右上角
     * @param Context
     * @param src
     * @param watermark
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap createWaterMaskRightTop(
            Context context, Bitmap src, Bitmap watermark,
            int paddingRight, int paddingTop) {
        return createWaterMaskBitmap( src, watermark,
                src.getWidth() - watermark.getWidth() - AndroidUtils.dip2px(context, paddingRight),
                AndroidUtils.dip2px(context, paddingTop));
    }

    /**
     * 设置水印图片到左下角
     * @param Context
     * @param src
     * @param watermark
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap createWaterMaskLeftBottom(
            Context context, Bitmap src, Bitmap watermark,
            int paddingLeft, int paddingBottom) {
        return createWaterMaskBitmap(src, watermark, AndroidUtils.dip2px(context, paddingLeft),
                src.getHeight() - watermark.getHeight() - AndroidUtils.dip2px(context, paddingBottom));
    }

    /**
     * 设置水印图片到中间
     * @param Context
     * @param src
     * @param watermark
     * @return
     */
    public static Bitmap createWaterMaskCenter(Bitmap src, Bitmap watermark) {
        return createWaterMaskBitmap(src, watermark,
                (src.getWidth() - watermark.getWidth()) / 2,
                (src.getHeight() - watermark.getHeight()) / 2);
    }

    /**
     * 给图片添加文字到左上角
     * @param context
     * @param bitmap
     * @param text
     * @return
     */
    public static Bitmap drawTextToLeftTop(Context context, Bitmap bitmap, String text,
                                           int size, int color, int paddingLeft, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(AndroidUtils.dip2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                AndroidUtils.dip2px(context, paddingLeft),
                AndroidUtils.dip2px(context, paddingTop) + bounds.height());
    }

    /**
     * 绘制文字到右下角
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingTop
     * @return
     */
    public static Bitmap drawTextToRightBottom(Context context, Bitmap bitmap, String text,
                                               int size, int color, int paddingRight, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(AndroidUtils.dip2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - AndroidUtils.dip2px(context, paddingRight),
                bitmap.getHeight() - AndroidUtils.dip2px(context, paddingBottom));
    }

    /**
     * 绘制文字到右上方
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingRight
     * @param paddingTop
     * @return
     */
    public static Bitmap drawTextToRightTop(Context context, Bitmap bitmap, String text,
                                            int size, int color, int paddingRight, int paddingTop) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(AndroidUtils.dip2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                bitmap.getWidth() - bounds.width() - AndroidUtils.dip2px(context, paddingRight),
                AndroidUtils.dip2px(context, paddingTop) + bounds.height());
    }

    /**
     * 绘制文字到左下方
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @param paddingLeft
     * @param paddingBottom
     * @return
     */
    public static Bitmap drawTextToLeftBottom(Context context, Bitmap bitmap, String text,
                                              int size, int color, int paddingLeft, int paddingBottom) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(AndroidUtils.dip2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                AndroidUtils.dip2px(context, paddingLeft),
                bitmap.getHeight() - AndroidUtils.dip2px(context, paddingBottom));
    }

    /**
     * 绘制文字到中间
     * @param context
     * @param bitmap
     * @param text
     * @param size
     * @param color
     * @return
     */
    public static Bitmap drawTextToCenter(Context context, Bitmap bitmap, String text,
                                          int size, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(AndroidUtils.dip2px(context, size));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        return drawTextToBitmap(context, bitmap, text, paint, bounds,
                (bitmap.getWidth() - bounds.width()) / 2,
                (bitmap.getHeight() + bounds.height()) / 2);
    }

    //图片上绘制文字
    private static Bitmap drawTextToBitmap(Context context, Bitmap bitmap, String text,
                                           Paint paint, Rect bounds, int paddingLeft, int paddingTop) {
        Bitmap.Config bitmapConfig = bitmap.getConfig();

        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawText(text, paddingLeft, paddingTop, paint);
        return bitmap;
    }

    /**
     * 缩放图片
     * @param src
     * @param w
     * @param h
     * @return
     */
    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }


    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
    /**
     * 图片旋转
     * @param bit
     * 旋转原图像
     *
     * @param degrees
     * 旋转度数
     *
     * @return
     * 旋转之后的图像
     *
     */
    public static Bitmap rotateImage(Bitmap bit, int degrees)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap tempBitmap = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),
                bit.getHeight(), matrix, true);
        return tempBitmap;
    }

}
