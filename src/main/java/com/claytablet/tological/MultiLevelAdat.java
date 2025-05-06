package com.claytablet.tological;

import com.google.common.collect.Multimap;

/**
 *
 * @author dpkap
 */
public class MultiLevelAdat {
    Multimap<Adat,Adat> ADATtree;
    Multimap<Adat,Adat>  ADATcomplex;
    Multimap<Adat,Adat>  ADATcontainment;
    Multimap<Adat,Adat>  ADATderived;
    
    public MultiLevelAdat(Multimap<Adat,Adat> ADATtree, Multimap<Adat,Adat>  ADATcomplex,
            Multimap<Adat, Adat> ADATcontainment, Multimap<Adat, Adat> ADATderived ){
        this.ADATtree = ADATtree;
        this.ADATcomplex= ADATcomplex;
        this.ADATcontainment = ADATcontainment;
        this.ADATderived = ADATderived;
    }
    public  int createTree(){
        // Getting the size
        if(this.ADATtree != null){
            int size = this.ADATtree.keySet().size();
            return size;
        }
        if(this.ADATcomplex != null){
            int size = this.ADATcomplex.keySet().size();
            return size;
        }
         if(this.ADATcontainment != null){
            int size = this.ADATcontainment.keySet().size();
            return size;
        }
        else{
            int size = this.ADATderived.keySet().size();
            return size;
        }   
    }
    public Multimap<Adat,Adat> getAdattree(){
        return this.ADATtree;
    }
    public Multimap<Adat,Adat> getAdatcomplex(){
        return this.ADATcomplex;
    }
    public Multimap<Adat,Adat> getAdatcontainment(){
        return this.ADATcontainment;
    }
     public Multimap<Adat,Adat> getAdatderived(){
        return this.ADATderived;
    }
    
}
