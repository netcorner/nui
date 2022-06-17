package com.ingwill.widget.textview.spannable;

import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.widget.TextView;

import com.ingwill.widget.textview.html.label.IHTMLLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class TextForHTML {

	private ArrayList<Spanned> spannableList = new ArrayList<Spanned>();
	

	public void appendHtmlFormat(String str, IHTMLLabel htmlLaber) {
		spannableList.add(Html.fromHtml(htmlLaber.htmlFormat(str)));
	}

	public TextForHTML appendHtmlFormat(String str, IHTMLLabel... arr_htmlLaber) {
		sort(arr_htmlLaber);

		for (IHTMLLabel i:arr_htmlLaber) {
			str = i.htmlFormat(str);
		}
		spannableList.add(Html.fromHtml(str));
	    return this;
	}
	public TextForHTML appendHtmlFormat(ITextSpannable text_spanable) {
		spannableList.add(text_spanable.getSpannable());
	    return this;
	}
	
	public TextForHTML appendHtmlFormat(Spannable spanable) {
		spannableList.add(spanable);
	    return this;
	}
	
	public static Spannable getStringSpan(String str, IHTMLLabel arr_htmlLaber)
	{
		return (Spannable) Html.fromHtml(arr_htmlLaber.htmlFormat(str));
	}
	
	
	public ArrayList<Spanned> getSpannableList() {
		return spannableList;
	}

	public void setSpannableList(ArrayList<Spanned> spannableList) {
		this.spannableList = spannableList;
	}

	public static Spannable getStringSpan(String str, IHTMLLabel... arr_htmlLaber)
	{
		sort(arr_htmlLaber);
		for (IHTMLLabel i:arr_htmlLaber) {
			str = i.htmlFormat(str);
		}
		return (Spannable) Html.fromHtml(str);
		
	}
	
	public void setSpanned(TextView tv) {

		int length=spannableList.size();
		for(int i=0;i<length;i++)
		{
			tv.append(spannableList.get(i));

		}
		
	}

	
	//将标签按照优先级进行排序
	public static void sort(IHTMLLabel arr_htmlLaber){
	}
	public static void sort(IHTMLLabel... arr_htmlLaber){
		Arrays.sort(arr_htmlLaber,new Comparator<IHTMLLabel>(){
			@Override
			public int compare(IHTMLLabel p1, IHTMLLabel p2) {
				// TODO Auto-generated method stub
				return p2.labelpriority()-p1.labelpriority();
			}
		 } );
	}



}
