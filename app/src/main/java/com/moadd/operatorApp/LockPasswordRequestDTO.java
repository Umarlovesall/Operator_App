package com.moadd.operatorApp;

import java.util.Date;

/**
 * Created by moadd on 22-Jan-18.
 */

public class LockPasswordRequestDTO {
    private Long lockPasswordRequestId;
    private Long createdBy;
    private Long updatedBy;
    private Long operatorId;
    private Long locItemTransactionId;
    private Long lockId;
    private String lockStatus;
    private String password;
    private String lockSno;
    private Date createdOn;
    private Date updatedOn;

    public Long getLocItemTransactionId() {
        return locItemTransactionId;
    }
    public void setLocItemTransactionId(Long locItemTransactionId) {
        this.locItemTransactionId = locItemTransactionId;
    }

    public String getLockStatus() {
        return lockStatus;
    }
    public void setLockStatus(String lockStatus) {
        this.lockStatus = lockStatus;
    }
    private String status;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getLockSno() {
        return lockSno;
    }
    public void setLockSno(String lockSno) {
        this.lockSno = lockSno;
    }

    public Long getLockPasswordRequestId() {
        return lockPasswordRequestId;
    }
    public void setLockPasswordRequestId(Long lockPasswordRequestId) {
        this.lockPasswordRequestId = lockPasswordRequestId;
    }
    public Long getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    public Long getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getOperatorId() {
        return operatorId;
    }
    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
    public Long getLockId() {
        return lockId;
    }
    public void setLockId(Long lockId) {
        this.lockId = lockId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Date getCreatedOn() {
        return createdOn;
    }
    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    public Date getUpdatedOn() {
        return updatedOn;
    }
    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }




}
