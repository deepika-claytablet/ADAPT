
package com.claytablet.relational;

import com.claytablet.tological.MultiLevelPan;
import com.claytablet.tological.Pan;
import com.google.common.collect.Multimap;
import java.util.Collection;

/**
 *
 * @author dpkap
 */
public class PTR_complex extends panToRelations{
    String output="";
    
    public PTR_complex(Pan P) {
        super(P);
    }
    public String createComplexLevel(MultiLevelPan mlp){  
        output = abc(mlp,this.P);
       return output;
    }
    int count=0;
    public String abc(MultiLevelPan mlp ,Pan pi){     
            Multimap<Pan, Pan> tree = mlp.getPanComplexMlP(); 
            Collection<Pan> children = tree.get(pi);  
            for (Pan i : children){
                if (count==0){                    
                    if (pi.getAttribte().isEmpty())
                        output = output + "\ncreate table "+ pi.getName()+ "( \n"+ createAddSurrogateKey(pi.getName())+"\t varchar(50) PRIMARY KEY\n);";
                    else
                        output = output + createRelation(pi);
                }
                if (i.getAttribte().isEmpty())
                        output = output + "\ncreate table "+ i.getName()+ "( \n"+ createAddSurrogateKey(i.getName())+"\t varchar(50) PRIMARY KEY\n);";
                else 
                    output = output + "\n\n"+createRelation(i);  
                
                if(!tree.get(i).isEmpty()) {
                    count =1;
                    output = output + "\n" + "alter table Dim_" + pi.getName() + " add "+ addColumn(i.getName()+"_SK")+  " ;";
                    output = output + "\n" + "alter table Dim_" + pi.getName() + " add foreign key ("+ i.getName() +  "_SK) references Dim_"+ 
                        i.getName() + "(" + i.getName() + "_SK);";
                    abc(mlp,i);             
                }
                else{
                    count =1;
                    output = output + "\n" + "alter table Dim_" + pi.getName() + " add "+ addColumn(i.getName()+"_SK")+  " ;";
                    output = output + "\n" + "alter table Dim_" + pi.getName() + " add foreign key ("+ i.getName() +  "_SK) references Dim_"+ 
                        i.getName() + "(" + i.getName() + "_SK);";
               
                }
            }  
        return output;
    }
}
