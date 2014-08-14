package toby.han.todo.model;

public class TodoData {
    // 保存用户上一次创建的待办
    public static TodoData NEW_TODO = null;
    
    // 待办id
    public long id;
    // 内容
    public String body;
    //创建时间
    public long creTime;
    // 提醒时间
    public long notifyTime;
}
