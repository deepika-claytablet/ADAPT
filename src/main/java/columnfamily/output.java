/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package columnfamily;

import com.claytablet.tological.Adat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 *
 * @author dpkap
 */
public class output {
    String output_c="";
    String output_h="";
    public String HBase_output(String [] HBase_col,String [] HBase_colFam, String [] HBase_meta, Adat a) throws ArrayStoreException{
        List<String> cfSet = new ArrayList<>();
        output_h = output_h + "\n"+ "**HBase**\n create '" + a.getName() + "'" ;
        for (String hc : HBase_colFam){  
            if(hc !=null)
                cfSet.add(hc);            
        }
        Object [] cfSetStream = Arrays.stream(cfSet.toArray()).distinct().toArray();
        
        for(Object cfMeta: cfSetStream){
            if(cfMeta.toString().contains("{"))
                output_h = output_h + ""+ ","+ cfMeta.toString();            
            else              
                output_h = output_h + ""+ ",'"+ cfMeta.toString() + "'";
        }
            
        for (String hc : HBase_col){
            if(hc !=null )                
                output_h = output_h + "\n"+ "put '" + a.getName() + "', rowKeyValue, '"+ hc + "', value";
        }
        //Meta Tables
        List<String> cfMetaSet = new ArrayList<>();
        output_h = output_h + "\n"+ "**HBase**\n create '" + a.getName() + "_Meta'" ;
        for (String hm : HBase_meta){
            if(hm !=null ) {
                String [] tokens = hm.split(":");
                cfMetaSet.add(tokens[0]);                
            }
        }
        Object [] cfMetaSetStream = Arrays.stream(cfMetaSet.toArray()).distinct().toArray();
        for(Object cfMeta: cfMetaSetStream)
            output_h = output_h + ""+ ",'"+ cfMeta.toString() + "'";
        for (String hm : HBase_meta){
            if(hm !=null ) 
                output_h = output_h + "\n"+ "put '"+ a.getName()+"_Meta', rowKeyValue, '" +hm + "', value";
        }
        return output_h;
    }
    public String Cassandra_output(Map<String, String> CassandraList, Adat a, Map<String, String> CassandraMeta, int size, int metasize ){
        Map<String, String > treeList = new TreeMap<>(CassandraList);
        Map<String, String > treeMeta = new TreeMap<>(CassandraMeta);
        output_c = output_c + "\n"+ "**Cassandra**\n CREATE TABLE " + a.getName() + " (\n" ;
        for(Map.Entry<String,String> it: treeList.entrySet() ){
            size--;
            output_c = output_c + it.getKey()+"  "+ it.getValue().replace("String", "text");
            if(size >=1)
                output_c = output_c + "\n"+",";

        }
        output_c = output_c + "\n"+ ",PRIMARY KEY());";
        output_c = output_c + "\n"+ "**Cassandra**\n CREATE TABLE "+a.getName()+ "_Meta (\n" ;
        for(Map.Entry<String,String> it: treeMeta.entrySet() ){
            metasize--;
            output_c = output_c + it.getKey()+"  "
                    + it.getValue().replace("String", "text");
            if(metasize >=1)
                output_c = output_c + "\n"+",";

        }
        output_c = output_c + "\n"+",PRIMARY KEY());";
        return output_c;
    }
}
