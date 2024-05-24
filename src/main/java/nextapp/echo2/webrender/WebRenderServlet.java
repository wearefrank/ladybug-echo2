/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
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

package nextapp.echo2.webrender;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nextapp.echo2.webrender.service.CoreServices;
import nextapp.echo2.webrender.service.DebugPaneService;

/**
 * Echo <code>HttpServlet</code> implementation.
 */
public abstract class WebRenderServlet extends HttpServlet {
    
    /** 
     * A <code>ThreadLocal</code> reference to the 
     * <code>Connection</code> relevant to the current thread.
     */ 
    private static final ThreadLocal activeConnection = new ThreadLocal();
    
    /**
     * A flag indicating whether caching should be disabled for all services.
     * This flag is for testing purposes only, and should be disabled for
     * production use.
     */
    public static final boolean DISABLE_CACHING = false;
    
    /**
     * Request parameter identifying requested <code>Service</code>.
     */
    public static final String SERVICE_ID_PARAMETER = "serviceId";
    
    /**
     * <code>Service</code> identifier of the 'default' service. 
     * The 'default' service is rendered when a client makes a request
     * without a service identifier and a session DOES exist.
     */
    public static final String SERVICE_ID_DEFAULT = "Echo.Default";
    
    /**
     * <code>Service</code> identifier of the 'new instance' service. 
     * The 'new instance' service is rendered when a client makes a request
     * without a service identifier and a session DOES NOT exist..
     */
    public static final String SERVICE_ID_NEW_INSTANCE = "Echo.NewInstance";
    
    /**
     * <code>Service</code> identifier of the 'session expired' service.
     * The 'session expired' service is rendered when a client makes a
     * request that has an identifier and is intended for an active session, 
     * but no session exists. 
     */
    public static final String SERVICE_ID_SESSION_EXPIRED = "Echo.Expired";
    
    /**
     * Global handler for multipart/form-data encoded HTTP requests.
     */
    private static MultipartRequestWrapper multipartRequestWrapper;
    
    private static final long startupTime = System.currentTimeMillis();
    
    /**
     * Global <code>ServiceRegistry</code>.
     */
    private static final ServiceRegistry services = new ServiceRegistry();

    static {
        CoreServices.install(services);
        services.add(DebugPaneService.INSTANCE);
    }
    
    /**
     * An interface implemented by a supporting object that will handle 
     * multipart/form-data encoded HTTP requests.  This type of request is
     * required for file uploads.  Echo does not provide internal support
     * for file uploads, but instead provides hooks for file-upload handling
     * components.  
     */
    public static interface MultipartRequestWrapper {
    
        /**
         * Returns a replacement <code>HttpServletRequest</code> object that
         * may be used to handle a multipart/form-data encoded HTTP request.
         *
         * @param request The HTTP request provided from the servlet container
         *        that has multipart/form-data encoding.
         * @return An HTTP request that is capable of handling 
         *         multipart/form-data encoding.
         */
        public HttpServletRequest getWrappedRequest(HttpServletRequest request)
        throws IOException, ServletException;
    }

    /**
     * Returns a reference to the <code>Connection</code> that is 
     * relevant to the current thread, or null if no connection is relevant.
     * 
     * @return the relevant <code>Connection</code>
     */
    public static final Connection getActiveConnection() {
        return (Connection) activeConnection.get();
    }
    
    /**
     * Returns the multipart/form-data encoded HTTP request handler.
     * 
     * @return The multipart/form-data encoded HTTP request handler.
     * @see #setMultipartRequestWrapper
     */
    public static MultipartRequestWrapper getMultipartRequestWrapper() {
        return multipartRequestWrapper;
    }
    
    /**
     * Sets the multipart/form-data encoded HTTP request handler.
     * The multipart request wrapper can only be set one time.  It should be set
     * in a static block of your Echo application.  This method will disregard
     * additional attempts to set the wrapper if the provided wrapper's class
     * is identical to the existing one.  If the wrapper is already set and the
     * new wrapper object's class is different or the wrapper is null, an
     * exception is thrown.
     *
     * @param multipartRequestWrapper The handler for multipart/form-data 
     *        encoded HTTP requests.
     * @throws IllegalStateException if the application attempts to change
     *        a previously set multipart request handler.
     */
    public static final void setMultipartRequestWrapper(MultipartRequestWrapper multipartRequestWrapper) {
        if (WebRenderServlet.multipartRequestWrapper == null) {
            WebRenderServlet.multipartRequestWrapper = multipartRequestWrapper;
        } else {
            if (multipartRequestWrapper == null || 
                    !WebRenderServlet.multipartRequestWrapper.getClass().getName().equals(
                    multipartRequestWrapper.getClass().getName())) {
                throw new IllegalStateException("MultipartRequestWrapper already set.");
            }
        }
    }
    
    /**
     * Handles a GET request.
     *
     * @see #process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public final void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        process(request, response);
    }
    
    /**
     * Handles a POST request.
     *
     * @see #process(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public final void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        process(request, response);
    }
    
    /**
     * Returns the service that corresponds to the specified Id.
     *
     * @param id The id of the service to return.
     * @return The service corresponding to the specified Id.
     */
    private static Service getService(UserInstance userInstance, String id) {
        Service service;
        
        service = services.get(id);
        if (id == null) {
            if (userInstance == null) {
                id = SERVICE_ID_NEW_INSTANCE;
            } else {
                id = SERVICE_ID_DEFAULT;
            }
        } else {
            if (userInstance == null) {
                id = SERVICE_ID_SESSION_EXPIRED;
            }
        }
        
        service = services.get(id);

        if (service == null) {
            if (SERVICE_ID_DEFAULT.equals(id)) {
                throw new RuntimeException("Service not registered: SERVICE_ID_DEFAULT");
            } else if (SERVICE_ID_NEW_INSTANCE.equals(id)) {
                throw new RuntimeException("Service not registered: SERVICE_ID_NEW_INSTANCE");
            } else if (SERVICE_ID_SESSION_EXPIRED.equals(id)) {
                throw new RuntimeException("Service not registered: SERVICE_ID_SESSION_EXPIRED");
            }
        }
        
        return service;
    }
    
    /**
     * Retrieves the global <code>ServiceRegistry</code>.
     * 
     * @return The global <code>ServiceRegistry</code>.
     */
    public static ServiceRegistry getServiceRegistry() {
        return services;
    }
    
    /**
     * Processes a HTTP request and generates a response.
     * 
     * @param request the incoming <code>HttpServletRequest</code>
     * @param response the outgoing <code>HttpServletResponse</code>
     */
    protected void process(HttpServletRequest request, HttpServletResponse response) 
    throws IOException, ServletException {
        Connection conn = null;
        try {
            conn = new Connection(this, request, response);
            activeConnection.set(conn);
            String serviceId = request.getParameter(SERVICE_ID_PARAMETER);
            Service service = getService(conn.getUserInstance(), serviceId);
            if (service == null) {
                throw new ServletException("Service id \"" + serviceId + "\" not registered.");
            }
            int version = service.getVersion();
            
            // Set caching directives.
            if ((!DISABLE_CACHING) && version != Service.DO_NOT_CACHE) {
                // Setting all of the following (possibly with the exception of "Expires")
                // are *absolutely critical* in order to ensure proper caching of resources
                // with Internet Explorer 6.  Without "Last-Modified", IE6 appears to not
                // cache images properly resulting in an substantially greater than expected
                // performance impact.
                response.setHeader("Cache-Control", "max-age=3600");
                response.setDateHeader("Expires", System.currentTimeMillis() + (86400000));
                response.setDateHeader("Last-Modified", startupTime);
            } else {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Expires", "0");
            }
            
            service.service(conn);
            
        } catch (ServletException ex) {
            if (conn != null) {
                conn.disposeUserInstance();
            }
            throw(ex);
        } catch (IOException ex) {
            if (conn != null) {
                conn.disposeUserInstance();
            }
            throw(ex);
        } catch (RuntimeException ex) {
            if (conn != null) {
                conn.disposeUserInstance();
            }
            throw(ex);
        } finally {
            activeConnection.set(null);
        }
    }
}
