package com.ingwill.widget.textview.html.label;

public class DivFont implements IHTMLLabel{


	@Override
	public String htmlFormat(String str) {
		str = "<div>" + str + "</div>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 1;
	}
}
