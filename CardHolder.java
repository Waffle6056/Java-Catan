import RenderingStuff.Mesh;

import java.util.*;
public class CardHolder<E> {
    List<Card<E>> Cards = new ArrayList<>();
    int ind = 0;
    List<Card<E>> CardsSelected = new ArrayList<>();
    boolean visible = false;
    Mesh mesh;
    public Card<E> add(Card<E> card){
        Cards.add(card);
        return card;
    }
    public Card<E> add(E data){
        return add(new Card<>(data));
    }
    public void trade(CardHolder<E> other){
        Cards.removeAll(CardsSelected);
        other.Cards.removeAll(other.CardsSelected);

        Cards.addAll(other.CardsSelected);
        other.Cards.addAll(CardsSelected);

        List<Card<E>> swap = CardsSelected;
        CardsSelected = other.CardsSelected;
        other.CardsSelected = swap;
    }
    public void clear(){
        Cards.clear();
        CardsSelected.clear();
    }
    public void scroll(int delta){
        ind += delta;
        ind = Math.floorMod(ind, Cards.size());
    }
    public Card<E> current(){
        return Cards.get(ind);
    }
    public void select(){
        Card<E> Current = Cards.get(ind);
        Current.selected = !Current.selected;
        if (Current.selected)
            CardsSelected.add(Current);
        else
            CardsSelected.remove(Current);
    }

    public void toggleVisible(){

    }
    public void toggleVisible(boolean val){
        if (val && !visible || !val && visible)
            toggleVisible();
    }
}
