package CardStructure;

import java.util.HashMap;
import java.util.List;

@FunctionalInterface
public interface TradingRequirementFunction {
    boolean matches(BankHolder a, CardHolder b);
    static boolean NoRequirements(CardHolder a, CardHolder b){
        return true;
    }
    static <E> boolean AnyMatchingAmount(BankHolder<E> a, CardHolder<E,Card<E>> b){

        System.out.println("CALLED BANK MATCHES");
        if (a.TradeRequirements.size() == 0)
            return true;
        HashMap<E, Integer> hm = new HashMap<>();
        for (Card<E> c : b.CardsSelected)
            hm.put(c.data, hm.getOrDefault(c.data,0) + 1);
        int max = 0;
        int sz = a.TradeRequirements.size();
        System.out.println(hm);
        for (E d : hm.keySet())
            if (hm.get(d) % sz == 0)
                max += hm.get(d) / sz;
            else
                return false;
        System.out.println(b.CardsSelected+" "+max+" "+a.CardsSelected);
        return a.CardsSelected.size() == max;
    }
    static <E> boolean SameTypeMatchingAmount(BankHolder<E> a, CardHolder<E,Card<E>> b){
        System.out.println("CALLED PORT MATCHES");
        if (a.TradeRequirements.size() == 0)
            return true;
        HashMap<E, Integer> req = new HashMap<>();
        for (Card<E> c : a.TradeRequirements)
            req.put(c.data, req.getOrDefault(c.data,0) + 1);

        HashMap<E, Integer> off = new HashMap<>();
        for (Card<E> c : b.CardsSelected) {
            off.put(c.data, off.getOrDefault(c.data,0) + 1);
        }


        for (E d : req.keySet())
            if (!off.containsKey(d))
                return false;

        int max = -1;
        for (E d : req.keySet()) {
            if (off.get(d) % req.get(d) != 0)
                return false;

            if (max == -1)
                max = off.get(d) / req.get(d);
            else if (off.get(d) / req.get(d) != max)
                return false;

        }
        System.out.println(max+" "+a.CardsSelected.size());
        System.out.println(a.CardsSelected);
        return max == a.CardsSelected.size();
    }
}
