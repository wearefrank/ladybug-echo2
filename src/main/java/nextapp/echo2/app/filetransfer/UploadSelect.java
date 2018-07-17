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
import java.util.TooManyListenersException;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;

/**
 * A component that allows users to upload files to the application from 
 * remote clients.
 */
public class UploadSelect extends Component {

    public static final String PROPERTY_HEIGHT = "height"; 
    public static final String PROPERTY_WIDTH = "width"; 
    public static final String PROPERTY_ENABLED_SEND_TEXT = "enabledSendText"; 
    public static final String PROPERTY_DISABLED_SEND_TEXT = "disabledSendText"; 
    public static final String PROPERTY_DISPLAY_SEND_BUTTON = "displaySendButton";

    public static final String UPLOAD_LISTENER_CHANGED_PROPERTY = "uploadListener"; 

    private UploadListener uploadListener = null;
    
    /**
     * Creates an upload selector.
     */
    public UploadSelect() {
        super();
    }
    
    /**
     * Adds an <code>UploadListener</code> to be notified of file uploads.
     * This listener is <strong>unicast</strong>, only one may be added.
     * 
     * @param l The listener to add.
     */
    public void addUploadListener(UploadListener l) 
    throws TooManyListenersException {
        if (uploadListener != null) {
            throw new TooManyListenersException();
        } else {
            uploadListener = l;
            firePropertyChange(UPLOAD_LISTENER_CHANGED_PROPERTY,null,l);
        }
    }
    
    /**
     * Notifies the upload listener that a file has been uploaded.
     * 
     * @param in the <code>InputStream</code> containing the file
     * @param size the length of the input stream
     * @param contentType the content type of the uploaded file
     * @param filename the name of the file, as specified by the client 
     *        uploading it
     */
    public void fileUpload(InputStream in, int size, String contentType, String filename) {
        if (uploadListener != null) {
            UploadEvent e = new UploadEvent(this, in, size, contentType, filename);
            if (size == 0) {
                uploadListener.invalidFileUpload(e);
            } else {
                uploadListener.fileUpload(e);
            }
        }
    }
    
    /**
     * Returns the height of the upload select component.
     *
     * @return the height of the upload select component
     */
    public Extent getHeight() {
        return (Extent)getProperty(PROPERTY_HEIGHT);
    }
    
    /**
     * Returns text displayed in &quot;Send&quot; button when it is disabled.
     * 
     * @return the text displayed in &quot;Send&quot; button when it is 
     *         disabled
     */
    public String getDisabledSendButtonText() {
        return (String)getProperty(PROPERTY_DISABLED_SEND_TEXT);
    }
    
    /**
     * Returns text displayed in &quot;Send&quot; button when it is enabled.
     * 
     * @return the text displayed in &quot;Send&quot; button when it is 
     *         enabled
     */
    public String getEnabledSendButtonText() {
        return (String)getProperty(PROPERTY_ENABLED_SEND_TEXT);
    }
    
    /**
     * Returns the upload listener that will process file uploads when they 
     * occur.
     *
     * @return the upload listener that will process file uploads when they
     *         occur
     */
    public UploadListener getUploadListener() {
        return uploadListener;
    }
    
    /**
     * Returns the width of the upload select component.
     *
     * @return the width of the upload select component
     */
    public Extent getWidth() {
        return (Extent)getProperty(PROPERTY_WIDTH);
    }
    
    /**
     * Removes a (the) <code>UploadListener</code> from this 
     * <code>UploadSelect</code>.
     * 
     * @param l the listener to remove
     */
    public void removeUploadListener(UploadListener l) {
        if (l.equals(uploadListener)) {
            uploadListener = null;
        }
    }
    
    /**
     * Sets the height of the upload select component.
     *
     * @param newValue the new height value
     */
    public void setHeight(Extent newValue) {
        setProperty(PROPERTY_HEIGHT,newValue);
    }

    /**
     * Sets the upload listener that will process file uploads when they occur.
     *
     * @param newValue the <code>UploadListener</code> that will process file 
     *        uploads
     */
    public void setUploadListener(UploadListener newValue) {
        UploadListener oldValue = uploadListener;
        uploadListener = newValue;
        firePropertyChange(UPLOAD_LISTENER_CHANGED_PROPERTY,oldValue,newValue);
    }
    
    /**
     * Sets the width of the upload select component.
     *
     * @param newValue the new width value
     */
    public void setWidth(Extent newValue) {
        setProperty(PROPERTY_WIDTH,newValue);
    }
    
    /**
     * Sets the text displayed in the &quot;Send&quot; button when it is 
     * disabled.
     * 
     * @param string text for the disabled button
     */
    public void setDisabledSendButtonText(String string) {
        setProperty(PROPERTY_DISABLED_SEND_TEXT,string);
    }

    /**
     * Sets the text displayed in the &quot;Send&quot; button when it is 
     * enabled.
     * 
     * @param string text for the enabled button
     */
    public void setEnabledSendButtonText(String string) {
        setProperty(PROPERTY_ENABLED_SEND_TEXT,string);
    }

    /**
     * Returns whether the &quot;Send&quot; button should be displayed.  By 
     * default, the button is displayed for the benefit of View environments 
     * (such as certain Web browsers) that do not support the automatic upload 
     * of a file once selected.
     *  
     * @return true if the button will be displayed when the component is 
     *         rendered
     */
    public boolean isSendButtonDisplayed() {
        Boolean displayed = (Boolean)getProperty(PROPERTY_DISPLAY_SEND_BUTTON);
        if (displayed == null) {
            displayed = Boolean.TRUE;
        }
        return displayed.booleanValue();
    }

    /**
     * Sets whether the &quot;Send&quot; button should be displayed when the 
     * component is rendered.  This is a <em>hint</em> to the component's user 
     * interface peer and <em>may</em> be ignored.  Peers that know the button 
     * is necessary because of limitations of the View environment (for 
     * example, on some Web browsers) should ignore this hint.  
     * 
     * @param b hint that the &quot;Send&quot; button should or should not be displayed
     */
    public void setSendButtonDisplayed(boolean b) {
        setProperty(PROPERTY_DISPLAY_SEND_BUTTON,Boolean.valueOf(b));
    }
}


