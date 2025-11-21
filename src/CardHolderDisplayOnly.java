import RenderingStuff.Mesh;
import org.joml.Math;

import java.util.List;

public class CardHolderDisplayOnly extends CardHolder<Card, Card<Card>>{
    public CardHolderDisplayOnly(Player owner, int visibilityLayers) {
        super(owner,visibilityLayers);
    }
    public CardHolderDisplayOnly(Player owner) {
        super(owner);
    }
    public CardHolder Data;

    public void select() {
        super.select();
        Data.setInd(ind);
        Data.select();
    }
    public void addCopyOfAll(CardHolder other) {
        addCopyOfAll(other.Cards);
    }
    public void addCopyOfAll(List<Card> other){
        for (Card card : other){
            super.add(new Card<>(card, card.file));
        }
    }
    public void update(CardHolder Data){
        this.Data = Data;
        update();
    }
    public void update(){
        super.clear();
        addCopyOfAll(Data);
    }
}
