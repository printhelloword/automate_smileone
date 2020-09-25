package SmileOne.pojo;

public class VoucherPojo {
    private String playerId;
    private String denom;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getDenom() {
        return denom;
    }

    public void setDenom(String denom) {
        this.denom = denom;
    }

    public VoucherPojo(String playerId, String denom) {
        this.playerId = playerId;
        this.denom = denom;
    }
}
