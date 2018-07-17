
package nextapp.echo2.webcontainer.filetransfer;

import java.io.File;


class UploadEvent {

    private File file;
    private int fileSize;
    private String contentType;
    private String fileName;
    
    public UploadEvent(File file, int fileSize, String contentType, String fileName){
        setFile(file);
        setFileSize(fileSize);
        setContentType(contentType);
        setFileName(fileName);
    }
    
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public int getFileSize() {
        return fileSize;
    }
    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }
}
