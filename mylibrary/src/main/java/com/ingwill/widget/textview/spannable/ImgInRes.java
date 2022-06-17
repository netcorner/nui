package com.ingwill.widget.textview.spannable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;


//从本地R文件中插入表情
public class ImgInRes implements ITextSpannable {

	

	private int resId;
	private String name;
	private Context context;
	


	public ImgInRes(Context context, int resId, String name) {
		super();
		this.resId = resId;
		this.name = name;
		this.context = context;
	}




	public Spannable getSpannable() {
		Drawable drawable = context.getResources().getDrawable(resId);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		SpannableString spannableString = new SpannableString(name==null?"[]":name);
		ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		spannableString.setSpan(imageSpan, 0, spannableString.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableString;
	}
    
	
	
	

//	 //添加		
//	ImageGetter imageGetter = new ImageGetter() {  
//        @Override
//       public Drawable getDrawable(String source) {
//       int id = Integer.parseInt(source);
//
//      //根据id从资源文件中获取图片对象
//       Drawable d = getResources().getDrawable(id);
//       d.setBounds(0, 0, d.getIntrinsicWidth(),d.getIntrinsicHeight());
//        return d;
//       }
//       };          
//       tv.append(Html.fromHtml("21344<img src='"+R.drawable.android+"'/>", imageGetter, new TagHandler() {
//		
//		@Override
//		public void handleTag(boolean arg0, String arg1, Editable arg2,
//				XMLReader arg3) {
//			// TODO Auto-generated method stub
//			System.out.println(arg0);
//			System.out.println(arg1);
//			System.out.println(arg2);
//			System.out.println(arg3);
//		}
//	})); 



}
