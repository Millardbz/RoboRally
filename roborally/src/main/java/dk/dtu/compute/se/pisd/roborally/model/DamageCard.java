package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class DamageCard extends Subject {

    private final Damage damage;
    private int amount = 0;

    public DamageCard(Damage damage){
        this.damage = damage;
    }

    public Damage getDamage(){return damage;}

    public void updateAmount(){amount++;}

    public int getAmount(){return amount;}
}
