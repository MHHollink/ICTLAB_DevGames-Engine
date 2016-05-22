package nl.devgames.model;

public class DuplicationFile extends Model {

    String file;
    Integer beginLine;
    Integer endLine;
    Integer size;

    public DuplicationFile() {
    }

    public DuplicationFile(String file, Integer beginLine, Integer endLine, Integer size) {
        this.file = file;
        this.beginLine = beginLine;
        this.endLine = endLine;
        this.size = size;
    }

    public String getFile() {
        return file;
    }

    public Integer getBeginLine() {
        return beginLine;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public Integer getSize() {
        return size;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
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
