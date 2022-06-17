package com.ingwill.widget.textview.html.label;

public class ItalicFont implements IHTMLLabel{

	@Override
	public String htmlFormat(String str) {
		str = "<i>" + str + "</i>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 10;
	}
}
