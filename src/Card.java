import RenderingStuff.Mesh;

import java.util.ArrayList;
import java.util.List;

public class Card<E> implements Renderable {
    public E data;
    public boolean selected;
    public Mesh HighLight;
    //public ?? imageData;
    public Mesh mesh;
    public String file;
    public Card(E data){
        this.data = data;
        try {
            HighLight = new Mesh("CatanCardMeshes/PlayerOne.fbx");
        } catch (Exception e){}
    }
    public Card(E data, String meshFile){
        this(data);
        file = meshFile;
        mesh = new Mesh(meshFile);
        HighLight.scale.mul(1.1f);
    }

    public boolean equals(Card<E> other) {
        return data.equals(other.data);
    }

    @Override
    public List<Mesh> toMesh() {
        ArrayList<Mesh> out = new ArrayList<>();
        if (mesh != null)
            out.add(mesh);
        if (HighLight != null && selected)
            out.add(HighLight);
        return out;
    }
    public static Card<Hex.resource> createResourceCard(Hex.resource r){
        return new Card<>(r, Hex.resourceFileNames[r.index]);
    }
}
