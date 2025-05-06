/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package columnfamily;

import com.claytablet.tological.Adat;
import com.claytablet.tological.AdatAttributeRelationships;
import com.claytablet.tological.AdatRelationships;
import com.claytablet.tological.MultiLevelAdat;
import com.claytablet.tological.MultiLevelPan;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dpkap
 */
public class containment {
    String output_c = "";
    String output_h="";
    String [] Hbase_col;
    String[] Colfamily;
    Map<String, String> CassandraList;
    String [] Hbase_meta;
    Map<String, String> CassandraMeta;
    static int len=0; 
    static int metalen =0;
    static int familylen=0;
    int count=0;
    public String createForContainer(Adat a, AdatRelationships [] ar, AdatAttributeRelationships [] adatAtrRel, MultiLevelPan mlp, MultiLevelAdat mla){
         
        this.CassandraList = new HashMap<>();
        this.Hbase_col = new String[1000];
        this.Colfamily = new String[1000];
        this.CassandraMeta = new HashMap<>();
        this.Hbase_meta = new String[1000];
        
        Multimap<Adat, Adat> tree = mla.getAdatcontainment(); 
        String cf = a.getName();
        if(count==0){  
            this.Colfamily[familylen++] = cf;          
            for (Map.Entry<String,String> it : a.getAttr_DataKind().entrySet()) {
                String attr = it.getKey();
                String dataKind = it.getValue();
                this.CassandraList.put(attr, dataKind);             
                this.Hbase_col[len++] = cf +":"+attr;  
                
            }
            addPans(a, ar,adatAtrRel, mlp );
            //ADAT key            
            CassandraList.put(cf+"_Key", "Bigint");
            Hbase_col[len++] = cf +":"+cf+"_Key";
            Colfamily[familylen++] = cf;
            count =1;
        }
        
        Collection<Adat> children = tree.get(a);
        String childAdatKey;
        for(Adat child: children){            
            containment childClass = new containment();
            childClass.CassandraList = new HashMap<>();
            childClass.Hbase_col = new String[1000];
            childClass.Colfamily = new String[1000];
            childClass.CassandraMeta = new HashMap<>();
            childClass.Hbase_meta = new String[1000];
            int childLen = 0;
            childClass.Colfamily[familylen++]= child.getName();
            for (Map.Entry<String,String> it :child.getAttr_DataKind().entrySet()) {
                String attr = it.getKey();
                String dataKind = it.getValue();
                childClass.CassandraList.put(attr, dataKind);             
                childClass.Hbase_col[childLen++] = child.getName() +":"+attr;  
            }
            //add Pans
            childClass.addPans(child, ar,adatAtrRel, mlp );
            //add Container Pans
            childClass.addPans(a, ar,adatAtrRel, mlp);
            //ADAT key
            String child_cf = child.getName();
            childAdatKey = child_cf+"_Key";
            childClass.CassandraList.put(childAdatKey, "Bigint");
            childClass.Hbase_col[childLen++] = child_cf +":"+child_cf+"_Key";          
            childClass.Colfamily[familylen++]= child_cf;
            
             //for output
            int childsize =childClass.CassandraList.size() ;
            int childmetasize = childClass.CassandraMeta.size();
            output child_o = new output();
            output_c = output_c + "\n"+ child_o.Cassandra_output(childClass.CassandraList, child, childClass.CassandraMeta, childsize, childmetasize);
            output_h =  output_h +"\n"+ child_o.HBase_output(childClass.Hbase_col, childClass.Colfamily, childClass.Hbase_meta, child);
            
            if (!(tree.get(child).isEmpty())){
                createForContainer(child, ar, adatAtrRel, mlp, mla); 
                childClass.CassandraList.put(childAdatKey, "Bigint");
            }
        }
               
       //for output
        int size =this.CassandraList.size() ;
        int metasize = this.CassandraMeta.size();
        output o = new output();
        output_c = output_c + "\n"+ o.Cassandra_output(CassandraList, a, CassandraMeta, size, metasize);
        output_h = output_h + "\n"+ o.HBase_output(Hbase_col, Colfamily, Hbase_meta, a);
        return output_c + output_h;
    }
    
     private void addPans(Adat a, AdatRelationships[] ar, AdatAttributeRelationships[] adatAtrRel, MultiLevelPan mlp) {
        for (Map.Entry<String,String> it : a.getAttr_DataKind().entrySet()) {
            String attr = it.getKey();
            PanColumn pc = new PanColumn();
            pc.convertPan(a, ar, adatAtrRel, mlp, attr);
            this.CassandraList.putAll(pc.List);
            this.CassandraMeta.putAll(pc.ListMeta);
            for (String Hbase_col_i : pc.Col) {
                if(Hbase_col_i != null)
                    Hbase_col[len++] = Hbase_col_i;
            }
            for (String Hbase_colfmaily_i : pc.Colfamily) {
                if(Hbase_colfmaily_i != null)
                    Colfamily[familylen++] = Hbase_colfmaily_i;
            }
            for (String Hbase_colMeta_i : pc.ColMeta) {
                if(Hbase_colMeta_i != null)
                    Hbase_meta[metalen++] = Hbase_colMeta_i;
            }
        }
    }
    
}
