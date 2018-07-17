package nextapp.echo2.webcontainer.filetransfer;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.filetransfer.DownloadProvider;
import nextapp.echo2.app.filetransfer.FilePane;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ComponentSynchronizePeer;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.DomUpdateSupport;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webrender.ServerMessage;
import nextapp.echo2.webrender.servermessage.DomUpdate;

public class FilePanePeer 
implements ComponentSynchronizePeer, DomUpdateSupport {
    
    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#getContainerId(Component)
     */
    public String getContainerId(Component child) {
        throw new UnsupportedOperationException("Component does not support children.");
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
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderDispose(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate,
     *      nextapp.echo2.app.Component)
     */
    public void renderDispose(RenderContext rc, ServerComponentUpdate update, Component component) {
        // Do nothing.
    }

    public void renderHtml(RenderContext rc, ServerComponentUpdate update, Node parentNode, Component component) {
        // Note: Embedding the file pane in a DIV is necessary for proper rendeirng on Internet Explorer 7.
        // Test all modifications with IE7, it's very finnicky about height/width attributes.
        
        FilePane filePane = (FilePane) component;
        String elementId = ContainerInstance.getElementId(filePane);
        ServerMessage serverMessage = rc.getServerMessage();
        Document document = serverMessage.getDocument();
        Element divElement = document.createElement("div");
        divElement.setAttribute("id", elementId);
        
        divElement.setAttribute("style", "background:red;position:absolute;width:100%;height:100%;overflow:hidden;");
        
        Element filePaneIFrameElement = document.createElement("iframe");
        filePaneIFrameElement.setAttribute("style", "position:absolute;overflow:hidden;");
        filePaneIFrameElement.setAttribute("width", "100%");
        filePaneIFrameElement.setAttribute("height", "100%");
        DownloadProvider provider = filePane.getProvider();
        if (provider == null) {
            filePaneIFrameElement.setAttribute("src", "about:blank");
        } else {
            filePaneIFrameElement.setAttribute("src", FilePaneService.INSTANCE.createUri(rc.getContainerInstance(),
                    ContainerInstance.getElementId(filePane)));
        }

        divElement.appendChild(filePaneIFrameElement);
        
        parentNode.appendChild(divElement);
    }

    /**
     * @see nextapp.echo2.webcontainer.ComponentSynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext,
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        DomUpdate.renderElementRemove(rc.getServerMessage(), ContainerInstance.getElementId(update.getParent()));
        renderAdd(rc, update, targetId, update.getParent());
        return true;
    }
}
