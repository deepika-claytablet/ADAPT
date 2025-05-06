package com.claytablet.tological;

import com.google.common.collect.Multimap;

/**
 *
 * @author dpkap
 */
public class AdatRelationships {
    Multimap<Adat,Adat> dependent;
    Multimap<Adat,Pan>  Analyzed;
    
     public AdatRelationships(Multimap<Adat,Adat> dependent, Multimap<Adat,Pan> Analyzed) {
        this.dependent = dependent;
        this.Analyzed = Analyzed;     
    }    
    public Multimap<Adat, Adat> getDependent(){
        return this.dependent;
    }   
    public Multimap<Adat, Pan> getAnalyzed(){
        return this.Analyzed;
    }
}
