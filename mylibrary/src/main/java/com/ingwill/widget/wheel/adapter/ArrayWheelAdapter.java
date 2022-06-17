package com.ingwill.widget.wheel.adapter;


import com.ingwill.widget.wheel.entity.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The simple Array wheel adapter
 * @param
 */
public class ArrayWheelAdapter implements WheelAdapter {
	

	// items
	private List<KeyValue> items;
	private Map<Object,Integer> dir;

	/**
	 * Constructor
	 * @param items the items
	 */
	public ArrayWheelAdapter(List<KeyValue> items) {
		this.items = items;
		if(items.size()>0) {
			if(items.get(0).getPrimaryValue()!=null) {
				dir = new HashMap<>();
				int i=0;
				for (KeyValue keyValue : items) {
					dir.put(keyValue.getPrimaryValue(), i);
					i++;
				}
			}
		}
	}

	public Map getDir() {
		return dir;
	}

	@Override
	public Object getItem(int index) {
		if (index >= 0 && index < items.size()) {
			return items.get(index).getKey();
		}
		return "";
	}

	@Override
	public int getItemsCount() {
		return items.size();
	}

	@Override
	public int indexOf(Object o){
		return items.indexOf(o);
	}

}
