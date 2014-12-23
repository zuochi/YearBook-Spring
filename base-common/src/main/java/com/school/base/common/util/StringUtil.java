/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.school.base.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 字符工具类
 *
 * @author work
 */
public class StringUtil {

    public static String convertIllegalStrings(String source) {
        source = StringUtils.replace(source, "<", "&lt;");
        source = StringUtils.replace(source, ">", "&gt;");
        return source;
    }
    
    public static String cammelToUnderlinePatterm(String str) {
		String regex = "[A-Z]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String [] words = str.split(regex);
		StringBuilder sb = new StringBuilder();
		int i =0 ;
		while(m.find()) {
			sb.append(words[i++]);
			sb.append("_");
			sb.append(str.charAt(m.start()));
		}
		sb.append(words[i]);
		return sb.toString();
    }
}
