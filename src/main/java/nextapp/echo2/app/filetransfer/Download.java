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

import java.io.Serializable;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Command;
import nextapp.echo2.app.RenderIdSupport;

/**
 * A command that causes the client to download a file.
 * Executing this <code>Command</code> via ApplicationInstance.executeCommand()
 * will direct the client browser to download a file, as defined by the 
 * given <code>DownloadProvider</code>  
 */
public class Download implements Command, RenderIdSupport, Serializable {

    private String id;
    private boolean active;
    private DownloadProvider provider;

    /**
     * Creates a new inactive <code>Download</code> command with no download
     * provider.
     */
    public Download() {
        this(null, false);
    }
    
    /**
     * Creates a new <code>Download</code> command with the specified 
     * producer and active state.
     *
     * @param provider The <code>DownloadProvider</code> that will provide the
     *        file download.
     * @param active True if the file should be immediately downloaded by the 
     *        client.
     */
    public Download(DownloadProvider provider, boolean active) {
        super();
        this.provider = provider;
        this.active = active;
    }
    
    /** 
     * Returns the <code>DownloadProvider</code> that will provide the file
     * download.
     *
     * @return The <code>DownloadProvider</code> that will provide the file
     *         download.
     */
    public DownloadProvider getProvider() {
        return provider;
    }
    
    /**
     * Returns whether the download component is &quot;active&quot;.  If the
     * component is active, it will cause the client to download the file.
     *
     * @return True if the file should be immediately downloaded by the 
     *         client.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets whether the download component is &quot;active&quot;.  If the
     * component is active, it will cause the client to download the file.
     *
     * @param newValue True if the file should be immediately downloaded by 
     *        the client.
     */
    public void setActive(boolean newValue) {
        this.active = newValue;
    }

    /**    
     * Sets the <code>DownloadProvider</code> that will provide the file
     * download.
     *
     * @param newValue A <code>DownloadProvider</code> that will provide the file
     * download.
     */
    public void setProvider(DownloadProvider newValue) {
        this.provider = newValue;
    }
    
    /** 
     * @see nextapp.echo2.app.RenderIdSupport#getRenderId()
     */
    public String getRenderId() {
        if (id == null) {
            id = ApplicationInstance.generateSystemId();
        }
        return id;
    }
}
