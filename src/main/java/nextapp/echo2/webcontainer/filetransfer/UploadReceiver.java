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

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import nextapp.echo2.app.filetransfer.UploadSelect;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.ContentType;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;

/**
 * Processes a file upload HTTP request from the client.
 */
public class UploadReceiver implements Serializable, Service {
    
    private static final String SERVICE_ID = "Echo.Upload"; 
    
    private static final String PARAMETER_UPLOAD_UID = "uploaduid"; 
    private static final String[] URL_PARAMETERS = new String[]{PARAMETER_UPLOAD_UID}; 
    
    public static final UploadReceiver INSTANCE = new UploadReceiver();
    
    static {
        WebRenderServlet.getServiceRegistry().add(INSTANCE);
    }
    
    /**
     * Creates a URI to execute a specific <code>UploadReceiver</code>
     * 
     * @param containerInstance the relevant application container instance.
     * @param imageId the unique id to retrieve the image from the
     *        <code>ContainerInstance</code>
     */
    public String createUri(ContainerInstance containerInstance, String imageId) {
        return containerInstance.getServiceUri(this, URL_PARAMETERS, new String[]{imageId});
    }
    
    /**
     * @see nextapp.echo2.webrender.Service#getId()
     */
    public String getId() {
        return SERVICE_ID;
    }
    
    /**
     * @see nextapp.echo2.webrender.Service#getVersion()
     */
    public int getVersion() {
        return DO_NOT_CACHE;
    }
    
    /**
     * @see nextapp.echo2.webrender.Service#service(Connection)
     */
    public void service(Connection conn) throws IOException {
        ContainerInstance containerInstance = (ContainerInstance)conn.getUserInstance();
        if (containerInstance == null) {
            serviceBadRequest(conn, "No container available.");
            return;
        }
        String uploadId = conn.getRequest().getParameter(PARAMETER_UPLOAD_UID);
        if (uploadId == null) {
            serviceBadRequest(conn, "Upload UID not specified.");
            return;
        }
        UploadSelect upload = (UploadSelect) containerInstance.getIdTable().getObject(uploadId);
        
        if (upload == null) {
            serviceBadRequest(conn, "Upload UID is not valid: " + uploadId);
            return;
        }
        service(conn,upload);
    }
    
    protected void service(Connection conn, UploadSelect uploadSelect) throws IOException {
        ContainerInstance instance = (ContainerInstance)conn.getUserInstance();
        
        MultipartUploadSPI provider = MultipartUploadFactory.getMultipartUploadSPI();
        try {
            
            provider.updateComponent(conn, uploadSelect);
            String id = uploadSelect.getRenderId();
            
            conn.getResponse().sendRedirect(UploadFormService.INSTANCE.createUri(instance,id));
            
        } catch (ServletException e) {
            throw new IOException(e.getMessage());
        }

    }
    
    protected void serviceBadRequest(Connection conn, String message) {
        conn.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
        conn.setContentType(ContentType.TEXT_PLAIN);
        conn.getWriter().write(message);
    }
}
