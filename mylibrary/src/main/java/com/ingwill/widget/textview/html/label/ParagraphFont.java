package com.ingwill.widget.textview.html.label;

/**段落*/
public class ParagraphFont implements IHTMLLabel {

	public ParagraphFont() {
		super();

	}

	@Override
	public String htmlFormat(String str) {
		str = "<p>" + str + "</p>";
		return str;
	}

	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 1;
	}

}
