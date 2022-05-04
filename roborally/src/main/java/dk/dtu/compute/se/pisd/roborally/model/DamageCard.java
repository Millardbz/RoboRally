package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class DamageCard extends Subject {

    public final Damage damageType;

    public DamageCard(Damage damageType){
        this.damageType = damageType;
    }
    public String getDamageType() {return damageType.displayName;}
}
