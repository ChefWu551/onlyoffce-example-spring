package com.example.onlineEditorFront.config.filter;

import org.apache.catalina.util.ParameterMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;

/**
* @author wuyuefeng
* @brief
* @email 565948592@qq.com
* @date 2020-04-24
*/
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private ParameterMap<String, String[]> params;

    @SuppressWarnings("all")
    public ParameterRequestWrapper(HttpServletRequest request) {
        super(request);
        params = (ParameterMap) request.getParameterMap();
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return params;
    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    public void addParameter(String name, Object value) {
        if (value != null) {
            params.setLocked(false);
            if (value instanceof String[]) {
                params.put(name, (String[]) value);
            } else if (value instanceof String) {
                params.put(name, new String[]{(String) value});
            } else {
                params.put(name, new String[]{String.valueOf(value)});
            }
            params.setLocked(true);
        }
    }
}
