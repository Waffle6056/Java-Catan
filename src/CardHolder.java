import RenderingStuff.Mesh;
import org.joml.*;

import javax.swing.text.Position;
import java.lang.Math;
import java.util.*;
public class CardHolder<E> {
    List<Card<E>> Cards = new ArrayList<>();
    int ind = 0;
    List<Card<E>> CardsSelected = new ArrayList<>();
    boolean visible = false;
    public Vector3f position = new Vector3f(0,0,0);
    List<Mesh> meshes = new ArrayList<>();
    public Card<E> add(Card<E> card){
        Cards.add(card);
        if (card.mesh != null)
            meshes.add(card.mesh);
        return card;
    }

    public Card<E> add(E data){
        return add(new Card<>(data));
    }
    public Card<E> add(E data, String meshFile){
        return add(new Card<>(data, meshFile));
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
        meshes.clear();
    }
    public void scroll(int delta){
        if (Cards.size() == 0)
            return;
        ind += delta;
        ind = Math.floorMod(ind, Cards.size());
        setPositions();
    }
    public Card<E> current(){
        if (Cards.size() == 0)
            return null;
        return Cards.get(ind);
    }
    public void select(){
        if (Cards.size() == 0)
            return;
        Card<E> Current = Cards.get(ind);
        Current.selected = !Current.selected;
        if (Current.selected)
            CardsSelected.add(Current);
        else
            CardsSelected.remove(Current);
    }

    public void toggleVisible(){
        visible = !visible;
        if (visible){
            setPositions();
        }
    }
    public void toggleVisible(boolean val){
        if (val && !visible || !val && visible)
            toggleVisible();
    }
    public List<Mesh> getMeshes(){return meshes;}
    public void setPositions(){
        System.out.println(current().data+" "+ind);
        for (int i = 0; i < Cards.size(); i++){
            if (Cards.get(i).mesh != null) {
                Vector3f c = new Vector3f(position);

                int midDiff = i-ind;
                float angle = (float)Math.toRadians(180f/7f) * midDiff;
                float len = 0.1f;
                if (midDiff == 0)
                    len += 0.05f;
                c.add(new Vector3f(0,len,0).rotateZ(angle));

                System.out.println(c);

                Cards.get(i).mesh.position = c;
                Cards.get(i).mesh.rotation = new Quaternionf();//.lookAlong(c,new Vector3f(0,1,0));
                Cards.get(i).mesh.rotation.rotateLocalX((float)Math.toRadians(-90)).rotateLocalZ(angle);//.lookAlong(c,position);
            }
        }
    }
}
