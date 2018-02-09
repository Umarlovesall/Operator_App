package com.moadd.operatorApp;

/**
 * Created by moadd on 19-Jan-18.
 */

public class ReadLockDetails {
    private String barcodeEncoded,barcode,Sno,lockId,secretNo;

    public String getSecretNo() {
        return secretNo;
    }

    public void setSecretNo(String secretNo) {
        this.secretNo = secretNo;
    }

    public String getBarcodeEncoded() {
        return barcodeEncoded;
    }

    public void setBarcodeEncoded(String barcodeEncoded) {
        this.barcodeEncoded = barcodeEncoded;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSno() {
        return Sno;
    }

    public void setSno(String sno) {
        Sno = sno;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }
}
