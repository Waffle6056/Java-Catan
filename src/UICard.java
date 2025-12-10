import CardStructure.Card;
import CardStructure.CardHolder;

public class UICard extends Card<CardHolder> {
    @FunctionalInterface
    interface CardInteract<T> {
        public void use(T element, Catan instance, Player owner);
    }
    CardInteract effect;
    public <E extends CardHolder> UICard(E data, CardInteract<E> effect){
        super(data);
        this.effect = effect;
    }
    public <E extends CardHolder> UICard(E data, String file, CardInteract<E> effect){
        super(data,file);
        this.effect = effect;
    }
    public void use(Catan instance, Player owner){
        effect.use(data,instance,owner);
    }
}
