
package columnfamily;

import com.claytablet.tological.Adat;
import com.claytablet.tological.AdatAttributeRelationships;
import com.claytablet.tological.AdatRelationships;
import com.claytablet.tological.MultiLevelPan;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author dpkap
 */
public class atomic {
    String output_c="";
    String output_h="";
    String [] Hbase_col;
    String[] Colfamily;
    Map<String, String> CassandraList;
    String [] Hbase_meta;
    Map<String, String> CassandraMeta;
    
    static int len=0; 
    static int metalen =0;
    static int familylen=0;
    public String load(Adat a, AdatRelationships [] ar, AdatAttributeRelationships [] mla, MultiLevelPan mlp){
         
        String cf = a.getName();
        this.CassandraList = new HashMap<>();
        this.CassandraMeta = new HashMap<>();
        this.Hbase_col = new String[1000];
        this.Hbase_meta = new String[2000];
        this.Colfamily = new String[2000];
        
        for (Map.Entry<String,String> it : a.getAttr_DataKind().entrySet()) {
            String attr = it.getKey();
            String dataKind = it.getValue();
            this.CassandraList.put(attr, dataKind);             
            this.Hbase_col[len++] = cf +":"+attr;  
            this.Colfamily[familylen++] = cf;
        }
        //ADAT key
        CassandraList.put(cf+"_Key", "Bigint");
        Hbase_col[len++] = cf +":"+cf+"_Key";
        
        addPans(a, ar, mla, mlp);
        
        //for output
        int size =this.CassandraList.size() ;
        int metasize = this.CassandraMeta.size();
        output o = new output();
        output_c = output_c + "\n"+ o.Cassandra_output(CassandraList, a, CassandraMeta, size, metasize);
        output_h = output_h + "\n"+ o.HBase_output(Hbase_col, Colfamily,Hbase_meta, a);
        return output_c + output_h;
    }
    private void addPans(Adat a, AdatRelationships[] ar, AdatAttributeRelationships[] adatAtrRel, MultiLevelPan mlp) {
        for (Map.Entry<String,String> it : a.getAttr_DataKind().entrySet()) {
            String attr = it.getKey();
            PanColumn pc = new PanColumn();
            pc.convertPan(a, ar, adatAtrRel, mlp, attr);
            CassandraList.putAll(pc.List);
            CassandraMeta.putAll(pc.ListMeta);
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
