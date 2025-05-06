package com.claytablet.relational;

import com.claytablet.tological.Adat;
import com.claytablet.tological.AdatAttributeRelationships;
import com.claytablet.tological.AdatRelationships;
import com.claytablet.tological.MultiLevelAdat;
import com.google.common.collect.Multimap;
import java.util.Collection;

/**
 *
 * @author dpkap
 */
public class ATR_specialized extends adatToRelations{
    String output;
    public ATR_specialized(Adat A) {
        super(A);
    }
     public String createSpecializedLevel( MultiLevelAdat mla, AdatAttributeRelationships [] aar, AdatRelationships [] ar){  
       output = abc(mla,this.A, aar, ar);
       return output;
    }
    int count=0;
    public String abc(MultiLevelAdat mla ,Adat ai, AdatAttributeRelationships [] aar, AdatRelationships [] ar){     
            Multimap<Adat, Adat> tree = mla.getAdattree(); 
            Collection<Adat> children = tree.get(ai);  
            for (Adat i : children){
                if (count==0)                 
                    output = output + "\n\n"+createRelation(ai) + "\n"+ createRelationships(ai,ar) + "\n" + createAnalysisProperty(ai, aar)
                +"\n" + createDependent(ai, ar);                
                output = output + "\n\n"+createRelation(i);            
                
                if(!tree.get(i).isEmpty()) {
                    count =1;  
                    output = output + "\n" + "alter table " + i.getName() + " add "+ addColumn(ai.getName()+"_Key\t varchar(50) PRIMARY KEY")+  " ;";
                    output = output + "\n" + "alter table " + i.getName() + " add foreign key ("+ ai.getName() +  "_Key) references "+ 
                        ai.getName() + "(" + ai.getName() + "_Key);";
                    abc(mla,i, aar, ar);             
                }
                else{
                    output = output + "\n" + "alter table " + i.getName() + " add "+ addColumn(ai.getName()+"_Key\t varchar(50) PRIMARY KEY")+  " ;";
                    output = output + "\n" + "alter table " + i.getName() + " add foreign key ("+ ai.getName() +  "_Key) references "+ 
                        ai.getName() + "(" + ai.getName() + "_Key);";
                    count=1;
                }
            }                       
        return output;
    }
}
