package nl.devgames.model;

public class DuplicationFile {

    String file;
    int beginLine;
    int endLine;
    int size;

    public DuplicationFile() {
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getBeginLine() {
        return beginLine;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    @Override
    public String toString() {
        return "DuplicationFile{" +
                "file='" + file + '\'' +
                ", beginLine=" + beginLine +
                ", endLine=" + endLine +
                ", size=" + size +
                '}';
    }
}
