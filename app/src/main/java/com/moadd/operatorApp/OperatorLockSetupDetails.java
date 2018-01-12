package com.moadd.operatorApp;

/**
 * Created by moadd on 11-Jan-18.
 */

public class OperatorLockSetupDetails {
    public String getLockSno() {
        return lockSno;
    }

    public void setLockSno(String lockSno) {
        this.lockSno = lockSno;
    }

    public String getTimeAndLimit() {
        return timeAndLimit;
    }

    public void setTimeAndLimit(String timeAndLimit) {
        this.timeAndLimit = timeAndLimit;
    }

    public String getUserIDandAppId() {
        return userIDandAppId;
    }

    public void setUserIDandAppId(String userIDandAppId) {
        this.userIDandAppId = userIDandAppId;
    }

    public String getOpSetup() {
        return opSetup;
    }

    public void setOpSetup(String opSetup) {
        this.opSetup = opSetup;
    }

    public String getSupSetup() {
        return supSetup;
    }

    public void setSupSetup(String supSetup) {
        this.supSetup = supSetup;
    }

    public String getConnectedSuppIds() {
        return connectedSuppIds;
    }

    public void setConnectedSuppIds(String connectedSuppIds) {
        this.connectedSuppIds = connectedSuppIds;
    }

    private String lockSno,timeAndLimit,userIDandAppId,opSetup,supSetup,connectedSuppIds;
}
