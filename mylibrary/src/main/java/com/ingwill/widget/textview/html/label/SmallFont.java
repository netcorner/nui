package com.ingwill.widget.textview.html.label;

public class SmallFont implements IHTMLLabel{


	//字体小化
	@Override
	public String htmlFormat(String str) {
		str = "<small>" + str + "</small>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 10;
	}
}
