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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nextapp.echo2.app.Command;
import nextapp.echo2.app.filetransfer.Download;
import nextapp.echo2.webcontainer.CommandSynchronizePeer;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.service.JavaScriptService;

import org.w3c.dom.Element;

/**
 * A peer for <code>Download</code> commands.
 */
public class DownloadPeer implements CommandSynchronizePeer, Serializable {
   
    private static final Map ID_TO_DOWNLOAD_MAP = Collections.synchronizedMap(new HashMap());
    
    /**
     * Service to provide supporting JavaScript library.
     */
    public static final Service DOWNLOAD_SERVICE = JavaScriptService.forResource("Echo.DownloadComponent", 
            "/nextapp/echo2/webcontainer/filetransfer/resource/js/Download.js");

    static {
        WebRenderServlet.getServiceRegistry().add(DOWNLOAD_SERVICE);
    }
    
    /**
     * @see nextapp.echo2.webcontainer.CommandSynchronizePeer#render(RenderContext, Command)
     */
    public void render(RenderContext rc, Command command) {
        Download download = (Download)command;
        if (download.isActive()) {
            ServerMessage serverMessage = rc.getServerMessage();
            serverMessage.addLibrary(DOWNLOAD_SERVICE.getId());
            
            String id = download.getRenderId();
            ID_TO_DOWNLOAD_MAP.put(id,download);

            String serviceUri = DownloadService.INSTANCE.createUri(rc.getContainerInstance(),id);
            Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_UPDATE,
                    "EchoDownloadComponent.MessageProcessor", "init",  new String[]{}, new String[]{});
            Element itemElement = serverMessage.getDocument().createElement("echoDownload");
            itemElement.setAttribute("serviceUri", serviceUri);
            itemizedUpdateElement.appendChild(itemElement);
            
            download.setActive(false);
        }
    }
    
    /**
     * Accessor to internal Download Map for Download Service
     */
    static Download getDownload(String id){
        return (Download) ID_TO_DOWNLOAD_MAP.get(id);
    }
    
}
