package com.wsc.qa.abc;
public class JdException extends Exception
{
    private static final long serialVersionUID = -7035498848577048685L;
    private String errCode;
    private String errMsg;

    public JdException() {
    }

    public JdException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JdException(final String message) {
        super(message);
    }

    public JdException(final Throwable cause) {
        super(cause);
    }

    public JdException(final String errCode, final String errMsg) {
        super(errCode + ": " + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}