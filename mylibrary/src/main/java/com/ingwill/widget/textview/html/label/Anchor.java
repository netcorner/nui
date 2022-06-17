package com.ingwill.widget.textview.html.label;

public class Anchor implements IHTMLLabel {

	private String url;
	
	public Anchor(String url) {
		super();
		this.url = url;
	}

	//锚点
	@Override
	public String htmlFormat(String str) {
		str = "<a href=\""+url+"\">"+str+"</a>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 10;
	}

}
