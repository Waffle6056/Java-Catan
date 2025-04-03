import RenderingStuff.Mesh;

public class Card<E> {
    public E data;
    public boolean selected;
    //public ?? imageData;
    public Mesh mesh;
    public Card(E data){
        this.data = data;
    }
    public Card(E data, String meshFile){
        this.data = data; mesh = new Mesh(meshFile);
    }
}
