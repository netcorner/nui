package com.ingwill.widget.textview.spannable;

import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.text.Spannable;

public class HtmlXmlParse implements ITextSpannable {

	private String xml;
	private ImageGetter imageGetter;
	private TagHandler tagHandler;

	public HtmlXmlParse(String xml) {
		super();
		this.xml = xml;
	}

	@Override
	public Spannable getSpannable() {
		// TODO Auto-generated method stub

		return (Spannable) Html.fromHtml(xml, imageGetter, tagHandler);
	}

	public HtmlXmlParse(String xml, ImageGetter imageGetter,
						TagHandler tagHandler) {
		super();
		this.xml = xml;
		this.imageGetter = imageGetter;
		this.tagHandler = tagHandler;
	}

}
