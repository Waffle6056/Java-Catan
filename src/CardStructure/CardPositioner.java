package CardStructure;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

@FunctionalInterface
public interface CardPositioner {
    void SetTransforms(List<Card> Cards, Vector3f position, float rotation, Vector3f scale, int ind, float len, float dis);
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
}