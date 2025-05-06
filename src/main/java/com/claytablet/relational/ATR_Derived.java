package com.claytablet.relational;

import com.claytablet.tological.Adat;
import com.claytablet.tological.AdatAttributeRelationships;
import com.claytablet.tological.AdatRelationships;
import com.claytablet.tological.MultiLevelAdat;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author dpkap
 */
public class ATR_Derived extends adatToRelations{
    
    String output="\n";
    
    public ATR_Derived(Adat A){
        super(A);
    }
    int count=0;
    String temp = "";
    //Same PANs
    public String createDerivedCase1(Adat a, MultiLevelAdat mla , AdatAttributeRelationships [] aar, AdatRelationships [] ar){     
        
        Multimap<Adat, Adat> tree = mla.getAdatderived(); 
        Collection<Adat> children = tree.get(a);
        this.output= this.output+ "\n"+ this.createRelation(a) + "\n" + createRelationships(a,ar) + "\n" + createAnalysisProperty(a, aar)
                +"\n" + createDependent(a, ar);     
        for (Adat i : children){ 
           if (!(tree.get(i).isEmpty())){            
            output = createDerivedCase1(i, mla, aar, ar);  
            this.count=0;
            }
            if(this.count==0){
                output = output + "\n create table derivedBaseFor_"+ a.getName() + " (";
                this.count=1;
            }
            for (Map.Entry<String,String> it : i.getAttr_DataKind().entrySet()) {
                output = output + "\n" + it.getKey() +"\t"+ it.getValue()+ ",";                       
            }
        }
        this.output = this.output + "\nderivedBaseFor_" + a.getName() +"_key\tvarchar(50) PRIMARY KEY"; 
            this.output=this.output+");";
        temp = "\n" + createRelationships(a,ar) + "\n" + temp    ; 
            String replace = temp.replace("alter table "+a.getName(),"alter table derivedBaseFor_" +a.getName());
            this.output=this.output+"\n" + replace; 
            
        output = output + "\n" + "alter table " + a.getName() + " add derivedBaseFor_" + a.getName()+"_key\tvarchar(50) PARIMARY KEY" ; 
        output = output + "\n" + "alter table " + a.getName() + " add foreign key (derivedBaseFor_"+ a.getName() +  "_key) references derivedBaseFor_"+ 
                        a.getName() + "(derivedBaseFor_" + a.getName() + "_key);";
         
        return output;
    }
    //different pans
    public String createDerivedCase2(Adat a, MultiLevelAdat mla, AdatAttributeRelationships [] aar, AdatRelationships [] ar){
        Multimap<Adat, Adat> tree = mla.getAdatderived(); 
        Collection<Adat> children = tree.get(a);
        if (count ==0){
            output= output+ "\n"+ createRelation(a) + "\n" + createRelationships(a,ar) + "\n" + createAnalysisProperty(a, aar)
                +"\n" + createDependent(a, ar);     
            count=1;
        }
        for (Adat i : children){ 
            if(!tree.containsKey(i)) {  
                output= output+ "\n"+ createRelation(i) + "\n" + createRelationships(i,ar) + "\n" + createAnalysisProperty(i, aar)
                +"\n" + createDependent(i, ar);
                output = output + "\n" + "alter table " + a.getName() + " add "+ addColumn(i.getName()+"_key\tvarchar(50) PRIMARY KEY")+  " ;";
                output = output + "\n" + "alter table " + a.getName() + " add foreign key ("+ i.getName() +  "_key) references "+ 
                        i.getName() + "(" + i.getName() + "_key);";
            }
            if (!(tree.get(i).isEmpty())){
                createDerivedCase2(i, mla, aar, ar);  
            }
        }
        return output;
    }
}
