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

/**
 *
 * @author Christian
 */
public class MessageException extends IllegalArgumentException {
    private final Message messageObject;

    public MessageException(Message message) {
        super(message.getMessage());
        this.messageObject = message;
    }

    public Message getMessageObject() {
        return messageObject;
    }



}
