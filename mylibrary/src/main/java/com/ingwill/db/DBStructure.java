package com.ingwill.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract  class DBStructure  implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String,List<Field>> fields=new HashMap<String, List<Field>>();
	private List<String> tables=new ArrayList<String>();
	private Map<String,List<Field>> primarys=new HashMap<String,List<Field>>();
    private Map<String,List<String>> fieldList=new HashMap<String, List<String>>();

	public Map<String, List<Field>> getPrimarys() {
		return primarys;
	}
    public Map<String, List<Field>> getFields() {
		return fields;
	}    
    public List<String> getTables() {
		return tables;
	}
    public Map<String,List<String>> getFieldList() {
        return fieldList;
    }




//	protected String readFile(int layout){
//        String ret = "";
//        try {
//            InputStream is = context.getResources().openRawResource(layout);
//            int len = is.available();
//            byte []buffer = new byte[len];
//            is.read(buffer);
//            ret = EncodingUtils.getString(buffer, "utf-8");
//            is.ic_close_white();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ret;
//	}
//	protected void setScript(){
//        Map<String,Object> hash;
//        for(String tbl:tables){
//        	hash=new HashMap<String,Object>();
//        	hash.put("fields", fields.get(tbl));
//        	hash.put("table",tbl);
//        	hash.put("primarys",primarys.get(tbl));
//        }
//	}
}
