import RenderingStuff.Mesh;

public class Card<E> {
    public E data;
    public boolean selected;
    //public ?? imageData;
    public Mesh mesh;
    public Card(E data){
        this.data = data;
    }
}
