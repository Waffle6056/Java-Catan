import CardStructure.RenderingStuff.Mesh;
import org.joml.*;

import javax.swing.text.Position;
import java.lang.Math;
import java.util.*;
public class GuideHolder<E> extends CardHolder<E>{
    public GuideHolder(Player owner){
        super(owner);
    }
    public void setPositions(){
        //System.out.println(current().data+" "+ind);
        for (int i = 0; i < Cards.size(); i++){
            if (Cards.get(i).mesh != null) {
                Vector3f c = new Vector3f(position);

                int midDiff = i-ind;
                //float angle = (float)Math.toRadians(Math.min(180f/Cards.size(), 180f/7f)) * midDiff;
                float len = 0.1f;
                if (midDiff == 0)
                    len += this.len;
                Vector3f rotated = new Vector3f(0,len,0).rotateZ(rotation);
                c.add(rotated);

                //System.out.println(c);

                Cards.get(i).mesh.position = c;
                Cards.get(i).mesh.rotation = new Quaternionf();//.lookAlong(c,new Vector3f(0,1,0));
                Cards.get(i).mesh.rotation.rotateLocalY((float)Math.toRadians(180)).rotateLocalX((float)Math.toRadians(90)).rotateLocalZ(rotation);//.lookAlong(c,position);

                if (Cards.get(i).selected){
                    Cards.get(i).HighLight.position = c.add(0,0,0.01f, new Vector3f());
                    //System.out.println("HAS HIGHLIGHT "+Cards.get(i).HighLight.position);
                    Cards.get(i).HighLight.rotation = Cards.get(i).mesh.rotation;
                }
            }
        }
    }
}
