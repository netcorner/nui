package com.ingwill.widget.textview.html.label;

public interface IHTMLLabel {

	/**
	 * <a href="..."> <b> <big> <blockquote> <br>
	 * <cite> <dfn> <div align="..."> <em>
	 * <font size="..." color="..." ic_head_ci="...">
	 * <h1>
	 * <h2>
	 * <h3>
	 * <h4>
	 * <h5>
	 * <h6>
	 * <i>
	 * <img src="...">
	 * <p>
	 * <small>
	 * <strike>
	 * <strong>
	 * <sub>
	 * <sup>
	 * <tt>
	 * <u>
	 * */

	public String htmlFormat(String str);
	
	//标签优先级
	public int labelpriority();
}
