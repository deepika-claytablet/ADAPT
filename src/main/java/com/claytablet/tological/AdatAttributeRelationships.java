package com.claytablet.tological;

import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author dpkap
 */
public class AdatAttributeRelationships {
    HashMap<ArrayList, Boolean> isAdditive;
    HashMap<ArrayList, String> cardinality;
    HashMap<ArrayList, Boolean> applicability;
  
    public AdatAttributeRelationships(HashMap<ArrayList,Boolean> isAdditive
            , HashMap<ArrayList, String> cardinality, HashMap<ArrayList, Boolean> applicability) {             
        this.isAdditive = isAdditive;
        this.cardinality = cardinality;
        this.applicability = applicability;
    }
    public HashMap<ArrayList, Boolean> getIsAdditive(){
        return this.isAdditive;
    }
    public HashMap<ArrayList, String> getCardinality(){
        return this.cardinality;
    } 
    public HashMap<ArrayList, Boolean> getApplicability(){
        return this.applicability;
    }
}
