package com.ingwill.widget.textview.html.label;


public class ColorFont implements IHTMLLabel{

	private String color;
	
	public ColorFont(String color) {
		super();
		this.color = color;
	}

	@Override
	public String htmlFormat(String str) {
		str = "<font color=" + color + ">" + str + "</font>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 9;
	}
	
}
