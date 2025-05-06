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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author dpkap
 */
public class mixed {
    HashMap<Adat, Integer> Adatpriority = new HashMap<>() ;
    String output_c = "";
    String output_h="";
    String output="";
    String [] Hbase_col;
    String[] Colfamily;
    Map<String, String> CassandraList;
    String [] Hbase_meta;
    Map<String, String> CassandraMeta;
    static int len=0; 
    static int metalen=0;
    static int familylen=0;
    public String createMixed(HashMap<Adat, String> mixedlist, MultiLevelAdat mla , AdatAttributeRelationships [] analysis_property, AdatRelationships [] arArray, MultiLevelPan mlp){     
        int priority = -1;
        Multimap<Adat, Adat> tree = mla.getAdatcomplex(); 
        for (Map.Entry<Adat, String> entry : mixedlist.entrySet()){
            if(entry.getValue().contains("complex")){                
                Collection<Adat> children = tree.get(entry.getKey());
                ++priority;
                if(Adatpriority.containsKey(entry.getKey())){
                    int localPriority = Adatpriority.get(entry.getKey());
                    // Adatpriority.replace(entry.getKey(), localPriority, localPriority+1);
                    for(Map.Entry<Adat, Integer> e : Adatpriority.entrySet()){
                       if( e.getValue()==localPriority+1 || e.getValue()==localPriority)
                            Adatpriority.replace(e.getKey(), e.getValue(), e.getValue()+1);
                    }
                    priority = localPriority-1;
                }
                for(Adat i:children){
                    Adatpriority.put(i, priority );
                }
                Adatpriority.put(entry.getKey(),  ++priority);                
            }
        }
        HashMap <Adat, Integer> sortedAdatpriority = Adatpriority.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1,e2)->e1, LinkedHashMap::new));
        
        for(Map.Entry<Adat, Integer> me : sortedAdatpriority.entrySet()){
            Adat a = me.getKey();            
            switch (mixedlist.get(a)){
                case "atomic": 
                {   columnfamily.atomic ca = new atomic();
                    output = output + "\n" + ca.load(a, arArray,analysis_property,mlp);       
                    break;
                }
                case "specialization":
                {
                    columnfamily.specialized ca = new specialized();
                    output = output + "\n" + ca.createForspecialized(a, arArray, analysis_property, mlp, mla);
                    break;
                }
                case "containment": {
                    columnfamily.containment ca = new containment();
                    output = output + "\n" + ca.createForContainer(a, arArray, analysis_property, mlp, mla);
                    break;
                }
                case "derived1":
                {
                    columnfamily.derived ca = new derived();
                    output = output + "\n" + ca.createForderived1(a, arArray, analysis_property, mlp, mla);
                    break;
                }
                case "derived2":
                {
                    columnfamily.derived ca = new derived();
                    output = output + "\n" + ca.createForderived2(a, arArray, analysis_property, mlp, mla);
                    break;
                }                
                case "complex":
                case "complex2":
                case "complex1":
                {
                    this.CassandraList = new HashMap<>();
                    this.CassandraMeta = new HashMap<>();
                    this.Hbase_col = new String[1000];
                    this.Colfamily = new String[1000];
                    this.Hbase_meta = new String[500];
                    this.Colfamily[familylen++] = a.getName();
                    for (Map.Entry<String,String> it : a.getAttr_DataKind().entrySet()) {
                        String attr = it.getKey();
                        String dataKind = it.getValue();
                        this.CassandraList.put(attr, dataKind);             
                        this.Hbase_col[len++] = a.getName() +":"+attr;                        
                    }
                    addPans(a, arArray, analysis_property, mlp);
                    Collection<Adat> children = tree.get(a);
                    String childAdatKey;
                    if(!children.isEmpty())
                        this.Colfamily[familylen++] = "Base_Attributes";
                    for(Adat child: children){                        
                        for (Map.Entry<String,String> itc : child.getAttr_DataKind().entrySet()) {
                            String child_attr = itc.getKey();
                            String child_dataKind = itc.getValue();
                            this.CassandraList.put(child_attr,child_dataKind);             
                            this.Hbase_col[len++] = "Base_Attributes:"+child.getName() + "_"+child_attr;
                        }
                    }
                    int size =this.CassandraList.size() ;
                    int metasize = this.CassandraMeta.size();
                    output o = new output();
                    output_c = output_c + "\n"+ o.Cassandra_output(CassandraList, a, CassandraMeta, size, metasize);
                    output_h = output_h + "\n"+ o.HBase_output(Hbase_col, Colfamily, Hbase_meta, a);
                    output = output + "\n"+output_c+output_h;
                    break;
                }
            }
        }
        return output;
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
