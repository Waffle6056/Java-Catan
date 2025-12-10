package CardStructure;

import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.Renderable2d;
import org.joml.*;

import java.lang.Math;
import java.util.*;

public class CardHolder<E, C extends Card<E>> implements Renderable2d {
    public List<C> Cards = new ArrayList<>();
    int ind = 0;
    public List<C> CardsSelected = new ArrayList<>();
    public Vector3f position = new Vector3f(0, 0, 0);
    public Vector3f scale = new Vector3f(2, 2, 2);

    public float rotation = 0;
    public float len = .2f;
    public float dis = .2f;
    HashMap<E, Integer> Counts = new HashMap<>();
    public CardPositioner cardPositioner = CardPositioner::setCircularTransforms;
    public int VisibilityLayers = 0;
    public enum VisibilityLayer{
        Building(1, "CatanCardMeshes/UICategories/BuildingTab.fbx"),
        Trading(2, "CatanCardMeshes/UICategories/TradingTab.fbx"),
        DevelopmentCard(4, "CatanCardMeshes/UICategories/DevelopmentTab.fbx");
        public int bit;
        public final String TabMeshFile;
        VisibilityLayer(int bit, String TabMeshFile){
            this.bit = bit;
            this.TabMeshFile = TabMeshFile;
        }
    }
    public CardHolder(int visibilityLayers) {
        this.VisibilityLayers = visibilityLayers;
    }

    public CardHolder(){}
    public List<C> Cards(){
        return Cards;
    }
    public List<E> CardData(){
        ArrayList<E> list = new ArrayList<>();
        for (C c : Cards())
            list.add(c.data);
        return list;
    }
    public C add(int i, C card) {
        Cards.add(i,card);
        Counts.put(card.data, Counts.getOrDefault(card.data, 0) + 1);
        return card;
    }


    public C add(C card) {
        return add(Cards.size(),card);
    }

    public C get(int i) {
        return Cards.get(i);
    }

    public void addAll(CardHolder<E,C> other) {
        for (C c : other.Cards)
            add(c);
    }

    public void addAll(List<C> other) {
        for (C c : other)
            add(c);
    }

    public int count(E Data) {
        return Counts.getOrDefault(Data, 0);
    }

    public int indexOf(E Data) {
        for (int i = 0; i < Cards.size(); i++)
            if (Cards.get(i).data.equals(Data))
                return i;
        return -1;
    }

    public void removeFirst(E data) {
        remove(Cards.get(indexOf(data)));
    }

    public void remove(C card) {
        if (!Cards.contains(card))
            return;
        Cards.remove(card);
        CardsSelected.remove(card);
        Counts.put(card.data, Counts.getOrDefault(card.data, 0) - 1);
        card.selected = false;
        setInd(ind);
    }

    public void removeAll(List<C> cards) {
        for (int i = cards.size() - 1; i >= 0; i--)
            remove(cards.get(i));
    }

    public void trade(CardHolder<E,C> other) {
        addAll(other.CardsSelected);
        other.addAll(CardsSelected);

        removeAll(CardsSelected);
        other.removeAll(other.CardsSelected);
    }

    public void clear() {
        while (!Cards.isEmpty())
            remove(get(Cards.size()-1));
    }

    public void setInd(int i) {
        ind = i;
        if (Cards.size() > 0)
            ind = Math.floorMod(ind, Cards.size());
    }

    public void scroll(int delta) {
        if (Cards.size() == 0)
            return;
        setInd(ind + delta);
    }

    public C current() {
        if (Cards.size() == 0 || ind < 0 || ind >= Cards.size())
            return null;
        return Cards.get(ind);
    }

    public void select(boolean val) {
        if (!Cards.isEmpty() && (val && !current().selected || !val && current().selected))
            select();
    }

    public void select() {
        if (Cards.size() == 0)
            return;
        C Current = Cards.get(ind);
        Current.selected = !Current.selected;
        if (Current.selected) {
            CardsSelected.add(Current);
        } else {
            CardsSelected.remove(Current);
        }
    }

    public void setTransforms(){
        List<Card> test = new ArrayList<>();
        test.addAll(Cards);
        cardPositioner.SetTransforms(test, position, rotation, scale, ind, len, dis);
    }
    public void deselectAll(){
        for (int i = 0; i < Cards.size(); i++) {
            scroll(1);
            select(false);
        }
    }
    public boolean selectAllEqual(E val){
        for (int i = 0; i < Cards.size(); i++) {
            if (current().data.equals(val)){
                select();
                return true;
            }
            scroll(1);
        }
        return false;
    }
    @Override
    public List<Mesh> toMesh2d() {
        ArrayList<Mesh> out = new ArrayList<>();
        setTransforms();
        for (C c : Cards)
            out.addAll(c.toMesh2d());

        return out;
    }
}
