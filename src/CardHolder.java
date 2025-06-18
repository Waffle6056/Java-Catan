import RenderingStuff.Mesh;
import org.joml.*;

import javax.swing.text.Position;
import java.lang.Math;
import java.util.*;
public class CardHolder<E> implements Renderable2d {
    List<Card<E>> Cards = new ArrayList<>();
    int ind = 0;
    List<Card<E>> CardsSelected = new ArrayList<>();
    boolean visible = false;
    Player owner;
    public Vector3f position = new Vector3f(0,0,0);
    public float rotation = 0;
    public float len = 0.05f;
    HashMap<E, Integer> Counts = new HashMap<>();
    public CardHolder(Player owner){
        this.owner = owner;
    }
    public Card<E> add(Card<E> card){
        Cards.add(card);
        Counts.put(card.data, Counts.getOrDefault(card.data,0)+1);
        return card;
    }

    public Card<E> add(E data){
        return add(new Card<>(data));
    }
    public Card<E> add(E data, String meshFile){
        return add(new Card<>(data, meshFile));
    }
    public Card<E> get(int i){
        return Cards.get(i);
    }
    public void addAll(CardHolder<E> other){
        for (Card<E> c : other.Cards)
            add(c);
    }
    public void addAll(List<Card<E>> other) {
        for (Card<E> c : other)
            add(c);
    }
    public int count(E Data){
        return Counts.getOrDefault(Data,0);
    }
    public int indexOf(E Data){
        for (int i = 0; i < Cards.size(); i++)
            if (Cards.get(i).data.equals(Data))
                return i;
        return -1;
    }
    public void remove(E data) {
        remove(Cards.get(indexOf(data)));
    }
    public void remove(Card<E> card){
        if (!Cards.contains(card))
            return;
        Cards.remove(card);
        Counts.put(card.data, Counts.getOrDefault(card.data,0)-1);
        card.selected = false;
        CardsSelected.remove(card);
    }
    public void removeAll(List<Card<E>> cards){
        for (int i = cards.size()-1; i >= 0; i--)
            remove(cards.get(i));
    }

    public void trade(CardHolder<E> other){
        addAll(other.CardsSelected);
        other.addAll(CardsSelected);

        removeAll(CardsSelected);
        other.removeAll(other.CardsSelected);
    }
    public void clear(){
        Cards.clear();
        CardsSelected.clear();
    }
    public void setInd(int i){
        ind = i;
    }
    public void scroll(int delta){
        if (Cards.size() == 0)
            return;
        ind += delta;
        ind = Math.floorMod(ind, Cards.size());
    }
    public Card<E> current(){
        if (Cards.size() == 0 || ind < 0 || ind >= Cards.size())
            return null;
        return Cards.get(ind);
    }
    public void select(boolean val){
        if (!Cards.isEmpty() && (val && !current().selected || !val && current().selected))
            select();
    }
    public void select(){
        if (Cards.size() == 0)
            return;
        Card<E> Current = Cards.get(ind);
        Current.selected = !Current.selected;
        if (Current.selected) {
            CardsSelected.add(Current);
        }
        else {
            CardsSelected.remove(Current);
        }
    }

    public void toggleVisible(){
        visible = !visible;
    }
    public void toggleVisible(boolean val){
        if (val && !visible || !val && visible)
            toggleVisible();
    }
    public void setPositions(){
        //System.out.println(current().data+" "+ind);
        ind = Math.max(0, Math.min(Cards.size()-1, ind));
        for (int i = 0; i < Cards.size(); i++){
            if (Cards.get(i).mesh != null) {
                Vector3f c = new Vector3f(position);

                int midDiff = i-ind;
                float angle = (float)Math.toRadians(Math.min(180f/Cards.size(), 180f/7f)) * midDiff;
                float len = 0.1f;
                if (midDiff == 0)
                    len += this.len;
                Vector3f rotated = new Vector3f(0,len,0).rotateZ(angle + rotation);
                c.add(rotated);

                //System.out.println(c);

                Cards.get(i).mesh.position = c;
                Cards.get(i).mesh.rotation = new Quaternionf();//.lookAlong(c,new Vector3f(0,1,0));
                Cards.get(i).mesh.rotation.rotateLocalY((float)Math.toRadians(180)).rotateLocalX((float)Math.toRadians(90)).rotateLocalZ(angle + rotation);//.lookAlong(c,position);

                if (Cards.get(i).HighLight != null){
                    Cards.get(i).HighLight.position = c.add(0,0,0.01f, new Vector3f());
                    //System.out.println("HAS HIGHLIGHT "+Cards.get(i).HighLight.position);
                    Cards.get(i).HighLight.rotation = Cards.get(i).mesh.rotation;
                }
            }
        }
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
        if (!visible)
            return out;
        setPositions();
        for (Card<E> c : Cards)
            out.addAll(c.toMesh());
        return out;
    }
}
