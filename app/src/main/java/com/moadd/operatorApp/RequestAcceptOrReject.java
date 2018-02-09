package com.moadd.operatorApp;

/**
 * Created by moadd on 22-Jan-18.
 */

public class RequestAcceptOrReject {
    private String status;
    private Long lockPasswordRequestId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getLockPasswordRequestId() {
        return lockPasswordRequestId;
    }

    public void setLockPasswordRequestId(Long lockPasswordRequestId) {
        this.lockPasswordRequestId = lockPasswordRequestId;
    }
}
