
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
public class ATR_Containment extends adatToRelations{
    String output="\n";
    int count=0;
    public ATR_Containment(Adat A) {
        super(A);
    }
  /*  public String createForContent1(MultiLevelAdat mla ,Adat ai, AdatAttributeRelationships [] aar, AdatRelationships [] ar){
        if(count==0){
            output = output + "\n"+createRelation(ai) + "\n" + createRelationships(ai, aar, ar); 
            count=1;
        }
        Multimap<Adat, Adat> tree = mla.getAdatcontainment(); 
        Collection<Adat> children = tree.get(ai);  
        for (Adat i : children){                               
            output = output + "\n\n"+createRelation(ai) + "\n" + createRelationships(ai, aar, ar);
            output = output + "\n" + "create table "+ ai.getName()+"_"+i.getName()
                +"(\n"+ai.getName()+"_key\tvarchar(50),"
                +i.getName()+"_key\tvarchar(50)\n);";
            if(!tree.get(i).isEmpty()) { 
                createForContent1(mla,i, aar, ar);             
            }
        } 
        return output;
    }*/
    public String createForContent(MultiLevelAdat mla ,Adat ai, AdatAttributeRelationships [] aar, AdatRelationships [] ar){
        if(count==0){
            output = output + "\n"+createRelation(ai) + "\n" + createRelationships(ai,ar) + "\n" + createAnalysisProperty(ai, aar)
                +"\n" + createDependent(ai, ar); 
            count=1;
        }
        Multimap<Adat, Adat> tree = mla.getAdatcontainment(); 
        Collection<Adat> children = tree.get(ai);  
        for (Adat i : children){                               
            output = output + "\n\n"+createRelation(i) + "\n" 
                    + createRelationships(i,ar) + "\n" 
                    + createAnalysisProperty(i, aar)
                +"\n" + createDependent(i, ar);
           
            output = output + "\n" + "alter table " + i.getName() + " add "+ addColumn(ai.getName()+"_Key\t varchar(50) PRIMARY KEY")+  " ;";
            output = output + "\n" + "alter table " + i.getName() + " add foreign key ("+ ai.getName() +  "_Key) references "+ 
                ai.getName() + "(" + ai.getName() + "_Key);";
            
            if(!tree.get(i).isEmpty()) { 
                createForContent(mla,i, aar, ar); 
            }
        }
        return output;
    }
}
