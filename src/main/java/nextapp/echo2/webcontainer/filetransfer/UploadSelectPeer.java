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
 */

package nextapp.echo2.webcontainer.filetransfer;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.filetransfer.UploadSelect;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * A peer for <code>UploadSelect</code> components.
 */
public class UploadSelectPeer 
implements DomUpdateSupport, ActionProcessor, PropertyUpdateProcessor, ComponentSynchronizePeer {

    /**
     * Default rendered height.
     */
    private static final Extent DEFAULT_HEIGHT = new Extent(70, Extent.PX);

    /**
     * Default rendered width.
     */
    private static final Extent DEFAULT_WIDTH = new Extent(280, Extent.PX);

    private static final Map ID_TO_ACTIVE_UPLOAD_MAP = Collections.synchronizedMap(new HashMap());

    static {
        MultipartUploadSPI requestWrapper = MultipartUploadFactory.getMultipartUploadSPI();
        WebRenderServlet.setMultipartRequestWrapper(requestWrapper);
    }

    /**
     * Adds the given <code>UploadEvent</code>
     */
    static final void activateUploadSelect(UploadSelect uploadSelect, UploadEvent uploadEvent) {
        ID_TO_ACTIVE_UPLOAD_MAP.put(uploadSelect, uploadEvent);
    }

    /**
     * Cleans up any task queue that was initiated via a call to
     * <code>activateUploadSelect</code>.
     */
    static final void deactivateUploadSelect(UploadSelect uploadSelect) {
        ID_TO_ACTIVE_UPLOAD_MAP.remove(uploadSelect);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("Component does not support children.");
    }

    /**
     * @see ActionProcessor#processAction(ContainerInstance, Component, Element)
     */
    public void processAction(ContainerInstance ci, Component component, Element propertyElement) {
        UploadSelect uploadSelect = (UploadSelect) component;
        processFileUpload(uploadSelect);
    }

    /**
     * Processes a file upload.
     * 
     * @param uploadSelect the <code>UploadSelect</code> uploading the file
     */
    protected void processFileUpload(UploadSelect uploadSelect) {
        UploadEvent event = (UploadEvent) ID_TO_ACTIVE_UPLOAD_MAP.get(uploadSelect);
        if (event != null) {
            try {
                FileInputStream in = new FileInputStream(event.getFile());
                uploadSelect.fileUpload(in, event.getFileSize(), event.getContentType(), event.getFileName());
                event.getFile().delete();
                deactivateUploadSelect(uploadSelect);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @see PropertyUpdateProcessor#processPropertyUpdate(ContainerInstance,
     *      Component, Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        // Do nothing.
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(RenderContext,
     *      ServerComponentUpdate, String, Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
        DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
        renderHtml(rc, update, htmlFragment, component);
        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, targetId, htmlFragment);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(RenderContext,
     *      ServerComponentUpdate, Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        UploadSelect uploadSelect = (UploadSelect) component;
        deactivateUploadSelect(uploadSelect);
        DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(component));
    }

    /**
     * @see DomUpdateSupport#renderHtml(RenderContext, ServerComponentUpdate,
     *      Node, Component)
     */
    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component component) {
        UploadSelect uploadSelect = (UploadSelect) component;

        Element parentDiv = parentNode.getOwnerDocument().createElement("div");
        parentDiv.setAttribute("id", ContainerInstance.getElementId(uploadSelect));
        Element iframe = parentNode.getOwnerDocument().createElement("iframe");
        iframe.setAttribute("scrolling", "no");
        CssStyle style = new CssStyle();
        style.setAttribute("border", "none");
        ExtentRender.renderToStyle(style, "height", (Extent) uploadSelect.getRenderProperty(UploadSelect.PROPERTY_HEIGHT,
                DEFAULT_HEIGHT));
        ExtentRender.renderToStyle(style, "width", (Extent) uploadSelect.getRenderProperty(UploadSelect.PROPERTY_WIDTH,
                DEFAULT_WIDTH));

        String id = uploadSelect.getRenderId();
        rc.getContainerInstance().getIdTable().register(uploadSelect);
        String uri = UploadFormService.INSTANCE.createUri(rc.getContainerInstance(), id);

        iframe.setAttribute("style", style.renderInline());
        iframe.setAttribute("src", uri);
        parentDiv.appendChild(iframe);

        parentNode.appendChild(parentDiv);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(RenderContext,
     *      ServerComponentUpdate, String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
        renderAdd(rc, update, targetId, update.getParent());
        return false;
    }
}
