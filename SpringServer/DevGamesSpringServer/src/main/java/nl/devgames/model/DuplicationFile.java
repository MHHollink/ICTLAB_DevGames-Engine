package nl.devgames.model;

public class DuplicationFile extends Model {

    String file;
    int beginLine;
    int endLine;
    int size;

    public DuplicationFile() {
    }

    public DuplicationFile(String file, int beginLine, int endLine, int size) {
        this.file = file;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.size = size;
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
