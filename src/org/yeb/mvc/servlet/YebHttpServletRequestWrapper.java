/*
 * Copyright 2011 Christian Essl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */


package org.yeb.mvc.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author Christian
 */
public class YebHttpServletRequestWrapper extends HttpServletRequestWrapper{
    
    private final Map myParameters;
    private String pathInfo;
    private String servletPath;
    private Locale myLocale;
    static public final String[] EMPTY_STRING_ARRAY = new String[]{};

    public YebHttpServletRequestWrapper(Locale locale,String servletPath,String pathInfo,Map myparameters,HttpServletRequest request) {
        super(request);
        this.myLocale = locale == null ? request.getLocale() : locale;
        this.pathInfo = pathInfo == null ? request.getPathInfo() : pathInfo;
        this.servletPath = servletPath == null ? request.getServletPath() : servletPath;
        if(myparameters != null){
            this.myParameters = new HashMap(request.getParameterMap());
            this.myParameters.putAll(myparameters);
        }else {
            this.myParameters = request.getParameterMap();
        }
    }

    public YebHttpServletRequestWrapper(boolean map,HttpServletRequest parent) {
        this(null,null,null,map ? Collections.EMPTY_MAP : null,parent);
    }

    public Locale getLocale() {
        return myLocale;
    }

    public void setLocale(Locale myLocale) {
        this.myLocale = myLocale;
    }




    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public String getServletPath() {
        return this.servletPath;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    




    public String[] getParameterValues(String name) {
        return (String[]) myParameters.get(name);

    }

    public String getParameter(String name) {
        String[] vs = getParameterValues(name);
        if(vs == null) return null;
        if(vs.length == 0) return "";
        return vs[0];
    }

    public Map getParameterMap() {
        return myParameters;
    }

    public void putParameter(String name, String value) {
        String[] ar = value == null ? EMPTY_STRING_ARRAY : new String[]{value};
        myParameters.put(name, ar);
    }

    public void putParameters(String name, String[] values) {
        String[] ar = values == null ? EMPTY_STRING_ARRAY : values;
        myParameters.put(name,ar);
    }

    public Enumeration getParameterNames() {
        final Iterator it = myParameters.keySet().iterator();
        return new Enumeration(){

            public boolean hasMoreElements() {
                return it.hasNext();
            }

            public Object nextElement() {
                return it.next();
            }

        };
    }


}
