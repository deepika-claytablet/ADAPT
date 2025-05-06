/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package columnfamily;

import static columnfamily.atomic.len;
import com.claytablet.tological.Adat;
import com.claytablet.tological.AdatAttributeRelationships;
import com.claytablet.tological.AdatRelationships;
import com.claytablet.tological.MultiLevelPan;
import com.claytablet.tological.Pan;
import com.google.common.collect.Multimap;
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
public class PanColumn {
    String [] Col;
    Map<String, String> List;
    String [] ColMeta;
    Map<String, String> ListMeta;
    static int len=0;
    static int metalen=0;
    String[] Colfamily;
    static int familylen=0;
    
    PanColumn(){
        this.List = new HashMap<>();
        this.Col = new String[1000];
        this.Colfamily = new String[1000];
        this.ListMeta = new HashMap<>();
        this.ColMeta = new String[1000];
    }
    
    private HashMap<Pan,ArrayList> createRelationships(Adat a, String attr, AdatAttributeRelationships[] aar, AdatRelationships[] ar, MultiLevelPan mlp){
    
    int count =0;
    // mapping is-analyzed-by relationship
    Boolean toreturn =false;
    //getting list of PANs
    HashMap<Pan,ArrayList> pans = new HashMap<>();
    for(AdatRelationships isab: ar){
        Multimap<Adat,Pan> i =  isab.getAnalyzed();
        for(Adat Adatkey: i.keySet()){
            if(Adatkey.getName().equalsIgnoreCase(a.getName())){
                Collection <Pan> pValues = i.get(a);   
                for(Pan value : pValues){
                    ArrayList<String> properties = new ArrayList<>();
                    for(AdatAttributeRelationships j:aar){
                        if(j == null) break;
                        HashMap<ArrayList,Boolean> isAdditive = j.getIsAdditive();
                        HashMap<ArrayList,String> cardinality = j.getCardinality();
                        HashMap<ArrayList,Boolean> applicability = j.getApplicability();
                        Set<ArrayList> key = isAdditive.keySet();
                        Iterator<ArrayList> iterator = key.iterator(); 
                        ArrayList next = iterator.next(); 
                        if(next.get(0).toString().equals(a.getName()) 
                            && next.get(1).toString().equalsIgnoreCase(attr) 
                            && next.get(2).toString().equalsIgnoreCase(value.getName()) )
                        {
                            this.ListMeta.put("is_Additive_"+next.get(0).toString()+"_"+next.get(1).toString()+"_"+ next.get(2).toString(), "map<frozen<set<text>>,Boolean>");
                            this.ListMeta.put("cardinality_"+next.get(0).toString()+"_"+next.get(1).toString()+"_"+ next.get(2).toString(), "map<frozen<set<text>>,text>");
                          //  this.ListMeta.put("applicability"+next.get(0).toString()+"_"+next.get(1).toString()+"_"+ next.get(2).toString(), "map<frozen<set<text>>,Boolean>");

                            this.ColMeta[metalen++] = "is_Additive: "+next.get(0).toString()+"_"+next.get(1).toString()+"_"+ next.get(2).toString();
                            this.ColMeta[metalen++] = "cardinality: "+next.get(0).toString()+"_"+next.get(1).toString()+"_"+ next.get(2).toString();
                          //  this.ColMeta[metalen++] = "applicability: "+next.get(0).toString()+"_"+next.get(1).toString()+"_"+ next.get(2).toString();
                                
                            Collection<Boolean> appl = applicability.values();                                
                            for(Boolean b : appl){
                                toreturn = b;
                            }
                            if(!(mlp.getPanComplex(value)==null) 
                                || !(mlp.getPanConstituent(value).isEmpty())){
                                properties.add("complex");
                                properties.add(toreturn.toString());
                                pans.put(value, properties);
                                break;
                            }
                            else if (!(mlp.getPanContainer(value)==null) 
                                || !(mlp.getPanContent(value).isEmpty())){
                                properties.add("container");
                                properties.add(toreturn.toString());
                                pans.put(value, properties);
                                break;
                            }
                            else if ((mlp.getPanParent(value)==null) 
                                && !(mlp.getPantree(value).isEmpty())){
                                properties.add("specialization1");
                                properties.add("false");
                                pans.put(value, properties);
                                break;
                            }
                            else if (!(mlp.getPanParent(value)==null)) {
                                properties.add("specialization2");
                                properties.add("false");
                                pans.put(value, properties);
                                break;
                            }
                            else{
                                properties.add("atomic");
                                properties.add("false");
                                pans.put(value, properties);
                                break;
                            }
                        } 
                    }
                }               
            }   
        }           
    }
  
    //mapping dependent relationship        
    for(AdatRelationships m: ar){
        Multimap<Adat, Adat> dependent = m.getDependent(); 
        if(!dependent.isEmpty()){
            ListMeta.put("DependentAdat_"+m,"set<text>");
            ListMeta.put("DependeeAdat_"+m,"set<text>");
            ColMeta[metalen++] = "Dependent_Dependee: dependentADAT";
            ColMeta[metalen++] = "Dependent_Dependee: dependeeADAT";            
            //String dependee_dent =  a.getName() + ":"+ a1.getName();
        }
    }
    //returing the entire output string        
    return pans;
}
   public void convertPan(Adat a, AdatRelationships [] ar, AdatAttributeRelationships [] mla,MultiLevelPan mlp, String attr){
       HashMap<Pan,ArrayList> pans = createRelationships(a,attr, mla, ar, mlp);
       for(Pan p:pans.keySet()){
            ArrayList value = pans.get(p);
            Iterator<String> iterator = value.iterator();
            String next = iterator.next(); 
            switch (next) {
            case "atomic":
                createAtomicPANCF(p);
                break;
            case "complex":
                createComplexPANCF(p,mlp,Boolean.valueOf(iterator.next()));
                break;
            case "specialization1":
                createSpecializedPANCF_Case1(p,mlp);
                break;
            case "specialization2":
                createSpecializedPANCF_Case2(p,mlp);
                break;
            case "containment":
                createContainerPANCF(p, mlp,Boolean.valueOf(iterator.next()) );                    
                break;
            default:
                break;
            }
        }            
    }
    private void createAtomicPANCF(Pan p){
        //adding PAN attributes 
        String cf;        
        cf = p.getName();  
        boolean flag = true;
            
        for (Map.Entry<String,String> it : p.getAttribte().entrySet()) {
            String key = cf+"_"+it.getKey();    
            String change_type = it.getValue();             
            if (change_type.equals("no_update")){                 
                List.put(key, "map<time,text>");
                flag= false;                
                Col[len++] = cf +":"+key;
                Col[len++] = cf +":"+key+"_Time";
            }
            else{
                List.put(key, "text");                  
                Col[len++] = cf +":"+key;
            }
            
        }
        if(flag) 
            Colfamily[familylen++] = cf;
        else
            Colfamily[familylen++] = "{NAME=> '"+cf +"', VERSIONS=>10}";
        List.put(cf+"_Sur_Key", "Bigint"); 
        Col[len++] = cf +":"+cf+"_Sur_Key";
    } 
    private void createSpecializedPANCF_Case1(Pan node, MultiLevelPan mlp){
        createAtomicPANCF(node);
        createForChild(node, mlp);
    }
    private void createSpecializedPANCF_Case2(Pan node, MultiLevelPan mlp){
        createAtomicPANCF(node);
        createForParent(node, mlp);    
        createForChild(node, mlp);
    }
    
    private void createForParent(Pan node, MultiLevelPan mlp) {
        Pan parent = mlp.getPanParent(node);
        String cf = parent.getName();
            if (parent.getAttribte().isEmpty()){    
                List.put(cf+"_"+node.getName(), "Boolean");
                List.put(cf+"_Sur_Key", "Bigint"); 
                Colfamily[familylen++] = cf;
                Col[len++] = cf +":"+node.getName()+"Boolean";     
                Col[len++] = cf +":"+cf+"_Sur_Key";
            }
            else{
                createAtomicPANCF(parent);
            }
        if (!(mlp.getPanParent(parent).getName().isEmpty())) {                           
                createForParent(parent,mlp);
            }
    }
    private void createForChild(Pan node, MultiLevelPan mlp) {
        String rootName = node.getName();
        Collection<Pan> children = mlp.getPantree(node);
        for(Pan i: children){
            String cf = i.getName();
            if (i.getAttribte().isEmpty()){    
                List.put(cf+"_"+rootName, "Boolean");
                List.put(cf+"_Sur_Key", "Bigint"); 
                Colfamily[familylen++] = cf;
                Col[len++] = cf +":"+rootName+"Boolean";     
                Col[len++] = cf +":"+cf+"_Sur_Key";
            }
            else{
                createAtomicPANCF(i);
            }                 
            if (!(mlp.getPantree(i).isEmpty())) {                           
                createForChild(i,mlp);
            }
        }        
    }
    private void createComplexPANCF(Pan node, MultiLevelPan mlp, Boolean applicability) {
        createAtomicPANCF(node);
        if(mlp.getPanComplex(node).getName().isEmpty())
            createForComplex(node,mlp, applicability);
        else
            createForConstituent(node,mlp,applicability);
    }
    private void createForComplex(Pan node, MultiLevelPan mlp, Boolean applicability) {
        Pan cxi = null; 
        if(applicability){
            cxi = mlp.getPanComplex(node);
            createAtomicPANCF(cxi);
        }
        if(mlp.getPanComplex(cxi).getName().isEmpty())
            createForComplex(cxi,mlp, applicability);
        else
            createForConstituent(cxi,mlp, applicability);
    }
    private void createForConstituent(Pan node, MultiLevelPan mlp, Boolean applicability) {
        if(applicability){
            Collection<Pan> constituents = mlp.getPanConstituent(node);
            for (Pan cni : constituents){                
                    createAtomicPANCF(cni);
                    if(!mlp.getPanConstituent(cni).isEmpty())
                        createForConstituent(cni, mlp, applicability);
            }
        }
    }
    private void createContainerPANCF(Pan node, MultiLevelPan mlp, Boolean applicability) {
        createAtomicPANCF(node);
        if(mlp.getPanContainer(node).getName().isEmpty())
            createForContainer(node,mlp, applicability);
        else
            createForContent(node,mlp,applicability);
    }
    private void createForContainer(Pan node, MultiLevelPan mlp, Boolean applicability) {
        Pan cti = null; 
        if(applicability){
            cti = mlp.getPanContainer(node);
            createAtomicPANCF(cti);
        }
        if(mlp.getPanContainer(cti).getName().isEmpty())
            createForContainer(cti,mlp, applicability);
        else
            createForContent(cti,mlp, applicability);
    }
    private void createForContent(Pan node, MultiLevelPan mlp, Boolean applicability) {        
        if(applicability){
            Collection<Pan> contents = mlp.getPanContent(node);
            for (Pan cni : contents){                
                    createAtomicPANCF(cni);
                    if(!mlp.getPanContent(cni).isEmpty())
                        createForContent(cni, mlp, applicability);
            }
        }
    }
    
}
