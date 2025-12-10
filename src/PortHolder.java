import CardStructure.BankHolder;
import CardStructure.Card;
import CardStructure.CardHolder;
import CardStructure.TradingRequirementFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PortHolder{
    static ArrayList<Card<Hex.resource>> defaultInventory(int cnt){
        ArrayList<Card<Hex.resource>> out = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < cnt; j++) {
                out.add(Hex.resource.createResourceCard(Hex.resource.values()[i]));
            }
        }
        return out;
    }
    public static Card<CardHolder<Hex.resource, Card<Hex.resource>>> generatePortCard(BankHolder<Hex.resource> port) {
        String cardFile = port.TradeRequirements.get(0).data.ResourceMesh;
        Card<CardHolder<Hex.resource, Card<Hex.resource>>> card = new Card<>(port, cardFile);
        return card;
    }

    static ArrayList<BankHolder<Hex.resource>> Ports = new ArrayList<>();
    public static BankHolder<Hex.resource> generatePort(){
        if (Ports.size() > 0)
            return Ports.remove(0);


        for (int i = 0; i < 5; i++) {
            BankHolder<Hex.resource> port = new BankHolder<>(TradingRequirementFunction::SameTypeMatchingAmount);
            for (Card<Hex.resource> c : PortHolder.defaultInventory(5))
                port.addPermanent(c);
            for (int j = 0; j < 2; j++) {
                port.TradeRequirements.add(Hex.resource.createResourceCard(Hex.resource.values()[i]));
            }
            Ports.add((int)(Math.random()*Ports.size()), port);
        }

        for (int i = 0; i < 4; i++) {
            BankHolder<Hex.resource> port = new BankHolder<>(TradingRequirementFunction::SameTypeMatchingAmount);
            for (Card<Hex.resource> c : PortHolder.defaultInventory(5))
                port.addPermanent(c);
            for (int j = 0; j < 3; j++) {
                port.TradeRequirements.add(new Card<>(Hex.resource.Desert, "CatanCardMeshes/Special/Arrow.fbx"));
            }
            Ports.add((int)(Math.random()*Ports.size()), port);
        }

        return Ports.remove(0);
    }
}
