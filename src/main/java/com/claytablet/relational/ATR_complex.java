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
public class ATR_complex extends adatToRelations{
    String output;
    public ATR_complex(Adat A) {
        super(A);
    }
    public String createComplexLevel( MultiLevelAdat mla, AdatAttributeRelationships [] aar, AdatRelationships [] ar){ 
       output = abc(mla,this.A, aar, ar);
       return output;
    }
    int count=0;
    public String abc(MultiLevelAdat mla ,Adat ai, AdatAttributeRelationships [] aar, AdatRelationships [] ar){     
            Multimap<Adat, Adat> tree = mla.getAdatcomplex(); 
            Collection<Adat> children = tree.get(ai);  
            for (Adat i : children){
                if (count==0)                 
                    output = output + "\n\n"+createRelation(ai) + "\n" + createRelationships(ai, aar, ar);                
                output = output + "\n\n"+createRelation(i);             
                
                if(!tree.get(i).isEmpty()) {
                    count =1;  
                    output = output + "\n" + "alter table " + ai.getName() + " add "+ addColumn(i.getName()+"_Key\t varchar(50)")+  " ;";
                    output = output + "\n" + "alter table " + ai.getName() + " add foreign key ("+ i.getName() +  "_Key) references "+ 
                        i.getName() + "(" + i.getName() + "_Key);";
                    abc(mla,i, aar, ar);             
                }
                else{
                    output = output + "\n" + "alter table " + ai.getName() + " add "+ addColumn(i.getName()+"_Key\t varchar(50)")+  " ;";
                    output = output + "\n" + "alter table " + ai.getName() + " add foreign key ("+ i.getName() +  "_Key) references "+ 
                        i.getName() + "(" + i.getName() + "_Key);";
                    count=1;
                }
            }          
             
        return output;
    }
}