//import RenderingStuff.Mesh;
//import org.joml.*;
//
//import javax.swing.text.Position;
//import java.lang.Math;
//import java.util.*;
//public class GuideHolder<E,C extends Card<E>> extends CardHolder{
//    CardHolder<E,C> playerCards;
//    Catan instance;
//    float[] xchange=new float[]{.25f,.25f,.1f,.1f,-.05f,-.05f},
//            ychange=new float[]{.2f,0,.2f,0,.2f,0};
//    public GuideHolder(Player owner){
//        super(owner);
//    }
//
//    public void build(Catan instance){
//        playerCards=new CardHolder<>(owner);
//        for (int i = 0; i < Player.ModelSet.values().length; i++) {
//            String file = Player.ModelSet.values()[i].ColorIndicator;
//            playerCards.add(new Card<>(null, file));
//            playerCards.get(i).HighLight = new Mesh(file);
//        }
//        addAll(playerCards);
//        Vector3f c = new Vector3f(position);
//        // playerCards.get(instance.turnInd).HighLight.position = c.add(0, 0, 0.005f, new Vector3f());
//        // Cards.add(playerCards.get(instance.turnInd));
//        this.instance=instance;
//        // Cards.get(1).selected=true;
//    }
//
//    public void setTransforms(){
//        for (int i = 0; i < Cards.size(); i++) {
//            if (Cards.get(i).mesh != null) {
//                Vector3f c = new Vector3f(position);
//                float len = 0.1f;
//                Vector3f rotated = new Vector3f(0, len, 0).rotateZ(rotation);
//                c.add(rotated);
//
//                //System.out.println(c);
//
//                Cards.get(i).mesh.position = c.add(xchange[i], ychange[i], 0.01f, new Vector3f());;
//                Cards.get(i).mesh.rotation = new Quaternionf();//.lookAlong(c,new Vector3f(0,1,0));
//                Cards.get(i).mesh.rotation.rotateLocalY((float) Math.toRadians(180)).rotateLocalX((float) Math.toRadians(90)).rotateLocalZ(rotation);//.lookAlong(c,position);
//            }
//        }
//        Vector3f c = new Vector3f(position);
//        float len = 0.1f;
//        Vector3f rotated = new Vector3f(0, len, 0).rotateZ(rotation);
//        c.add(rotated);
//    }
//}
