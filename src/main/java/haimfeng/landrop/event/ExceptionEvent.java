package haimfeng.landrop.event;

public class ExceptionEvent extends Event {
    public String exceptionSubmitter; // 异常提交者
    public String exceptionMessage; // 异常信息
    public Throwable exception; // 异常对象

    /**
     * 构造函数
     * @param exceptionSubmitter 异常提交者
     * @param exceptionMessage 异常信息
     * @param exception 异常对象
     */
    public ExceptionEvent(String exceptionSubmitter, String exceptionMessage, Throwable exception)
    {
        this.exceptionSubmitter = exceptionSubmitter;
        this.exceptionMessage = exceptionMessage;
        this.exception = exception;
    }
}
