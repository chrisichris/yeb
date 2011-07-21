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


package org.yeb.mvc;

import java.io.IOException;
import java.net.URL;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.log4j.PropertyConfigurator;
import org.yeticl.YetiClassLoader;

/**
 *
 * @author Christian
 */
public class YebFilter implements javax.servlet.Filter{

    public static boolean isDevelopment() {
        ClassLoader cl = YebFilter.class.getClassLoader();
        URL u = cl.getResource("yebWebMain.class");
        return u == null;
    }

    private final javax.servlet.Filter filterImpl;
    private final ClassLoader ycl;

    public YebFilter() {
     
        //load the yeti filter implemetnation
        //to do so first load the module because this than compile the filter
        try{
            ycl = new YetiClassLoader(null,null,true);
            //this compiles the javaclass
            ycl.loadClass("org.yeb.mvc.filter");
            Class fc = ycl.loadClass("org.yeb.mvc.YebFilterImpl");
            filterImpl = (Filter) fc.newInstance();
        }
        catch (RuntimeException ex) {
            throw ex;
        }catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    public void init(FilterConfig fc) throws ServletException {
            filterImpl.init(fc);
    }

    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        filterImpl.doFilter(sr, sr1, fc);
    }

    public void destroy() {
            filterImpl.destroy();
    }

}
