package CardStructure;

import CardStructure.RenderingStuff.Mesh;
import CardStructure.RenderingStuff.Renderable;
import CardStructure.RenderingStuff.Renderable2d;

import java.util.ArrayList;
import java.util.List;

public class Card<E> implements Renderable, Renderable2d {
    public final E data;
    public boolean selected;
    public Mesh HighLight;
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
    public Card<E> duplicate(){
        return new Card<>(data,mesh.file);
    }

    @Override
    public String toString() {
        return this.getClass() +":"+data;
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

    @Override
    public List<Mesh> toMesh2d() {
        ArrayList<Mesh> out = new ArrayList<>();
        if (mesh != null)
            out.add(mesh);
        if (HighLight != null && selected)
            out.add(HighLight);
        return out;
    }
}
