public class Building {
   int resourcegain;
   Player owner;
   boolean inverted;// if Y is upsidedown;
    Road down,upleft,upright;

   public Building(Road d,Road ul,Road ur,boolean inverted){
       resourcegain=0;
       owner=null;
       down=d;
       upleft=ul;
       upright=ur;
       this.inverted=inverted;
   }
   public void gather(Hex.resource resource){
       //check resource and owner exists
       owner.resources[0]++;
   }
   public void connect(){
       if (down!=null) down.connect(this);
       if (upleft!=null) upleft.connect(this);
       if (upright!=null) upright.connect(this);
   }
   public void combine(Building e){
       if (down!=null){ down=e.down;}
       if (upleft!=null){ upleft=e.upleft;}
       if (upright!=null){ upright=e.upright;}
   }


}
