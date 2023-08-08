package com.claytablet.tological;

import com.google.common.collect.Multimap;

/**
 *
 * @author dpkap
 */
public class MultiLevelPan {
    
    Multimap<Pan,Pan>  PANtree;
    Multimap<Pan,Pan>  PANcomplex;
    Multimap<Pan,Pan>  PANcontainment;
    
    public MultiLevelPan(Multimap<Pan,Pan> PANtree, Multimap<Pan,Pan>  PANcomplex, Multimap<Pan, Pan> PANcontainment) {
        this.PANtree = PANtree;
        this.PANcomplex= PANcomplex;
        this.PANcontainment = PANcontainment;
    }
    public  int createTree(){
        // Getting the size
        if(this.PANtree != null){
            int size = this.PANtree.keySet().size();
            return size;
        }
        if(this.PANcontainment != null){
            int size = this.PANcontainment.keySet().size();
            return size;
        }
        else{
            int size = this.PANcomplex.keySet().size();
            return size;
        }   
    }
    public Multimap<Pan,Pan> getPantree(){
        return this.PANtree;
    }
    public Multimap<Pan,Pan> getPancomplex(){
        return this.PANcomplex;
    }
    public Multimap<Pan,Pan> getPancontainment(){
        return this.PANcontainment;
    }
}
