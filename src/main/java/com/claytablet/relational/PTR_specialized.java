package com.claytablet.relational;


import com.claytablet.tological.MultiLevelPan;
import com.claytablet.tological.Pan;
import java.util.Collection;

/**
 *
 * @author dpkap
 */
public class PTR_specialized extends panToRelations{
    static int count=1;
    Pan root;
    static String output;
    public PTR_specialized(Pan P) {
        super(P);
        root = P;
    }
    //ADAT - is analyzed by ROOT PAN
    public String createSpecializedLevelCase1(Pan input,  MultiLevelPan mlp){  
        output = createRelation(root);
        output= createForChild(input, mlp);
        return output;
    }   
    //ADAT - is analyzed by intermediate PANs
     public String createSpecializedLevelCase2(Pan input, MultiLevelPan mlp){ 
        if(input.getAttribte().isEmpty())
            output = "create table Dim_"+ input.getName() +" ("+ "\n" 
                    + createAddSurrogateKey(input.getName()) + "\t varchar(50)\n)";                
        else
            output = createRelation(input);
        Pan parent = mlp.getPanParent(input); 
        output= createForParent(parent, input, mlp);  
        output= createForChild(input, mlp);        
        return output;
    }
    //ADAT - is analyzed by ALL PANs
    public String createSpecializedLevelCase3(Pan input, MultiLevelPan mlp){        
         if (count==1)
            output = createRelation(input);
        count=2;
        Collection<Pan> children = mlp.getPantree(input);    
         for(Pan i: children){         
                if (i.getAttribte().isEmpty())
                    output = output + "\nalter table Dim_"+ input.getName()+ " add "+ i.getName()+"\t boolean;";
                else{
                    output = output + "\n" + createRelation(i);
                    output = output + "\n" + "alter table Dim_" + input.getName() + " add "+ addColumn(i.getName()+"_SK")+  " ;";
                    output = output + "\n" + "alter table Dim_" + input.getName() + " add foreign key ("+ i.getName() +  "_SK) references Dim_"+ 
                            i.getName() + "(" + i.getName() + "_SK);";
                }
            if (!(mlp.getPantree(i).isEmpty())) {                   
                createSpecializedLevelCase3(i,mlp);
            }
        }        
       return output;
    }
    private String createForParent(Pan parent, Pan input, MultiLevelPan mlp ){            
        for (; ; )
        {   if (parent == null) return "";        
                if (parent.getAttribte().isEmpty())
                    output = output + "\nalter table Dim_"+ input.getName()+ " add "+ parent.getName()+"\t boolean;";
                else{
                    for(String parentAttr:  parent.getAttribte().keySet())            
                    {
                        output = output + "\nalter table Dim_"+ input.getName()+ " add " +addColumn(parentAttr) +";";
                        if ((parent.getAttribte().get(parentAttr)).equals("no_update")){
                        output = output + "\nalter table Dim_"+ input.getName()+ " add "+ addColumn(parentAttr+"_TMSP")+";";
                        }                        
                    }
                }
                output = output + "\n" + "alter table Dim_" + input.getName() + " add "+ addColumn(parent.getName()+"_SK")+  " ;";  
             Pan newparent = mlp.getPanParent(parent);   
             if (newparent != null){
                 createForParent(newparent, input, mlp);
             }
             else
                return output;
            }          
     }
    private String createForChild(Pan child, MultiLevelPan mlp){
        Collection<Pan> children = mlp.getPantree(child); 
        for(Pan i: children){      
            if (i.getAttribte().isEmpty())                   
                output = output + "\nalter table Dim_"+ root.getName()+ " add "+ i.getName()+"\t boolean;";
                for(String childAttr:  i.getAttribte().keySet())            
                {
                    output = output + "\nalter table Dim_"+ root.getName()+ " add " +addColumn(childAttr) +";";
                    if ((i.getAttribte().get(childAttr)).equals("no_update")){
                        output = output + "\nalter table Dim_"+ root.getName()+ " add "+ addColumn(childAttr+"_TMSP")+";";
                    }
                }
                output = output + "\n" + "alter table Dim_" + root.getName() + " add "+ addColumn(i.getName()+"_SK")+  " ;";                              
            if (!(mlp.getPantree(i).isEmpty())) {                           
                createSpecializedLevelCase1(i,mlp);
            }
        }
        return output;
     }
}
