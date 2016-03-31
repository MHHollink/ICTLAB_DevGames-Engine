package nl.devgames.model;

public class Duplication {

    String fileNameA;
    String fileNameB;

    int beginLineNumberA;
    int endLineNumberA;

    int beginLineNumberB;
    int endLineNumberB;

    public Duplication() {
    }

    public String getFileNameA() {
        return fileNameA;
    }

    public void setFileNameA(String fileNameA) {
        this.fileNameA = fileNameA;
    }

    public String getFileNameB() {
        return fileNameB;
    }

    public void setFileNameB(String fileNameB) {
        this.fileNameB = fileNameB;
    }

    public int getBeginLineNumberA() {
        return beginLineNumberA;
    }

    public void setBeginLineNumberA(int beginLineNumberA) {
        this.beginLineNumberA = beginLineNumberA;
    }

    public int getEndLineNumberA() {
        return endLineNumberA;
    }

    public void setEndLineNumberA(int endLineNumberA) {
        this.endLineNumberA = endLineNumberA;
    }

    public int getBeginLineNumberB() {
        return beginLineNumberB;
    }

    public void setBeginLineNumberB(int beginLineNumberB) {
        this.beginLineNumberB = beginLineNumberB;
    }

    public int getEndLineNumberB() {
        return endLineNumberB;
    }

    public void setEndLineNumberB(int endLineNumberB) {
        this.endLineNumberB = endLineNumberB;
    }

    @Override
    public String toString() {
        return "Duplication{" +
                "fileNameA='" + fileNameA + '\'' +
                ", fileNameB='" + fileNameB + '\'' +
                ", beginLineNumberA=" + beginLineNumberA +
                ", endLineNumberA=" + endLineNumberA +
                ", beginLineNumberB=" + beginLineNumberB +
                ", endLineNumberB=" + endLineNumberB +
                '}';
    }
}
