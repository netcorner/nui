package com.ingwill.widget.textview.html.label;

//设置下划线
public class UnderLinerFont implements IHTMLLabel {

	public UnderLinerFont() {
		super();

	}

	@Override
	public String htmlFormat(String str) {
		str = "<u>" + str + "</u>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 10;
	}

}
