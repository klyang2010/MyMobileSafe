package com.mymobilesafe.utils;

/**
 * 为了避免和json的关键字冲突，将 需要转成json格式的字符串 中的字符用中文的字符来替代
 */
public class JsonStrTools {
	/**
	 * @param json
	 *
	 * @return
	 *
	 */
	public static String changeStr(String json){
		json = json.replaceAll(",", "，");
		json = json.replaceAll(":", "：");
		json = json.replaceAll("\\[", "【");
		json = json.replaceAll("\\]", "】");
		json = json.replaceAll("\\{", "<"); 
		json = json.replaceAll("\\}", ">"); 
		json = json.replaceAll("\"", "“");
		
		return json.toString();
	}
}
