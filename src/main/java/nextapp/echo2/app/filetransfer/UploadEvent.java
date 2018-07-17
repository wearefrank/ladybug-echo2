/* 
 * This file is part of the Echo File Transfer Library (hereinafter "EFTL").
 * Copyright (C) 2002-2009 NextApp, Inc.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

package nextapp.echo2.app.filetransfer;

import java.io.InputStream;
import java.util.EventObject;

/**
 * An event that describes a file upload.
 */
public class UploadEvent extends EventObject {

    private String contentType;
    private String fileName;
    private InputStream inputStream;
    private int size;
    
    /**
     * Creates a new <code>UploadEvent</code>
     *
     * @param source the source of the event
     * @param inputStream an input stream referencing the uploaded file
     * @param size the size of the input stream
     * @param contentType the content type of the uploaded file
     * @param fileName the file name of the uploaded file
     */
    public UploadEvent(Object source, InputStream inputStream, int size, String contentType, String fileName) {
        super(source);
        
        this.inputStream = inputStream;
        this.size = size;
        this.contentType = contentType;
        this.fileName = fileName;
    }
    
    /**
     * Returns the content type of the uploaded file.
     *
     * @return the content type of the uploaded file
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Returns the file name of the uploaded file.
     *
     * @return the file name of the uploaded file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns an input stream containing the uploaded file.
     *
     * @return an input stream containing the uploaded file
     */
    public InputStream getInputStream() {
        return inputStream;
    }
    
    /**
     * Returns the size of the uploaded file, in bytes.
     *
     * @return the size of the uploaded file
     */
    public int getSize() {
        return size;
    }
}
