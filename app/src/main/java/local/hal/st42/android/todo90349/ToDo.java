package local.hal.st42.android.todo90349;

import java.sql.Timestamp;

public class ToDo {
    /**
     * 主キーのID値。
     */
    private long _id;
    /**
     * タスク名。
     */
    private String _name;
    /**
     * 期限を表すエポックからのミリセカンド。
     */
    private Long _deadline;
    /**
     * タスクが完了(1)か未完了(0)かのフラグ。
     */
    private Integer _done;
    /**
     * タスクの詳細。
     */
    private String _note;

    //以下アクセサメソッド。
    public Long getId(){
        return _id;
    }
    public void setId(long id) {
        _id = id;
    }
    public String getName() {
        return _name;
    }
    public void setName(String name) {
        _name = name;
    }
    public Long getDeadLine(){
        return _deadline;
    };
    public void setDeadLine(Long deadline){
        _deadline = deadline;
    }
    public Integer getDone(){
        return _done;
    }
    public void setDone(Integer done) {
        _done = done;
    }
    public String getNote(){
        return _note;
    }
    public void setNote(String note) {
        _note = note;
    }
}
