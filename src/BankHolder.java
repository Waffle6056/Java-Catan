import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class BankHolder<E, C extends Card<E>> extends PortHolder<E, C>{

    public BankHolder(Player owner) {
        super(owner);
    }

    @Override
    public boolean matches(List<C> Offer){

        System.out.println("CALLED BANK MATCHES");
        if (TradeRequirements.size() == 0)
            return true;
        HashMap<E, Integer> hm = new HashMap<>();
        for (Card<E> c : Offer)
            hm.put(c.data, hm.getOrDefault(c.data,0) + 1);
        int max = 0;
        int sz = TradeRequirements.size();
        System.out.println(hm);
        for (E d : hm.keySet())
            if (hm.get(d) % sz == 0)
                max += hm.get(d) / sz;
            else
                return false;
        System.out.println(Offer+" "+max+" "+CardsSelected);
        return CardsSelected.size() == max;
    }
}
