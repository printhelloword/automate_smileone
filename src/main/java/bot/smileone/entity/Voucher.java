package bot.smileone.entity;

public class Voucher {

    private Player player;
    private String denomUri;
    private String type;

    public String getType() {
        return type;
    }

    public Voucher() {

    }

    public Voucher(Player player, String denom) {
        this.player = player;
        this.denomUri = denom;
    }

    public static Voucher byPlayerAndDenom(Player player, String denom){
        return new Voucher(player, denom);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getDenomLocator() {
        return denomUri;
    }




}
