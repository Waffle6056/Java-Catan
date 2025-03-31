public class Player {
    int[] resources =new int[5];
    String name;
    int vpvisable=0,vphidden=0;
    public Player(){
        Playercreate("Tester");
    }
    public Player(String name){
        Playercreate(name);
    }
    public void Playercreate(String name){
        this.name=name;
    }
    public int[] getResources(){
        return resources;
    }
    public boolean hasWon(){
        return 10<=(vpvisable+vphidden);
    }
}
