package com.ingwill.widget.textview.html.label;

public class BoldFont implements IHTMLLabel{


	//粗体
	@Override
	public String htmlFormat(String str) {
		str = "<b>" + str + "</b>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 10;
	}
}
