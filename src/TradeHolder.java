import java.util.List;

public class TradeHolder<E> extends CardHolder<E>{
    public TradeHolder(Player owner, int visibilityLayers) {
        super(owner,visibilityLayers);
    }
    public TradeHolder(Player owner) {
        super(owner);
    }
    public CardHolder<E> Data;

    public void select() {
        super.select();
        Data.setInd(ind);
        Data.select();
    }
    public void addCopyOfAll(CardHolder<E> other) {
        addCopyOfAll(other.Cards);
    }
    public void addCopyOfAll(List<Card<E>> other){
        for (Card<E> card : other){
            add(new Card<E>(card.data, card.file));
        }
    }
}
