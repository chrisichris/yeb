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

package org.yeb;

import java.util.Locale;

/**
 *
 * @author Christian
 */
public class Message {

    private final String key;
    private final String defaultFormat;

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public Object[] getParams() {
        return params;
    }
    private final Object[] params;
    private final String message;

    public Message(String key, String defaultFormat, Object[] params) {
        this.key = key;
        this.defaultFormat = defaultFormat;
        this.params = params;
        this.message = String.format(defaultFormat, params);
    }

    public String getLocalMessage(Locale loc) {
        return String.format(loc,defaultFormat,params);
    }

    public String toString() {
        return getMessage();
    }



}
