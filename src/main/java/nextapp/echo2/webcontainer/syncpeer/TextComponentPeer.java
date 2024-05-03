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

package nextapp.echo2.webcontainer.syncpeer;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.TextArea;
import nextapp.echo2.app.text.TextComponent;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ActionProcessor;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.FocusSupport;
import nextapp.echo2.webcontainer.PartialUpdateManager;
import nextapp.echo2.webcontainer.PartialUpdateParticipant;
import nextapp.echo2.webcontainer.PropertyUpdateProcessor;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.image.ImageRenderSupport;
import nextapp.echo2.webcontainer.partialupdate.BorderUpdate;
import nextapp.echo2.webcontainer.partialupdate.ColorUpdate;
import nextapp.echo2.webcontainer.partialupdate.InsetsUpdate;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webcontainer.propertyrender.FillImageRender;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webcontainer.propertyrender.LayoutDirectionRender;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;
import nextapp.echo2.webrender.servermessage.DomUpdate;
import nextapp.echo2.webrender.servermessage.WindowUpdate;
import nextapp.echo2.webrender.service.JavaScriptService;
import nextapp.echo2.webrender.util.DomUtil;

/**
 * Abstract base synchronization peer for the built-in
 * <code>nextapp.echo2.app.text.TextComponent</code> -derived components.
 * <p>
 * This class should not be extended or used by classes outside of the Echo
 * framework.
 */
public abstract class TextComponentPeer 
implements ActionProcessor, ComponentSynchronizePeer, DomUpdateSupport, FocusSupport, ImageRenderSupport, PropertyUpdateProcessor {

    private static final String IMAGE_ID_BACKGROUND = "background";

    /**
     * Service to provide supporting JavaScript library.
     */
    static final Service TEXT_COMPONENT_SERVICE = JavaScriptService.forResource("Echo.TextComponent",
            "/nextapp/echo2/webcontainer/resource/js/TextComponent.js");

    static {
        WebRenderServlet.getServiceRegistry().add(TEXT_COMPONENT_SERVICE);
    }
    
    /**
     * A <code>PartialUpdateParticipant</code> to update the text of
     * a text component.
     */
    private class TextUpdate
    implements PartialUpdateParticipant {
    
        /**
         * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#canRenderProperty(nextapp.echo2.webcontainer.RenderContext, 
         *      nextapp.echo2.app.update.ServerComponentUpdate)
         */
        public boolean canRenderProperty(RenderContext rc, ServerComponentUpdate update) {
            return true;
        }
    
        /**
         * @see nextapp.echo2.webcontainer.PartialUpdateParticipant#renderProperty(
         *      nextapp.echo2.webcontainer.RenderContext, nextapp.echo2.app.update.ServerComponentUpdate)
         */
        public void renderProperty(RenderContext rc, ServerComponentUpdate update) {
            TextComponent textComponent = (TextComponent) update.getParent();
            String elementId = ContainerInstance.getElementId(textComponent);
            ServerMessage serverMessage = rc.getServerMessage();
            Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
                    "EchoTextComponent.MessageProcessor", "set-text", new String[0], new String[0]);
            Element itemElement = serverMessage.getDocument().createElement("item");
            itemElement.setAttribute("eid", elementId);
            itemElement.setAttribute("text", textComponent.getText());
            itemizedUpdateElement.appendChild(itemElement);
            
        }
    }

    private PartialUpdateManager partialUpdateManager;

    /**
     * Default constructor.
     */
    public TextComponentPeer() {
        partialUpdateManager = new PartialUpdateManager();
        partialUpdateManager.add(TextComponent.PROPERTY_FOREGROUND,
                new ColorUpdate(TextComponent.PROPERTY_FOREGROUND, null, ColorUpdate.CSS_COLOR));
        partialUpdateManager.add(TextComponent.PROPERTY_BACKGROUND,
                new ColorUpdate(TextComponent.PROPERTY_BACKGROUND, null, ColorUpdate.CSS_BACKGROUND_COLOR));
        partialUpdateManager.add(TextComponent.PROPERTY_BORDER,
                new BorderUpdate(TextComponent.PROPERTY_BORDER, null, BorderUpdate.CSS_BORDER));
        partialUpdateManager.add(TextComponent.PROPERTY_INSETS,
                new InsetsUpdate(TextComponent.PROPERTY_INSETS, null, InsetsUpdate.CSS_PADDING));
        partialUpdateManager.add(TextComponent.TEXT_CHANGED_PROPERTY, new TextUpdate());
    }

    /**
     * Creates a base <code>CssStyle</code> for properties common to text
     * components.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the text component
     * @return the style
     */
    protected CssStyle createBaseCssStyle(RenderContext rc, TextComponent textComponent) {
        CssStyle cssStyle = new CssStyle();

        boolean renderEnabled = textComponent.isRenderEnabled();

        Border border;
        Color foreground, background;
        Font font;
        FillImage backgroundImage;
        if (!renderEnabled) {
            // Retrieve disabled style information.
            background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND);
            backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND_IMAGE);
            border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_BORDER);
            font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_FONT);
            foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_DISABLED_FOREGROUND);

            // Fallback to normal styles.
            if (background == null) {
                background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND);
                if (backgroundImage == null) {
                    // Special case:
                    // Disabled background without disabled background image will render disabled background instead of
                    // normal background image.
                    backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
                }
            }
            if (border == null) {
                border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_BORDER);
            }
            if (font == null) {
                font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_FONT);
            }
            if (foreground == null) {
                foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_FOREGROUND);
            }
        } else {
            border = (Border) textComponent.getRenderProperty(TextComponent.PROPERTY_BORDER);
            foreground = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_FOREGROUND);
            background = (Color) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND);
            font = (Font) textComponent.getRenderProperty(TextComponent.PROPERTY_FONT);
            backgroundImage = (FillImage) textComponent.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
        }
        
        Alignment alignment = (Alignment) textComponent.getRenderProperty(TextComponent.PROPERTY_ALIGNMENT);
        if (alignment != null) {
            int horizontalAlignment = AlignmentRender.getRenderedHorizontal(alignment, textComponent);
            switch (horizontalAlignment) {
            case Alignment.LEFT:
                cssStyle.setAttribute("text-align", "left");
                break;
            case Alignment.CENTER:
                cssStyle.setAttribute("text-align", "center");
                break;
            case Alignment.RIGHT:
                cssStyle.setAttribute("text-align", "right");
                break;
            }
        }
        
        LayoutDirectionRender.renderToStyle(cssStyle, textComponent.getLayoutDirection(), textComponent.getLocale());
        BorderRender.renderToStyle(cssStyle, border);
        ColorRender.renderToStyle(cssStyle, foreground, background);
        FontRender.renderToStyle(cssStyle, font);
        FillImageRender.renderToStyle(cssStyle, rc, this, textComponent, IMAGE_ID_BACKGROUND, backgroundImage, 
                FillImageRender.FLAG_DISABLE_FIXED_MODE);
        
        InsetsRender.renderToStyle(cssStyle, "padding", (Insets) textComponent.getRenderProperty(TextComponent.PROPERTY_INSETS));
        
        Extent width = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_WIDTH);
        Extent height = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_HEIGHT);

        if (width != null) {
            cssStyle.setAttribute("width", ExtentRender.renderCssAttributeValue(width));
        }

        if (height != null) {
            cssStyle.setAttribute("height", ExtentRender.renderCssAttributeValue(height));
        }
        return cssStyle;
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("Component does not support children.");
    }

    /**
     * @see nextapp.echo2.webcontainer.image.ImageRenderSupport#getImage(nextapp.echo2.app.Component,
     *      java.lang.String)
     */
    public ImageReference getImage(Component component, String imageId) {
        if (IMAGE_ID_BACKGROUND.equals(imageId)) {
            FillImage backgroundImage;
            if (component.isRenderEnabled()) {
                backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
            } else {
                backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_DISABLED_BACKGROUND_IMAGE);
                if (backgroundImage == null) {
                    backgroundImage = (FillImage) component.getRenderProperty(TextComponent.PROPERTY_BACKGROUND_IMAGE);
                }
            }
            if (backgroundImage == null) {
                return null;
            } else {
                return backgroundImage.getImage();
            }
        } else {
            return null;
        }
    }

    /**
     * @see nextapp.echo2.webcontainer.ActionProcessor#processAction(nextapp.echo2.webcontainer.ContainerInstance, 
     *      nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processAction(ContainerInstance ci, Component component, Element actionElement) {
        ci.getUpdateManager().getClientUpdateManager().setComponentAction(component, TextComponent.INPUT_ACTION, null);
    }

    /**
     * @see nextapp.echo2.webcontainer.PropertyUpdateProcessor#processPropertyUpdate(
     *      nextapp.echo2.webcontainer.ContainerInstance,
     *      nextapp.echo2.app.Component, org.w3c.dom.Element)
     */
    public void processPropertyUpdate(ContainerInstance ci, Component component, Element propertyElement) {
        String propertyName = propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_NAME);
        if (TextComponent.TEXT_CHANGED_PROPERTY.equals(propertyName)) {
            String propertyValue = DomUtil.getElementText(propertyElement);
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    TextComponent.TEXT_CHANGED_PROPERTY, propertyValue);
        } else if (TextComponent.PROPERTY_HORIZONTAL_SCROLL.equals(propertyName)) {
            Extent propertyValue = new Extent(Integer.parseInt(
                    fixWhenNotAnInteger(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE))));
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    TextComponent.PROPERTY_HORIZONTAL_SCROLL, propertyValue);
        } else if (TextComponent.PROPERTY_VERTICAL_SCROLL.equals(propertyName)) {
            Extent propertyValue = new Extent(Integer.parseInt(
                    fixWhenNotAnInteger(propertyElement.getAttribute(PropertyUpdateProcessor.PROPERTY_VALUE))));
            ci.getUpdateManager().getClientUpdateManager().setComponentProperty(component, 
                    TextComponent.PROPERTY_VERTICAL_SCROLL, propertyValue);
        }
    }

    /**
     * In rare cases we get an exception / stack trace like in the comment below this method. This is a workaround for
     * these situations.
     * 
     * @param number  the number to fix when not an integer
     * @return        an integer
     */
    private static String fixWhenNotAnInteger(String number) {
        if (number.contains(".")) {
            return new BigDecimal(number).setScale(0, RoundingMode.HALF_UP).toString();
        }
        return number;
    }
//	See below for a stack trace found in the log when the message "An application error has occurred. Your session has
//	been reset." was shown to the user in the GUI. At the time of this tack trace the workaround wasn't implemented yet,
//	hence in this version of TextComponentPeer.java, with the workaround implemented, line 293 has moved to line 296.
//	
//	08-Mar-2024 09:44:29.998 SEVERE [http-nio-8080-exec-2] org.apache.catalina.core.StandardWrapperValve.invoke Servlet.service() for servlet [TestTool] in context with path [] threw exception
//	      java.lang.NumberFormatException: For input string: "4.44444465637207"
//	            at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
//	            at java.lang.Integer.parseInt(Integer.java:580)
//	            at java.lang.Integer.parseInt(Integer.java:615)
//	            at nextapp.echo2.webcontainer.syncpeer.TextComponentPeer.processPropertyUpdate(TextComponentPeer.java:293)
//	            at nextapp.echo2.webcontainer.ContainerSynchronizeService$1.process(ContainerSynchronizeService.java:143)
//	            at nextapp.echo2.webrender.service.SynchronizeService.processClientMessage(SynchronizeService.java:217)
//	            at nextapp.echo2.webcontainer.ContainerSynchronizeService.renderUpdate(ContainerSynchronizeService.java:469)
//	            at nextapp.echo2.webrender.service.SynchronizeService.service(SynchronizeService.java:279)
//	            at nextapp.echo2.webrender.WebRenderServlet.process(WebRenderServlet.java:273)
//	            at nextapp.echo2.webrender.WebRenderServlet.doPost(WebRenderServlet.java:189)
//	            at javax.servlet.http.HttpServlet.service(HttpServlet.java:682)
//	            at javax.servlet.http.HttpServlet.service(HttpServlet.java:765)
//	            at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:231)
//	            at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
//	            at org.apache.tomcat.websocket.server.WsFilter.doFilter(WsFilter.java:52)
//	            at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
//	            at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:352)
//	            at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:126)
//	            at org.springframework.security.web.access.ExceptionTranslationFilter.doFilter(ExceptionTranslationFilter.java:120)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:131)
//	            at org.springframework.security.web.session.SessionManagementFilter.doFilter(SessionManagementFilter.java:85)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.authentication.AnonymousAuthenticationFilter.doFilter(AnonymousAuthenticationFilter.java:100)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter.doFilter(SecurityContextHolderAwareRequestFilter.java:164)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.savedrequest.RequestCacheAwareFilter.doFilter(RequestCacheAwareFilter.java:63)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.header.HeaderWriterFilter.doHeadersAfter(HeaderWriterFilter.java:90)
//	            at org.springframework.security.web.header.HeaderWriterFilter.doFilterInternal(HeaderWriterFilter.java:75)
//	            at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:117)
//	            at org.springframework.security.web.context.SecurityContextPersistenceFilter.doFilter(SecurityContextPersistenceFilter.java:87)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter.doFilterInternal(WebAsyncManagerIntegrationFilter.java:62)
//	            at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.session.DisableEncodeUrlFilter.doFilterInternal(DisableEncodeUrlFilter.java:42)
//	            at org.springframework.web.filter.OncePerRequestFilter.doFilter(OncePerRequestFilter.java:117)
//	            at org.springframework.security.web.FilterChainProxy$VirtualFilterChain.doFilter(FilterChainProxy.java:361)
//	            at org.springframework.security.web.FilterChainProxy.doFilterInternal(FilterChainProxy.java:225)
//	            at org.springframework.security.web.FilterChainProxy.doFilter(FilterChainProxy.java:190)
//	            at org.springframework.web.filter.DelegatingFilterProxy.invokeDelegate(DelegatingFilterProxy.java:354)
//	            at org.springframework.web.filter.DelegatingFilterProxy.doFilter(DelegatingFilterProxy.java:267)
//	            at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
//	            at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
//	            at org.apache.logging.log4j.web.Log4jServletFilter.doFilter(Log4jServletFilter.java:71)
//	            at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:193)
//	            at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:166)
//	            at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:177)
//	            at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:97)
//	            at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:662)
//	            at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:135)
//	            at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:92)
//	            at org.apache.catalina.valves.AbstractAccessLogValve.invoke(AbstractAccessLogValve.java:698)
//	            at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:78)
//	            at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:367)
//	            at org.apache.coyote.http11.Http11Processor.service(Http11Processor.java:639)
//	            at org.apache.coyote.AbstractProcessorLight.process(AbstractProcessorLight.java:65)
//	            at org.apache.coyote.AbstractProtocol$ConnectionHandler.process(AbstractProtocol.java:885)
//	            at org.apache.tomcat.util.net.NioEndpoint$SocketProcessor.doRun(NioEndpoint.java:1688)
//	            at org.apache.tomcat.util.net.SocketProcessorBase.run(SocketProcessorBase.java:49)
//	            at org.apache.tomcat.util.threads.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1191)
//	            at org.apache.tomcat.util.threads.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:659)
//	            at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61)
//	            at java.lang.Thread.run(Thread.java:750)

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderAdd(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String,
     *      nextapp.echo2.app.Component)
     */
    public void renderAdd(RenderContext rc, ServerComponentUpdate update, String targetId, Component component) {
        Element domAddElement = DomUpdate.renderElementAdd(rc.getServerMessage());
        DocumentFragment htmlFragment = rc.getServerMessage().getDocument().createDocumentFragment();
        renderHtml(rc, update, htmlFragment, component);
        DomUpdate.renderElementAddContent(rc.getServerMessage(), domAddElement, targetId, htmlFragment);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate,
     *      nextapp.echo2.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        rc.getServerMessage().addLibrary(TEXT_COMPONENT_SERVICE.getId());
        renderDisposeDirective(rc, (TextComponent) component);
    }

    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to
     * dispose the state of a text component, performing tasks such as
     * registering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the <code>TextComponent<code>
     */
    public void renderDisposeDirective(RenderContext rc, TextComponent textComponent) {
        String elementId = ContainerInstance.getElementId(textComponent);
        ServerMessage serverMessage = rc.getServerMessage();
        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_PREREMOVE,
                "EchoTextComponent.MessageProcessor", "dispose", new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        itemizedUpdateElement.appendChild(itemElement);
    }

    /**
     * Renders a directive to the outgoing <code>ServerMessage</code> to
     * initialize the state of a text component, performing tasks such as
     * registering event listeners on the client.
     * 
     * @param rc the relevant <code>RenderContext</code>
     * @param textComponent the <code>TextComponent<code>
     */
    public void renderInitDirective(RenderContext rc, TextComponent textComponent) {
        Extent horizontalScroll = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_HORIZONTAL_SCROLL);
        Extent verticalScroll = (Extent) textComponent.getRenderProperty(TextComponent.PROPERTY_VERTICAL_SCROLL);
        String elementId = ContainerInstance.getElementId(textComponent);
        ServerMessage serverMessage = rc.getServerMessage();

        Element itemizedUpdateElement = serverMessage.getItemizedDirective(ServerMessage.GROUP_ID_POSTUPDATE,
                "EchoTextComponent.MessageProcessor", "init", new String[0], new String[0]);
        Element itemElement = serverMessage.getDocument().createElement("item");
        itemElement.setAttribute("eid", elementId);
        if (horizontalScroll != null && horizontalScroll.getValue() != 0) {
            itemElement.setAttribute("horizontal-scroll", ExtentRender.renderCssAttributePixelValue(horizontalScroll, "0"));
        }
        if (verticalScroll != null && verticalScroll.getValue() != 0) {
            itemElement.setAttribute("vertical-scroll", ExtentRender.renderCssAttributePixelValue(verticalScroll, "0"));
        }
        if (textComponent instanceof TextArea) {
            Integer maximumLength = (Integer) textComponent.getProperty(TextComponent.PROPERTY_MAXIMUM_LENGTH);
            if (maximumLength != null) {
                itemElement.setAttribute("maximum-length", maximumLength.toString());
            }
        }
        if (textComponent instanceof TextArea && rc.getContainerInstance().getClientProperties().getBoolean(
                ClientProperties.QUIRK_TEXTAREA_CONTENT)) {
            String value = textComponent.getText();
            if (value != null) {
                itemElement.setAttribute("text", value);
            }
        }
        if (!textComponent.isRenderEnabled()) {
            itemElement.setAttribute("enabled", "false");
        }
        if (textComponent.hasActionListeners()) {
            itemElement.setAttribute("server-notify", "true");
        }

        itemizedUpdateElement.appendChild(itemElement);
    }

    /**
     * @see nextapp.echo2.webcontainer.FocusSupport#renderSetFocus(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.Component)
     */
    public void renderSetFocus(RenderContext rc, Component component) {
        WindowUpdate.renderSetFocus(rc.getServerMessage(), ContainerInstance.getElementId(component));
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        boolean fullReplace = false;
        if (update.hasUpdatedProperties()) {
            if (!partialUpdateManager.canProcess(rc, update)) {
                fullReplace = true;
            }
        }

        if (fullReplace) {
            // Perform full update.
            DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
            renderAdd(rc, update, targetId, update.getParent());
        } else {
            partialUpdateManager.process(rc, update);
        }

        return false;
    }
}