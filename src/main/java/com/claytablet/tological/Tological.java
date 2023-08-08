package com.claytablet.tological;

import com.claytablet.relational.ATR_Containment;
import com.claytablet.relational.PTR_complex;
import com.claytablet.relational.PTR_specialized;
import com.claytablet.relational.adatToRelations;
import com.claytablet.relational.ATR_Derived;
import com.claytablet.relational.ATR_complex;
import com.claytablet.relational.ATR_specialized;
import com.claytablet.relational.PTR_Containment;
import com.claytablet.relational.panToRelations;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import java.util.HashMap;
/**
 *
 * @author dpkap
 */
public class Tological  {
    
    public static Pan readCsvPan(String fileName){        
        String file= fileName;
        String line = "";
        String cvsSplitBy = ",";
        Pan p = null;
        String newObject="";
        HashMap<String, String>  attr_changeType = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] token = line.split(cvsSplitBy);
                if(token.length==1){
                    newObject = token[0];                    
                    break;
                }
                else
                    attr_changeType.put(token[1], token[2]);
                newObject = token[0];
            }
            if (attr_changeType.isEmpty())
                p = new Pan (newObject, null);
            else
                p = new Pan (newObject,attr_changeType);
        } catch (IOException e) {
        }
        return  p;
    }
    public static Adat readCsvAdat(String fileName){        
        String file= fileName;         
        String line = "";
        String cvsSplitBy = ",";
        Adat a = null;
        String newObject="";
        String nature="";
        HashMap<String, String>  attr_dataKind = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                String[] token = line.split(cvsSplitBy);
                attr_dataKind.put(token[2], token[3]);
                newObject = token[0];
                nature = token[1];
            }
            a = new Adat (newObject,nature,attr_dataKind);
        } catch (IOException e) {
            e.printStackTrace();
        }               
        return  a;
    }
    public static AdatAttributeRelationships [] readCsvAdatAttributeRelationships(String fileAnalyzed){  
        String fileAnalyzedBy=fileAnalyzed;
        String line;
        String cvsSplitBy = ",";
        AdatAttributeRelationships ar ;
        AdatAttributeRelationships [] alarray = new AdatAttributeRelationships[10] ;
        try (BufferedReader br = new BufferedReader(new FileReader(fileAnalyzedBy))) {
           int i=0;
            while ((line = br.readLine()) != null) {
                ArrayList<String> key = new ArrayList<>();
                HashMap<ArrayList, Boolean>  isAdditive = new HashMap<>();
                HashMap<ArrayList, Integer>  cardinality = new HashMap<>();
                HashMap<ArrayList, Boolean>  applicability = new HashMap<>();
                String[] token = line.split(cvsSplitBy);
                if (!key.contains(token[0])){
                    key.add(token[0]);
                }
                if (!key.contains(token[1])){                    
                    key.add(token[1]);                    
                }
                if (!key.contains(token[2])){
                    key.add(token[2]);
                }
                isAdditive.put(key,Boolean.valueOf(token[3]));
                
                if (token[4].equals("many")){
                    cardinality.put(key,99999);
                }
                else
                    cardinality.put(key,Integer.valueOf(token[4]));
                applicability.put(key,Boolean.valueOf(token[5]));    
               
                ar = new AdatAttributeRelationships(isAdditive, cardinality, applicability);
                alarray [i] = ar;
                i++;
            }            
        } catch (IOException e) {
        }  
        return  alarray;
    }    
    public static void writefile(String output){
        try (PrintWriter writer = new PrintWriter(new File(".\\test.txt"))) {
            writer.write(output);
            writer.close();           

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        //creating Pan from csv (in the form Pan,attribute, change type)
        Pan Product = readCsvPan(".\\Product.txt");       
        //Creating Specialized PANs from csv
        Pan PPProduct = readCsvPan(".\\Perishable.txt");
        Pan NPProduct = readCsvPan(".\\NonPerishable.txt");
        Pan level2_PPProduct = readCsvPan(".\\Dairy_Perishable.txt");
        Pan level2_PPProduct_1 = readCsvPan(".\\Fruit_Perishable.txt");
        
        //creating specialization
        Multimap<Pan, Pan> Pantree = ArrayListMultimap.create();
        Pantree.put(Product, PPProduct);
        Pantree.put(Product, NPProduct);
        Pantree.put(PPProduct, level2_PPProduct);
        Pantree.put(PPProduct, level2_PPProduct_1);
        MultiLevelPan mlp = new MultiLevelPan(null,Pantree, null);        
          
        //create ADAT from csv (in the form Adat,nature,attribute,data kind)
        Adat Purchase = readCsvAdat(".\\Purchase.txt");
        Adat IGST = readCsvAdat(".\\IGST.txt");
        Adat TotalValue = readCsvAdat(".\\TotalValue.txt");
        Adat CIF = readCsvAdat(".\\CIF.txt");
        Adat BCD = readCsvAdat(".\\BCD.txt");
        
        //create ADAT relationships analysis property of additivity, cardinality, applicability from csv
        AdatAttributeRelationships [] aar =readCsvAdatAttributeRelationships(".\\analyzedBy.txt");
       
        //create is-analyzed by property
        Multimap<Adat, Pan> Analyzed = ArrayListMultimap.create();
        Analyzed.put(Purchase, Product);  
       
        //dependency between ADATs
        Multimap<Adat, Adat> dependency = ArrayListMultimap.create(); 
        dependency.put(Purchase, Purchase);
        AdatRelationships ar = new AdatRelationships(dependency, Analyzed);
        AdatRelationships [] arArray = new AdatRelationships[1];
        arArray[0] = ar;
        
        Multimap<Adat, Adat> ADATderived = ArrayListMultimap.create();
        ADATderived.put(Purchase, IGST);
        ADATderived.put(Purchase, TotalValue);
        ADATderived.put(TotalValue, CIF);
        ADATderived.put(TotalValue, BCD);
        MultiLevelAdat mla = new MultiLevelAdat(null,null,ADATderived,null);
                      
        //Create relations
       //PTR_specialized p = new PTR_specialized(Product);
       PTR_complex p = new PTR_complex(Product);
       // PTR_Containment p = new PTR_Containment(group_companies);
      // PTR_complex 
      // panToRelations d = new panToRelations(Customer);     
       // adatToRelations atr = new adatToRelations(Purchase);
      //  ATR_Derived der = new ATR_Derived(Purchase);
     // ATR_complex der = new ATR_complex(Purchase);
       //  ATR_specialized der = new ATR_specialized(Purchase);
        ATR_Containment der = new ATR_Containment(Purchase);
      writefile(p.createComplexLevel(mlp) + der.createContainmentLevel(mla,aar, arArray));
    }
}
