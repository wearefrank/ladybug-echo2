Latest binary distro's of Echo2 aren't compiled for Java 1.4 anymore:

http://echo.nextapp.com/site/node/6200

Following (nightly build) files downloaded on 20100210:

http://download.nextapp.com/downloads/echo2go/NextApp_Echo2.zip
http://download.nextapp.com/downloads/echo2go/NextApp_Echo2_Extras.zip
http://download.nextapp.com/downloads/echo2go/NextApp_Echo2_FileTransfer.zip
http://sourceforge.net/projects/echopoint/files/echopointng/2.1.0rc5/echopointng-src-2.1.0rc5.zip/download

Source files extracted using the build.xml file

Following lines added to nextapp.echo2.webcontainer.filetransfer.DownloadService
(see http://echo.nextapp.com/site/node/4092 also):

        // Workaround for not being able to download in IE over https. Problem
        // reproduced with IE 6 and IE 8. Fix tested with IE 8.
        response.setHeader("Cache-Control", "");
        response.setHeader("Pragma", "");

Updated nextapp.echo2.webrender.util.DomUtil to select the right
javax.xml.transform.TransformerFactory.

Updated nextapp.echo2.webrender.UserInstance (which is stored in user's http
session, see nextapp.echo2.webrender.Connection) to not be Serializable to
prevent java.io.NotSerializableException when the application wants to persist
sessions. In general this might happen on restart in case it's configured to
save session data. WebSphere will also serialize session data when
SessionObjectSize is enabled in PMI (Performance Monitoring Infrastructure).

Adjusted nextapp.echo2.webrender.service.JavaScriptService and 
echopointng.ui.util.JavaScriptSnippetService to use text/javascript instead of
text/plain.

Added fixWhenNotAnInteger() to TextComponentPeer.java to prevent "An application error has occurred. Your session has
been reset."