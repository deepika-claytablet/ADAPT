package com.claytablet.tological;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;


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
    /*
    public MultiLevelPan(char choice, Pan ... p){
        switch (choice) {
            case 'S' -> this.createSpecialization(p);
            case 'C' -> this.createComplex(p);
            case 'T' -> this.createContainment(p);
            default -> {
            }
        }
    }
    //create specialization
    private void createSpecialization(Pan ... p){
        Multimap<Pan, Pan> Pantree = ArrayListMultimap.create();
        Pan previous = null ;
        boolean flag = true;
        for(Pan input:p){   
            if(flag) {
                previous = input;
                flag=false;
            }
            else{
                 Pantree.put(previous, input);
                 previous = input;
            }
        }
    }
    private void createComplex(Pan ... p){
        Multimap<Pan, Pan> PanComplex = ArrayListMultimap.create();
        Pan previous = null ;
        boolean flag = true;
        for(Pan input:p){   
            if(flag) {
                previous = input;
                flag=false;
            }
            else{
                 PanComplex.put(previous, input);
                 previous = input;
            }
        }       
    }
    private void createContainment(Pan ... p){
        Multimap<Pan, Pan> PanCont = ArrayListMultimap.create();
        Pan previous = null ;
        boolean flag = true;
        for(Pan input:p){   
            if(flag) {
                previous = input;
                flag=false;
            }
            else{
                 PanCont.put(previous, input);
                 previous = input;
            }
        }
    }*/
    public Multimap<Pan,Pan> getPantree(){
        return this.PANtree;
    }
    public Collection<Pan> getPantree(Pan p){
        Multimap<Pan,Pan> tree = this.PANtree;
        Collection<Pan> children = tree.get(p);
        return children;
    }
    public Pan getPanParent(Pan child){
        Multimap<Pan, Pan> tree = this.PANtree;
        Map<Pan, Collection <Pan>> m = tree.asMap();
            for (Map.Entry<Pan, Collection<Pan>> entry : m.entrySet()) {
                Pan key = entry.getKey();
                Collection<Pan> value = entry.getValue();
                if (value.contains(child))
                    return key;
            }
           return null;
    }
    //Complex PANs
    public Multimap<Pan, Pan> getPanComplexMlP (){
        return this.PANcomplex;
    }
    public Collection<Pan> getPanConstituent(Pan complex){
        Multimap<Pan,Pan> constituentmlp = this.PANcomplex;
        Collection<Pan> constituent = constituentmlp.get(complex);
        return constituent;
    }
    public Pan getPanComplex(Pan constituent){
        Multimap<Pan, Pan> constituentmlp = this.PANcomplex;
        Map<Pan, Collection <Pan>> m = constituentmlp.asMap();
            for (Map.Entry<Pan, Collection<Pan>> entry : m.entrySet()) {
                Pan key = entry.getKey();
                Collection<Pan> value = entry.getValue();
                if (value.contains(constituent))
                    return key;
            }
           return null;
    }
    //Container PANs
    public Multimap<Pan, Pan> getPanContainerMLP(){
        return this.PANcontainment;
    }
    public Collection<Pan>  getPanContent(Pan container){
        Multimap<Pan,Pan> containermlp = this.PANcontainment;
        Collection<Pan> content = containermlp.get(container);
        return content;
    }
    public Pan getPanContainer(Pan content){
        Multimap<Pan, Pan> containermlp = this.PANcontainment;
        Map<Pan, Collection <Pan>> m = containermlp.asMap();
            for (Map.Entry<Pan, Collection<Pan>> entry : m.entrySet()) {
                Pan key = entry.getKey();
                Collection<Pan> value = entry.getValue();
                if (value.contains(content))
                    return key;
            }
           return null;
    }
    //Atomic PANs
    public Boolean getAtomic(Pan atomic){
        return !atomic.getName().isEmpty();
    
    }
   /* public  int createTree(){
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
    }*/
}
