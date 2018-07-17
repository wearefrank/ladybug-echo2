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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A download provider that allows the download of a resource deployed with
 * the application.
 */
public class ResourceDownloadProvider extends AbstractDownloadProvider {
    
    private static final int BUFFER_SIZE = 4096;

    protected String contentType;
    private String resourceName;
    private boolean fileNameProvided = true;

    /**
     * Creates a new <code>ResourceDownloadProvider</code> to download the 
     * specified resource.
     * 
     * @param resourceName the name of the resource to download
     * @param contentType the content type of the resource
     */
    public ResourceDownloadProvider(String resourceName, String contentType) {
        super();
        this.contentType = contentType;
        this.resourceName = resourceName;
    }

    /**
     * @see nextapp.echo2.app.filetransfer.DownloadProvider#getContentType()
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Returns the file name of the resource based on the resource name.
     * Null will be returned if the <code>FileNameProvided</code> flag has
     * been set to false.
     * 
     * @see nextapp.echo2.app.filetransfer.DownloadProvider#getFileName()
     */
    public String getFileName() {
        if (fileNameProvided) {
            int lastSlash = resourceName.lastIndexOf("/");
            if (lastSlash == -1) {
                return resourceName;
            } else {
                return resourceName.substring(lastSlash + 1);
            }
        } else {
            return null;
        }
    }
    
    /**
     * Returns true if the file name will be provided (based on the resource name).
     * 
     * @return True if the file name will be provided (based on the resource name)
     */
    public boolean isFileNameProvided() {
        return fileNameProvided;
    }
    
    /**
     * Sets whether the file name will be provided (based on the resource name).
     * 
     * @param fileNameProvided true if the file name should be provided
     */ 
    public void setFileNameProvided(boolean fileNameProvided) {
        this.fileNameProvided = fileNameProvided;
    }

    /**
     * @see nextapp.echo2.app.filetransfer.DownloadProvider#writeFile(java.io.OutputStream)
     */
    public void writeFile(OutputStream out) throws IOException {
        InputStream in = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        
        try {
            in = ResourceDownloadProvider.class.getResourceAsStream(resourceName);
            if (in == null) {
                throw new IllegalArgumentException("Specified resource does not exist: " + resourceName + ".");
            }
            do {
                bytesRead = in.read(buffer);
                if (bytesRead > 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } while (bytesRead > 0);
        } finally {
            if (in != null) { try { in.close(); } catch (IOException ex) { } } 
        }
    }
}
