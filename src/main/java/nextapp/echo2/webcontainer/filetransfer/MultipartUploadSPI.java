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

package nextapp.echo2.webcontainer.filetransfer;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.ServletException;
import nextapp.echo2.app.filetransfer.UploadSelect;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.WebRenderServlet;

/**
 * This interface allows pluggable selection of the library to be used to parse 
 * the HttpServletRequest.  
 * Custom MultipartUploadSPI implementations may be configured by calling 
 * <code>System.setProperty(MultipartUploadSPI.SYSTEM_PROPERTY_NAME, "com.example.CustomMultipartUploadSPI")</code>
 * from within an application entry point.  The given fully qualified implementation class 
 * <strong>must</strong> implement a public default (no argument) constructor.
 */
public interface MultipartUploadSPI  extends WebRenderServlet.MultipartRequestWrapper   {
    
    static final String SYSTEM_PROPERTY_NAME = "nextapp.echo2.webcontainer.filetransfer.MultipartUploadSPI";
    
      
    /**
     * Implementations return <code>true</code> if they support caching the 
     * uploaded file to disk.  Implementations that return <code>true</code> 
     * must not throw <code>UnsupportedOperationException</code>s for the 
     * methods <code>setMemoryCacheThreshold()</code> and 
     * <code>setDiskCacheLocation()</code>.  
     * 
     * @return  true if the implementation is able to conserve memory by 
     *          storing the uploaded file to disk
     */
    boolean supportsDiskCaching();
    
    /**
     * Implementations return <code>true</code> if they support limiting the 
     * size of the upload.  Implementations that return <code>true</code> must 
     * not throw an <code>UnsupportedOperationException</code> for the method 
     * <code>setFileUploadSizeLimit()</code>.
     * 
     * @return  true if the implmentation is able to terminate uploads 
     *          larger than a specified size
     */
    boolean supportsFileUploadSizeLimit();
    
    /**
     * Implementations use the information from the HttpServletRequest in the 
     * Connection to call the <code>fileUpload()</code> method of the 
     * UploadSelect component.
     *  
     * @param   conn provides access to the HttpServletRequest
     * @param   uploadSelect component to be updated with the file upload 
     *          information
     * @throws  IOException if the Connection or the HttpServletRequest throws 
     *          an IOException during the operation
     * @throws  ServletException if Connection or the HttpServletRequest throws 
     *          an IOException during the operation
     */
    void updateComponent(Connection conn, UploadSelect uploadSelect) 
    throws IOException, ServletException;
    
    /**
     * Gets the size, in bytes, that the file upload must exceed before being 
     * cached to disk.  Some implementations that support disk caching may not 
     * support the storing of small uploads in memory.  Such implementations 
     * should ignore calls to this method and <em>not</em> throw an 
     * <code>UnsupportedOperationException</code>.
     * 
     * @return   bytes maximum size of the file upload before it will be cached 
     *          to disk
     * @throws  UnsupportedOperationException if this implementation does not 
     *          support disk caching
     */
    int getMemoryCacheThreshold()
    throws UnsupportedOperationException;
    
    /**
     * Gets the directory to be used for storing file uploads that exceed the 
     * memory cache threshold.  
     * 
     * @return  location to store uploaded files
     * @throws  IOException if the directory does not exist or the File throws 
     *          an IOException during this operation
     * @throws  UnsupportedOperationException if this implementation does not 
     *          support disk caching
     */
    File getDiskCacheLocation() 
    throws IOException, UnsupportedOperationException;
    
    /**
     * Gets the maximum size, in bytes, of file uploads to be accepted.  
     * Uploads that exceed the specified size are terminated or handled in a 
     * manner that minimizes the risk of a Denial of Service attack against 
     * the application.  
     * 
     * @return  the size threshold after which a file upload is rejected
     * @throws  UnsupportedOperationException if this implementation does not 
     *          support limiting file upload sizes
     */
    int getFileUploadSizeLimit() 
    throws UnsupportedOperationException;
   
}
