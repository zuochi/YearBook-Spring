package com.school.base.common.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * URI过滤器Request封装类
 *
 * @author work
 */
public class URIHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private String encoding;
    private Map<String, String[]> map;

    public URIHttpServletRequestWrapper(HttpServletRequest request, String encoding)
            throws UnsupportedEncodingException {
        super(request);
        this.encoding = encoding;

        Map<String, String[]> lockedMap = super.getParameterMap();
        this.map = new HashMap<String, String[]>();
        Set<String> set = lockedMap.keySet();
        for (String key : set) {
            String[] values = lockedMap.get(key);
            if (values != null) {
                List<String> list = new ArrayList<String>();
                for (String value : values) {
                    list.add(value == null ? null : URLDecoder.decode(value, encoding));
                }
                this.map.put(key, list.toArray(new String[list.size()]));
            }
        }
    }

    @Override
    public String getRequestURI() {
        try {
            return super.getRequestURI() != null
                    ? URLDecoder.decode(super.getRequestURI(), encoding) : super.getRequestURI();
        } catch (UnsupportedEncodingException e) {
            return super.getRequestURI();
        }
    }

    @Override
    public StringBuffer getRequestURL() {
        try {
            if (super.getRequestURL() != null) {
                String requestURL = URLDecoder.decode(super.getRequestURL().toString(), encoding);
                StringBuffer buffer = new StringBuffer();
                buffer.append(requestURL);
                return buffer;
            } else {
                return super.getRequestURL();
            }
        } catch (UnsupportedEncodingException e) {
            return super.getRequestURL();
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.map;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> l = new Vector<String>(this.map.keySet());
        return l.elements();
    }

    @Override
    public String[] getParameterValues(String name) {
        Object v = this.map.get(name);
        if (v == null) {
            return null;
        } else if (v instanceof String[]) {
            return (String[]) v;
        } else if (v instanceof String) {
            return new String[]{(String) v};
        } else {
            return new String[]{v.toString()};
        }
    }

    @Override
    public String getParameter(String name) {
        Object v = this.map.get(name);
        if (v == null) {
            return null;
        } else if (v instanceof String[]) {
            String[] strArr = (String[]) v;
            if (strArr.length > 0) {
                return strArr[0];
            } else {
                return null;
            }
        } else if (v instanceof String) {
            return (String) v;
        } else {
            return v.toString();
        }
    }
}
