import RenderingStuff.Mesh;
import org.joml.*;

import java.lang.Math;
import java.util.*;
public class CardHolder<E, C extends Card<E>> implements Renderable2d {
    List<C> Cards = new ArrayList<>();
    int ind = 0;
    List<C> CardsSelected = new ArrayList<>();
    Player owner;
    public Vector3f position = new Vector3f(0, 0, 0);
    public Vector3f scale = new Vector3f(2, 2, 2);

    public float rotation = 0;
    public float len = .2f;
    public float dis = .2f;
    HashMap<E, Integer> Counts = new HashMap<>();
    public CardPositioner cardPositioner = CardHolder::setCircularTransforms;
    int VisibilityLayers = 0;
    enum VisibilityLayer{
        Building(1, "CatanCardMeshes/UICategories/BuildingTab.fbx"),
        Trading(2, "CatanCardMeshes/UICategories/TradingTab.fbx"),
        DevelopmentCard(4, "CatanCardMeshes/UICategories/DevelopmentTab.fbx");
        int bit;
        public final String TabMeshFile;
        VisibilityLayer(int bit, String TabMeshFile){
            this.bit = bit;
            this.TabMeshFile = TabMeshFile;
        }
    }
    public CardHolder(Player owner, int visibilityLayers) {
        this.owner = owner;
        this.VisibilityLayers = visibilityLayers;
    }

    public CardHolder(Player owner) {
        this.owner = owner;
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

    public C add(C card) {
        Cards.add(card);
        Counts.put(card.data, Counts.getOrDefault(card.data, 0) + 1);
        return card;
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
    @FunctionalInterface
    interface CardPositioner {
        public void SetTransforms(List<Card> Cards, Vector3f position, float rotation, Vector3f scale, int ind, float len, float dis);
    }

    public static void setCircularTransforms(List<Card> Cards, Vector3f position, float rotation, Vector3f scale, int ind, float len, float dis) {
        //System.out.println(current().data+" "+ind);
        ind = Math.max(0, Math.min(Cards.size() - 1, ind));
        float zDistance = 1f; // arbitrary separation between card meshes
        for (int i = 0; i < Cards.size(); i++) {
            if (Cards.get(i).mesh != null) {
                Vector3f c = new Vector3f(position).add(0, 0, i * zDistance);

                int midDiff = i - ind;
                float angle = (float) Math.toRadians(Math.min(180f / Cards.size(), 180f / 7f)) * midDiff;
                float distance = len;
                if (midDiff == 0)
                    distance += dis;
                Vector3f rotated = new Vector3f(0, distance, 0).rotateZ(angle + rotation);
                c.add(rotated);

                //System.out.println(c);

                Cards.get(i).mesh.position = c;
                Cards.get(i).mesh.rotation = new Quaternionf();//.lookAlong(c,new Vector3f(0,1,0));
                Cards.get(i).mesh.rotation.rotateLocalY((float) Math.toRadians(180)).rotateLocalX((float) Math.toRadians(90)).rotateLocalZ(angle + rotation);//.lookAlong(c,position);
                Cards.get(i).mesh.scale = scale;

                if (Cards.get(i).HighLight != null) {
                    Cards.get(i).HighLight.position = c.add(0, 0, 0.02f, new Vector3f());
                    //System.out.println("HAS HIGHLIGHT "+Cards.get(i).HighLight.position);
                    Cards.get(i).HighLight.rotation = Cards.get(i).mesh.rotation;
                    Cards.get(i).HighLight.scale = scale.add(.2f, .2f, .2f, new Vector3f());
                }
            }
        }
    }

    public static void setLinearTransforms(List<Card> Cards, Vector3f position, float rotation, Vector3f scale, int ind, float len, float dis) {
        //System.out.println(current().data+" "+ind);
        ind = Math.max(0, Math.min(Cards.size() - 1, ind));
        float zDistance = 1f; // arbitrary separation between card meshes
        for (int i = 0; i < Cards.size(); i++) {
            if (Cards.get(i).mesh != null) {
                Vector3f c = new Vector3f(position).add(0, 0, i * zDistance);

                int midDiff = i - ind;
                float distance = 0;
                if (midDiff == 0)
                    distance += dis;
                Vector3f rotated = new Vector3f(i*-len, distance, 0).rotateZ(rotation);
                c.add(rotated);

                //System.out.println(c);

                Cards.get(i).mesh.position = c;
                Cards.get(i).mesh.rotation = new Quaternionf();//.lookAlong(c,new Vector3f(0,1,0));
                Cards.get(i).mesh.rotation.rotateLocalY((float) Math.toRadians(180)).rotateLocalX((float) Math.toRadians(90)).rotateLocalZ(rotation);//.lookAlong(c,position);
                Cards.get(i).mesh.scale = scale;

                if (Cards.get(i).HighLight != null) {
                    Cards.get(i).HighLight.position = c.add(0, 0, 0.02f, new Vector3f());
                    //System.out.println("HAS HIGHLIGHT "+Cards.get(i).HighLight.position);
                    Cards.get(i).HighLight.rotation = Cards.get(i).mesh.rotation;
                    Cards.get(i).HighLight.scale = scale.add(.2f, .2f, .2f, new Vector3f());
                }
            }
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
