package CardStructure;

import CardStructure.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class BankHolder<E> extends CardHolder<E, Card<E>> {
    public BankHolder(TradingRequirementFunction tradingRequirementFunction){
        this.tradingRequirementFunction = tradingRequirementFunction;
    }
    public List<Card<E>> TradeRequirements = new ArrayList<>();
    public TradingRequirementFunction tradingRequirementFunction = TradingRequirementFunction::NoRequirements;

    public Card<E> addPermanent(Card<E> card){
        return super.add(card);
    }

    @Override
    public void remove(Card<E> card) {
        super.add(indexOf(card.data),card.duplicate());
        super.remove(card);
    }
    @Override
    public Card<E> add(Card<E> card){
        return null;
    }
    @Override
    public void clear(){
        CardsSelected = new ArrayList<>();
    }
    @Override
    public void trade(CardHolder<E, Card<E>> other){
        System.out.println("MODIFIED TRADE");
        if (!tradingRequirementFunction.matches(this,other))
            return;
        if (other instanceof BankHolder<E> && !((BankHolder<E>) other).tradingRequirementFunction.matches(this,other))
            return;

        super.trade(other);
    }

}
