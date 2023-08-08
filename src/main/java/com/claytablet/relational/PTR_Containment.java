package com.claytablet.relational;

import com.google.common.collect.Multimap;
import com.claytablet.tological.MultiLevelPan;
import com.claytablet.tological.Pan;
import java.util.Collection;

/**
 *
 * @author dpkap
 */
public class PTR_Containment extends panToRelations{
    String output="";
    public PTR_Containment(Pan P) {
        super(P);
    }
    public String createContainmentLevel( MultiLevelPan mlp){ 
        output = abc(mlp,this.P);
       return output;
    }
    int count=0;
    public String abc(MultiLevelPan mlp ,Pan pi){     
            Multimap<Pan, Pan> tree = mlp.getPancontainment(); 
            Collection<Pan> children = tree.get(pi);  
            for (Pan i : children){
                if (count==0){                                        
                    if (pi.getAttribte().isEmpty())
                        output = output + "\ncreate table "+ pi.getName()+ "( \n"+ createAddSurrogateKey(pi.getName())+"\t varchar(50)\n);";
                    else
                        output = output + createRelation(pi);
                }
                if (i.getAttribte().isEmpty())
                        output = output + "\ncreate table "+ i.getName()+ "( \n"+ createAddSurrogateKey(i.getName())+"\t varchar(50)\n);";
                else 
                    output = output + "\n\n"+createRelation(i); 
                if(!tree.get(i).isEmpty()) {
                    count=1;                                 
                    output = output + "\n" + "alter table " + i.getName() + " add "+ addColumn(pi.getName()+"_SK")+  " ;";
                    output = output + "\n" + "alter table " + i.getName() + " add foreign key ("+ pi.getName() +  "_SK) references "+ 
                        pi.getName() + "(" + pi.getName() + "_SK);";
                    abc(mlp,i);    
                }
                else{
                    count =1;
                    output = output + "\n" + "alter table " + i.getName() + " add "+ addColumn(pi.getName()+"_SK")+  " ;";
                    output = output + "\n" + "alter table " + i.getName() + " add foreign key ("+ pi.getName() +  "_SK) references "+ 
                        pi.getName() + "(" + pi.getName() + "_SK);";
                }
            } 
        return output;
    }   
    
}
