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
public class ATR_complex extends adatToRelations{
    String output="\n";
    int count=0;
    String temp = "";
  //  String child="";
    public ATR_complex(Adat A) {
        super(A);
    }
    
    //Same PANs
    public String createComplexCase1(Adat a, MultiLevelAdat mla , AdatAttributeRelationships [] aar, AdatRelationships [] ar){     
        Multimap<Adat, Adat> tree = mla.getAdatcomplex(); 
        Collection<Adat> children = tree.get(a);
        this.output= this.output+ "\n"+ this.createRelation(a) + "\n" + createRelationships(a,ar) + "\n" + createAnalysisProperty(a, aar)
                +"\n" + createDependent(a, ar);     
    
        for (Adat i : children){ 
           if (!(tree.get(i).isEmpty())){            
            output = createComplexCase1(i, mla, aar, ar);  
            this.count=0;
            }
            if(this.count==0){
                output = output + "\n create table complexContentFor_"+ a.getName() + " (";
                this.count=1;
            }
            for (Map.Entry<String,String> it : i.getAttr_DataKind().entrySet()) {
                output = output + "\n" + it.getKey() +"\t"+ it.getValue()+ ",";                       
            }
        }
        this.output = this.output + "\ncomplexContentFor_" + a.getName() +"_key\tvarchar(50) PRIMARY KEY "; 
        this.output=this.output+");";
        temp = "\n" + createRelationships(a,ar) + "\n" ; 
  
        String replace = temp.replace("alter table "+a.getName(),"alter table complexContentFor_" +a.getName());
        this.output=this.output+"\n" + replace; 
            
        output = output + "\n" + "alter table " + a.getName() + " add complexContentFor_" + a.getName()+"_key\tvarchar(50)" ; 
        output = output + "\n" + "alter table " + a.getName() + " add foreign key (complexContentFor_"+ a.getName() +  "_key) references complexContentFor_"+ 
                        a.getName() + "(complexContentFor_" + a.getName() + "_key);";
         
        return output;
    }
    //Different PANs
    public String createComplexCase2(Adat a, MultiLevelAdat mla, AdatAttributeRelationships [] aar, AdatRelationships [] ar){
        Multimap<Adat, Adat> tree = mla.getAdatcomplex(); 
        Collection<Adat> children = tree.get(a);
        if (count ==0){
            output= output+ "\n"+ createRelation(a) + "\n" + createRelationships(a,ar) + "\n" + createAnalysisProperty(a, aar)
                +"\n" + createDependent(a, ar);     
            count=1;
        }
        for (Adat i : children){ 
            if(!tree.containsKey(i)) {  
                output= output+ "\n"+ createRelation(i) + "\n" + createRelationships(a,ar) + "\n" + createAnalysisProperty(a, aar)
                +"\n" + createDependent(a, ar);
                output = output + "\n" + "alter table " + a.getName() + " add "+ addColumn(i.getName()+"_key\tvarchar(50) PRIMARY KEY ")+  " ;";
                output = output + "\n" + "alter table " + a.getName() + " add foreign key ("+ i.getName() +  "_key) references "+ 
                        i.getName() + "(" + i.getName() + "_key);";
            }
            if (!(tree.get(i).isEmpty())){
                createComplexCase2(i, mla, aar, ar);  
            }
        }
        return output;
    }
}
