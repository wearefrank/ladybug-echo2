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


/**
 * Factory for MultipartUploadSPI implementations.  This class is responsible 
 * for discovery of MultipartUploadSPI implementations and creation of 
 * new instances on demand.  
 * 
 * Custom MultipartUploadSPI implementations may be configured by calling 
 * <code>System.setProperty(MultipartUploadSPI.SYSTEM_PROPERTY_NAME, "com.example.CustomMultipartUploadSPI")</code>
 * from within an application entry point.  The given fully qualified implementation class 
 * <strong>must</strong> implement a public default (no argument) constructor.
 * 
 */
public class MultipartUploadFactory {
    
    protected static final Class DEFAULT_MULTIPART_UPLOAD_SPI = JakartaCommonsFileUploadProvider.class;
    protected static MultipartUploadSPI INSTANCE = null;
    
    /**
     * Attempts to find the configured MultipartUploadSPI implementation class by
     * checking for the System property setting 
     * <code>MultipartUploadSPI.SYSTEM_PROPERTY_NAME</code>.
     * If the property is null, a default implementation is created and 
     * returned.
     * 
     * @return the implementation
     */
    public static MultipartUploadSPI getMultipartUploadSPI(){
        if (INSTANCE == null) {
            INSTANCE = newInstance(getImplementationClass());
        }
        return INSTANCE;
    }
    
    /**
     * Attempts to locate the class defined by the System property 
     * nextapp.echo2.webcontainer.filetransfer.MultipartUploadSPI
     * 
     * @return the <code>MultipartUploadSPI</code> class if found, otherwise the default implementation.
     */
    private static Class getImplementationClass() {
        String implementationClassName = System.getProperty(MultipartUploadSPI.SYSTEM_PROPERTY_NAME);
        
        if (implementationClassName != null){
            try {
                return Class.forName(implementationClassName);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        } else {
            return DEFAULT_MULTIPART_UPLOAD_SPI;
        }
        
    }
    
    private static MultipartUploadSPI newInstance(Class clss){
        try {
            return (MultipartUploadSPI) clss.newInstance();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
