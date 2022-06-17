package com.ingwill.widget.textview.html.label;



//设置字体大小
public class SizeFont implements IHTMLLabel{

    private int sizeFont;
    
    
    /**
     * sizeFont 在1-6之间
     * */
	public SizeFont(int sizeFont) {
		super();
		
		this.sizeFont = sizeFont;
		
	}


	@Override
	public String htmlFormat(String str) {
		str = "<h"+sizeFont+">" + str + "</h"+sizeFont+">";
		return str;
	}


	@Override
	public int labelpriority() {
		// TODO Auto-generated method stub
		return 2;
	}
}
