package CardStructure;

import CardStructure.CardHolder;

import java.util.List;

public class CardHolderDisplayOnly extends CardHolder<Card, Card<Card>> {
    public CardHolderDisplayOnly(int visibilityLayers) {
        super(visibilityLayers);
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
            Card dupe = new Card<>(card, card.file);
            dupe.selected = card.selected;
            super.add(dupe);

        }
    }
    public void update(CardHolder Data){
        this.Data = Data;
        update();
    }
    public void update(){
        super.clear();
        if (Data != null)
            addCopyOfAll(Data);
    }
}
