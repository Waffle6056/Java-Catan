public class Player {
    int[] resources =new int[5];
    String name;
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

}
