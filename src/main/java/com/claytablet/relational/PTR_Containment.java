package com.claytablet.relational;


import com.claytablet.tological.AdatAttributeRelationships;
import com.google.common.collect.Multimap;
import com.claytablet.tological.MultiLevelPan;
import com.claytablet.tological.Pan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 *
 * @author dpkap
 */
public class PTR_Containment extends panToRelations{
    String output="";
    public PTR_Containment(Pan P) {
        super(P);
    }
    public Boolean getApplicability(Pan pi, AdatAttributeRelationships[] aar){ 
        for(AdatAttributeRelationships j : aar){
            HashMap<ArrayList,Boolean> MapApplicability = j.getApplicability();
            Set<ArrayList> key = MapApplicability.keySet();
            Iterator<ArrayList> iterator = key.iterator();  
            ArrayList next = iterator.next();
            String Panname = pi.getName();
            if(Panname.equalsIgnoreCase(next.get(2).toString())){                
                return(Boolean.valueOf(MapApplicability.values().toString().replaceAll("[\\[\\](){}]","")));               
            }
            
        }
        return false;
    }
    int count=0;
    public String createContentForContainer(MultiLevelPan mlp ,Pan container, AdatAttributeRelationships[] aar){
        if(count==0){
            output = createRelation(container);
            count=1;
        }
        Collection<Pan> contents = mlp.getPanContent(container); 
        if(getApplicability(container,aar)){
        for (Pan i : contents){                                        
                    for(String attr:  i.getAttribte().keySet())            
                    {
                        output = output + "\nalter table Dim_"+ container.getName()+ " add " +addColumn(attr) +";";
                        if ((i.getAttribte().get(attr)).equals("no_update")){
                            output = output + "\nalter table Dim_"+ container.getName()
                                    + " add "+ addColumn(attr+"_TMSP")+";";
                        }                        
                    }
                    output = output + "\n" + "alter table Dim_" + container.getName() + " add "+ addColumn(i.getName()+"_SK")+  " ;";  
            
            if(mlp.getPanContent(i)!=null){
                createContentForContainer(mlp, i, aar);
            }
        }
        }
        if(count==1) {
            count=2;
            createContainerForContent( mlp , container, aar);
        }        
        return output;
    }
    public String createContainerForContent(MultiLevelPan mlp ,Pan content, AdatAttributeRelationships[] aar){        
        if(count==0){
            output = createRelation(content);
            count=1;
        }
        Multimap<Pan, Pan> containerMLP = mlp.getPanContainerMLP(); 
        Collection<Pan> containers = new ArrayList<>();        
                for (Map.Entry<Pan, Pan> entry : containerMLP.entries()) {
                if (entry.getValue().equals(content)) {
                    containers.add(entry.getKey());
                }
            }
            
        
        
        for (Pan i : containers){
            if(getApplicability(content,aar)){                             
                    for(String attr:  i.getAttribte().keySet())            
                    {
                        output = output + "\nalter table Dim_"+ content.getName()+ " add " +addColumn(attr) +";";
                        if ((i.getAttribte().get(attr)).equals("no_update")){
                        output = output + "\nalter table Dim_"+ content.getName()+ " add "+ addColumn(attr+"_TMSP")+";";
                        }                        
                    }
                    output = output + "\n" + "alter table Dim_" + content.getName() + " add "+ addColumn(i.getName()+"_SK")+  " ;";  
            }
            if(containerMLP.get(i)!=null){
                createContainerForContent(mlp, i, aar);
            }
        }
        if(count==1) {
            count=2;
            createContentForContainer( mlp , content, aar);
        }       
        return output;
        
    }
}
