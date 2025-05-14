import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortHolder<E> extends CardHolder<E>{
    List<Card<E>> TradeRequirements = new ArrayList<>();
    public PortHolder(Player owner) {
        super(owner);
    }

    public Card<E> addPermanent(Card<E> card){
        return super.add(card);
    }

    @Override
    public void remove(Card<E> card) {
        ;
    }
    @Override
    public Card<E> add(Card<E> card){
        return null;
    }
    @Override
    public void clear(){
        ;
    }

    static ArrayList<Card<NewHex.resource>> defaultInventory(int cnt){
        ArrayList<Card<NewHex.resource>> out = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < cnt; j++) {
                out.add(new Card<>(NewHex.resource.values()[i], NewHex.fileNames[i]));
            }
        }
        return out;
    }
    static ArrayList<PortHolder<NewHex.resource>> Ports = new ArrayList<>();
    public static PortHolder<NewHex.resource> generatePort(){
        if (Ports.size() > 0)
            return Ports.remove(0);


        for (int i = 0; i < 5; i++) {
            PortHolder<NewHex.resource> port = new PortHolder<>(null);
            for (Card<NewHex.resource> c : PortHolder.defaultInventory(5))
                port.addPermanent(c);
            for (int j = 0; j < 2; j++) {
                port.TradeRequirements.add(new Card<>(NewHex.resource.values()[i], NewHex.fileNames[i]));
            }
            Ports.add((int)(Math.random()*Ports.size()), port);
        }

        for (int i = 0; i < 4; i++) {
            BankHolder<NewHex.resource> port = new BankHolder<>(null);
            for (Card<NewHex.resource> c : PortHolder.defaultInventory(5))
                port.addPermanent(c);
            for (int j = 0; j < 3; j++) {
                port.TradeRequirements.add(new Card<>(NewHex.resource.Desert, "CatanCardMeshes/Special/Arrow.fbx"));
            }
            Ports.add((int)(Math.random()*Ports.size()), port);
        }

        return Ports.remove(0);
    }
    public boolean matches(List<Card<E>> Offer){
        System.out.println("CALLED PORT MATCHES");
        if (TradeRequirements.size() == 0)
            return true;
        HashMap<E, Integer> req = new HashMap<>();
        for (Card<E> c : TradeRequirements)
            req.put(c.data, req.getOrDefault(c.data,0) + 1);

        HashMap<E, Integer> off = new HashMap<>();
        for (Card<E> c : Offer) {
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
        System.out.println(max+" "+CardsSelected.size());
        return max == CardsSelected.size();
    }
    @Override
    public void trade(CardHolder<E> other){
        System.out.println("MODIFIED TRADE");
        if (!matches(other.CardsSelected))
            return;
        if (other instanceof BankHolder<E> && !((BankHolder<E>) other).matches(CardsSelected))
            return;

        super.trade(other);
    }
}
