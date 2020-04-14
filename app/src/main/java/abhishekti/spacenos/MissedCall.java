package abhishekti.spacenos;

public class MissedCall {

    private String phnum;
    private String time;

    public MissedCall(String phnum, String time) {
        this.phnum = phnum;
        this.time = time;
    }

    public String getPhnum() {
        return phnum;
    }

    public void setPhnum(String phnum) {
        this.phnum = phnum;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
