package com.ingwill.widget.textview.spannable;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.io.IOException;

//从本地R文件中插入表情
public class ImgInAssets implements ITextSpannable {

	private String filepath;
	private String name;
	private Context context;

	public ImgInAssets(Context context, String filepath, String name) {
		super();
		this.filepath = filepath;
		this.name = name;
		this.context = context;
	}

	public Spannable getSpannable() {
		AssetManager asset_manager = context.getAssets();
		Bitmap bitmap;
		SpannableString spannableString = new SpannableString(
				name == null ? "[]" : name);
		try {
			bitmap = BitmapFactory.decodeStream(asset_manager.open(filepath));
			Drawable drawable = new BitmapDrawable(bitmap);

			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());

			ImageSpan imageSpan = new ImageSpan(drawable,
					ImageSpan.ALIGN_BASELINE);
			spannableString.setSpan(imageSpan, 0, spannableString.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			asset_manager = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return spannableString;
	}


}
