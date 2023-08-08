package com.claytablet.relational;

import com.google.common.collect.Multimap;
import com.claytablet.tological.MultiLevelPan;
import com.claytablet.tological.Pan;
import java.util.Collection;

/**
 *
 * @author dpkap
 */
public class PTR_specialized extends panToRelations{

    public PTR_specialized(Pan P) {
        super(P);
    }
    public String createSpecializedLevel(int height, MultiLevelPan mlp){        
        String output="";
        Multimap<Pan, Pan> tree = mlp.getPantree();        
         for(Pan p: tree.keySet()){
            output = output + "\n" + createRelation(p);
            Collection<Pan> children = tree.get(p);
            for (Pan i : children){
                output = output + "\n" + createRelation(i);
                if (i.getAttribte().isEmpty())
                    output = output + "\ncreate table "+ i.getName()+ "( \n"+ createAddSurrogateKey(i.getName())+"\t varchar(50)\n);";
                output = output + "\n" + "alter table " + i.getName() + " add "+ addColumn(p.getName()+"_SK")+  " ;";
                output = output + "\n" + "alter table " + i.getName() + " add foreign key ("+ p.getName() +  "_SK) references "+ 
                        p.getName() + "(" + p.getName() + "_SK);";
            }
        }        
       return output;
    }
    
}
