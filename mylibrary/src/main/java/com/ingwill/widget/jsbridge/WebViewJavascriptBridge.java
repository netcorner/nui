package com.ingwill.widget.jsbridge;



public interface WebViewJavascriptBridge {
	
	void send(String data);
	void send(String data, CallBackFunction responseCallback);
	
	

}
