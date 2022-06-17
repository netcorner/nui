package com.ingwill.widget.textview.html.label;

public class BigFont implements IHTMLLabel{


	//字体小化
	@Override
	public String htmlFormat(String str) {
		str = "<big>" + str + "</big>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 10;
	}
}
