public class UICard extends Card<CardHolder>{
    @FunctionalInterface
    interface CardInteract<T> {
        public void use(T element, Catan instance);
    }
    CardInteract effect;
    public <E extends CardHolder> UICard(E data, CardInteract<E> effect){
        super(data);
        this.effect = effect;
    }
    public void use(Catan instance){
        effect.use(data,instance);
    }
}
