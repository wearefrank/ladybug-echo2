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
import java.io.OutputStream;

/**
 * An interface for download-providing objects.  This interface specifies
 * methods for obtaining the content type, filename, size, and data of a
 * downloadable file.
 */
public interface DownloadProvider {

    /**
     * Returns the content type of the file.
     *
     * @return The content type of the file.
     */
    public String getContentType();

    /** 
     * Returns the file's name.  Returning null is allowed.
     *
     * @return The file's name.
     */
    public String getFileName();
    
    /**
     * Returns the size of the file.  If the size is unknown, -1 may be 
     * returned.
     *
     * @return The size of the file.
     */
    public int getSize();

    /** 
     * Writes the file to the specified output stream.
     *
     * @param out The output stream to which the file should be written.
     * @throws IOException If the provider is unable to perform this operation.
     */
    public void writeFile(OutputStream out)
    throws IOException;
}
