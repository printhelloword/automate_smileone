package SmileOne.pojo;

public class VoucherPojo {
    private Long playerId;
    private String denom;

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public String getDenom() {
        return denom;
    }

    public void setDenom(String denom) {
        this.denom = denom;
    }

    public VoucherPojo(Long playerId, String denom) {
        this.playerId = playerId;
        this.denom = denom;
    }
}
