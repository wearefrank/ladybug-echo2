/*
 * This file is part of the Echo File Transfer Library (hereinafter "EFTL").
 * Copyright (C) 2002-2009 NextApp, Inc.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or the
 * GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in which
 * case the provisions of the GPL or the LGPL are applicable instead of those
 * above. If you wish to allow use of your version of this file only under the
 * terms of either the GPL or the LGPL, and not to allow others to use your
 * version of this file under the terms of the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and other
 * provisions required by the GPL or the LGPL. If you do not delete the
 * provisions above, a recipient may use your version of this file under the
 * terms of any one of the MPL, the GPL or the LGPL.
 * 
 * 
 * NOTICE:
 * 
 * This product includes software developed by the Apache Software Foundation
 * (http://www.apache.org/).
 */

package nextapp.echo2.webcontainer.filetransfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import nextapp.echo2.app.filetransfer.UploadSelect;
import nextapp.echo2.webrender.Connection;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

/**
 * Implementation of MultipartUploadSPI that uses the Jakarta Commons FileUpload
 * library to perform the parsing of multipart HttpServletRequests.
 */
public class JakartaCommonsFileUploadProvider extends AbstractFileUploadProvider {

    /**
     * @see nextapp.echo2.webcontainer.filetransfer.MultipartUploadSPI#updateComponent(nextapp.echo2.webrender.Connection,
     *      nextapp.echo2.app.filetransfer.UploadSelect)
     */
    public void updateComponent(Connection conn, UploadSelect uploadSelect) throws IOException, ServletException {

        DiskFileUpload handler = null;
        HttpServletRequest request = null;
        List items = null;
        Iterator it = null;
        FileItem item = null;
        boolean searching = true;
        InputStream in = null;
        int size = 0;
        String contentType = null;
        String name = null;

        try {
            handler = new DiskFileUpload();
            handler.setSizeMax(getFileUploadSizeLimit());
            handler.setSizeThreshold(getMemoryCacheThreshold());
            handler.setRepositoryPath(getDiskCacheLocation().getCanonicalPath());

            request = conn.getRequest();
            items = handler.parseRequest(request);

            searching = true;
            it = items.iterator();
            while (it.hasNext() && searching) {
                item = (FileItem) it.next();
                if (UploadFormService.FILE_PARAMETER_NAME.equals(item.getFieldName())) {
                    in = item.getInputStream();
                    size = (int) item.getSize();
                    contentType = item.getContentType();
                    name = item.getName();

                    File tempFile = writeTempFile(in, uploadSelect);
                    UploadEvent uploadEvent = new UploadEvent(tempFile, size, contentType, name);
                    UploadSelectPeer.activateUploadSelect(uploadSelect, uploadEvent);

                    searching = false;
                }
            }
        } catch (FileUploadException e) {
            throw new IOException(e.getMessage());
        }
    }

}
