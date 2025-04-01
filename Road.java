public class Road {
    Player owner;
    Road[] connects=new Road[4];
    Building left, right;
    int creation=a;
    static int a=0;
    public Road(){
        owner=null;
        a++;
    }

    public Road[] getConnects() {
        return connects;
    }
    public Player getOwner() {
        return owner;
    }
    public void connectRoads(Road e){
       // System.out.print(creation+" is connecting to "+e.creation+"  :");
        for (int i = 0; i < 4; i++) {
            if (connects[i]==null){
                connects[i]=e;
                break;
            }
           if(connects[i].creation == e.creation){
             //  System.out.print("AllreadyDone");
               return;
           }
        }
        for (int i = 0; i < 4; i++) {
            if (e.connects[i]==null){
                e.connects[i]=this;
                break;
            }
        }
      //  System.out.println("Sucess");
    }
}
