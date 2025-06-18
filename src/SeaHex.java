public class SeaHex extends Hex {
    resource porttype=resource.Sea;
    public SeaHex(int q, int r, int s,resource porttype) {
        super(q, r, s, String.valueOf(resource.Sea));
        this.porttype=porttype;
    }
}