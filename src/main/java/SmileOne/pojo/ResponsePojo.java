package SmileOne.pojo;

public class ResponsePojo {
    private VoucherPojo voucher;
    private String message;
    private String trxId;
    private String status;

    public VoucherPojo getVoucher() {
        return voucher;
    }

    public ResponsePojo() {
    }

    public void setVoucher(VoucherPojo voucher) {
        this.voucher = voucher;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrxId() {
        return trxId;
    }

    public void setTrxId(String trxId) {
        this.trxId = trxId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResponsePojo(VoucherPojo voucher, String message, String trxId, String status) {
        this.voucher = voucher;
        this.message = message;
        this.trxId = trxId;
        this.status = status;
    }
}
