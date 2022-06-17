package com.ingwill.widget.textview.spannable;

import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class ActionSpan extends ClickableSpan implements ITextSpannable {

	public interface ClickListen {
		void onClick(View widget, String url);
	}

	private String url;
	private ClickListen click;

	private Spannable spannable;

	public ActionSpan(String url, Spannable spannable, ClickListen click) {
		super();

		this.url = url;
		this.click = click;

		this.spannable = spannable;
		spannable.setSpan(this, 0, spannable.length(),
				Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		// if(color==-1){
		// ds.setColor(ds.linkColor);
		// }
		// ds.setUnderlineText(isUnderline); //<span
		// style="color: red;">//去掉下划线</span>
		//
	}


	
	@Override
	public void onClick(View widget) {
		if (click != null) {
			click.onClick(widget, url);
		}
	}
	

	@Override
	public Spannable getSpannable() {
		// TODO Auto-generated method stub
		return spannable;
	}

}