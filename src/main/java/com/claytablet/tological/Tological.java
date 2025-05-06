package com.claytablet.tological;

import columnfamily.atomic;
import columnfamily.complex;
import columnfamily.containment;
import columnfamily.derived;
import columnfamily.mixed;
import columnfamily.specialized;
import com.claytablet.relational.ATR_Containment;
import com.claytablet.relational.PTR_complex;
import com.claytablet.relational.PTR_specialized;
import com.claytablet.relational.adatToRelations;
import com.claytablet.relational.ATR_Derived;
import com.claytablet.relational.ATR_Mixed;
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
import java.util.Collection;
import java.util.HashMap;
/**
 *
 * @author dpkap
 */
public class Tological  {
    static final File folder = new File(".\\PanCSVFiles_Tax");
    static final String PanPanFile = ".//PanPan_Tax.txt";
    static final File Adatfolder = new File(".\\AdatCSVFiles_Tax");
    static final String analysisPropertyFile =".\\caranalyzedby_Tax.txt";
    static final String AdatAdatFile = ".//AdatAdatMultilevel_Tax.txt";
    static final String ObjectList=".//Input_Tax.txt";
    static final String conversionChoiceFile = ".//conversionChoice.txt";
    static final String outputFilePath = ".\\test_tax.txt";
    
    static Pan panArray[] = new Pan[1000];
    static Adat[] adatArray= new Adat[1000];
    static AdatAttributeRelationships [] analysis_property;
    static MultiLevelAdat mla;
    static MultiLevelPan mlp;
    static AdatRelationships ar;
    static AdatRelationships [] arArray;
    static HashMap<Adat, String> mixedlist = new HashMap<>();
    static HashMap<Adat, String> mixedlist_col = new HashMap<>();
    static String output="";
    
    private static Pan readCsvPan(String fileName){        
        String file= fileName;
        String line;
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
                p = new Pan (newObject, null,null);
            else
                p = new Pan (newObject,attr_changeType, null);
        } catch (IOException e) {
        }
        return  p;
    }
    private static Adat readCsvAdat(String fileName){        
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
    private static AdatAttributeRelationships [] readCsvAdatAttributeRelationships(String fileAnalyzed){  
        String fileAnalyzedBy=fileAnalyzed;
        String line;
        String cvsSplitBy = ",";
        AdatAttributeRelationships ar ;
        AdatAttributeRelationships [] alarray = new AdatAttributeRelationships[1000] ;
        try (BufferedReader br = new BufferedReader(new FileReader(fileAnalyzedBy))) {
           int i=0;
            while ((line = br.readLine()) != null) {
                ArrayList<String> key = new ArrayList<>();
                HashMap<ArrayList, Boolean>  isAdditive = new HashMap<>();
                HashMap<ArrayList, String>  cardinality = new HashMap<>();
                HashMap<ArrayList, Boolean>  applicability = new HashMap<>();
                String[] token = line.split(cvsSplitBy);
                if (!key.contains(token[0])){
                    key.add(token[0]);
                }             
                key.add(token[1]);
                if (!key.contains(token[2])){
                    key.add(token[2]);
                }
                isAdditive.put(key,Boolean.valueOf(token[3]));
                cardinality.put(key,token[4]);
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
        try (PrintWriter writer = new PrintWriter(new File(outputFilePath))) {
            writer.write(output);
            writer.close();           

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    private static Multimap<String, String> AdatIsAnalyzedBy(String fileAnalyzed){
        String fileAnalyzedBy=fileAnalyzed;
        String line;
        String cvsSplitBy = ",";
        Multimap<String, String> CSVisAnalyzedBy = ArrayListMultimap.create();
        try (BufferedReader br = new BufferedReader(new FileReader(fileAnalyzedBy))) {
            while ((line = br.readLine()) != null) {
                String[] token = line.split(cvsSplitBy);                
                    CSVisAnalyzedBy.put(token[0], token[2]);
            }
            return CSVisAnalyzedBy;
        }
        catch (IOException e) {
            return null;
        }
    }
    private static void createPan(){
        int i=0;
        for(final File fileEntry : folder.listFiles()){
            panArray[i]=readCsvPan(fileEntry.getPath());
            i++;
        }
        //creating multi-level and specialization PANs
        Multimap<Pan, Pan> Pantree = ArrayListMultimap.create();
        Multimap<Pan, Pan> Pancomplex = ArrayListMultimap.create();
        Multimap<Pan, Pan> Pancontainer = ArrayListMultimap.create();
        String ln;
        try (BufferedReader br = new BufferedReader(new FileReader(PanPanFile))) {
                while ((ln = br.readLine()) != null) {
                String[] token = ln.split(",");
                if(token[0].equalsIgnoreCase("specialization")){
                    String parent = token[1];
                    String child = token[2];
                   
                    for(Pan p:panArray){
                        if(p.name.equalsIgnoreCase(parent)){                           
                            for (Pan c:panArray){
                               if(c.name.equalsIgnoreCase(child)){
                                  Pantree.put(p, c);
                                  break; 
                               }
                            }
                            break;
                        }  
                    }
                }
                else if(token[0].equalsIgnoreCase("complex")){
                    String complex = token[1];
                    String constituent = token[2];
                   
                    for(Pan p:panArray){
                        if(p.name.equalsIgnoreCase(complex)){                           
                            for (Pan c:panArray){
                               if(c.name.equalsIgnoreCase(constituent)){
                                  Pancomplex.put(p, c);
                                  break; 
                               }
                            }
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("containment")){
                    String container = token[1];
                    String content = token[2];
                   
                    for(Pan p:panArray){
                        if(p.name.equalsIgnoreCase(container)){                           
                            for (Pan c:panArray){
                               if(c.name.equalsIgnoreCase(content)){
                                  Pancontainer.put(p, c);
                                  break; 
                               }
                            }
                            break;
                        }
                    }
                }
            }
        }
        catch(FileNotFoundException ffe){
            ffe.printStackTrace();
        }
        catch(IOException ie){
            ie.printStackTrace();
        }
        mlp = new MultiLevelPan(Pantree,Pancomplex, Pancontainer); 
        
    }
    private static void createAdat(){
        int i=0;
        for(final File fileEntry : Adatfolder.listFiles()){
            adatArray[i]=readCsvAdat(fileEntry.getPath());
            i++;
        }     
    
        //create ADAT relationships analysis property of additivity, cardinality, applicability from csv
        analysis_property =readCsvAdatAttributeRelationships(analysisPropertyFile);
        
        //creating multi-level and specialization PANs
        Multimap<Adat, Adat> Adattree = ArrayListMultimap.create();
        Multimap<Adat, Adat> Adatcomplex = ArrayListMultimap.create();
        Multimap<Adat, Adat> Adatcontainer = ArrayListMultimap.create();
        Multimap<Adat, Adat> ADATderived = ArrayListMultimap.create();
        
        String lne;
        try (BufferedReader br = new BufferedReader(new FileReader(AdatAdatFile))) {
                while ((lne = br.readLine()) != null) {
                String[] token = lne.split(",");
                if(token[0].equalsIgnoreCase("specialization")){
                    String parent = token[1];
                    String child = token[2];
                   
                    for(Adat p:adatArray){
                        if(p.name.equalsIgnoreCase(parent)){                           
                            for (Adat c:adatArray){
                               if(c.name.equalsIgnoreCase(child)){
                                  Adattree.put(p, c);
                                  break; 
                               }
                            }
                            break;
                        }  
                    }
                }
                else if(token[0].equalsIgnoreCase("complex")){
                    String complex = token[1];
                    String constituent = token[2];
                   
                    for(Adat p:adatArray){
                        if(p.name.equalsIgnoreCase(complex)){                           
                            for (Adat c:adatArray){
                               if(c.name.equalsIgnoreCase(constituent)){
                                  Adatcomplex.put(p, c);
                                  break; 
                               }
                            }
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("containment")){
                    String container = token[1];
                    String content = token[2];
                   
                    for(Adat p:adatArray){
                        if(p.name.equalsIgnoreCase(container)){                           
                            for (Adat c:adatArray){
                               if(c.name.equalsIgnoreCase(content)){
                                  Adatcontainer.put(p, c);
                                  break; 
                               }
                            }
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("derived")){
                    String derived = token[1];
                    String base = token[2];
                   
                    for(Adat p:adatArray){
                        if(p.name.equalsIgnoreCase(derived)){                           
                            for (Adat c:adatArray){
                               if(c.name.equalsIgnoreCase(base)){
                                  ADATderived.put(p, c);
                                  break; 
                               }
                            }
                            break;
                        }
                    }
                }
            }
        }
        catch(FileNotFoundException ffe){
        }
        catch(IOException ie){
        }
        mla = new MultiLevelAdat(Adattree, Adatcomplex, Adatcontainer, ADATderived);
        
        //dependency between ADATs
        Multimap<Adat, Adat> dependency = ArrayListMultimap.create(); 
        //dependency.put(Purchase, Purchase);
        Multimap<Adat, Pan> isAnalyzedBy = ArrayListMultimap.create();
        Multimap<String, String> CSVisAnalyzedBy = AdatIsAnalyzedBy(analysisPropertyFile);
        for(String key:CSVisAnalyzedBy.keySet() ){
            for(Adat a : adatArray){
                if(key.equalsIgnoreCase(a.getName())){
                    Collection<String> values = CSVisAnalyzedBy.get(key);
                    for(String s : values){
                        for(Pan p:panArray){
                            if(s.equalsIgnoreCase(p.getName())){
                                isAnalyzedBy.put(a, p);
                                break;
                            }
                        }
                    }
                    break;     
                }
            }
        }
       
        ar = new AdatRelationships(dependency, isAnalyzedBy);
        arArray = new AdatRelationships[1];
        arArray[0] = ar;
    }
    private static void forColumnFamily(){
        //create columns
        String colLine;
        try (BufferedReader br = new BufferedReader(new FileReader(ObjectList))) {
            while ((colLine = br.readLine()) != null) {
                String[] token = colLine.split(",");
                if(token[0].equalsIgnoreCase("adat") && token[2].equalsIgnoreCase("atomic")&& token[3].equalsIgnoreCase("notmixed")){
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(token[1])){
                            columnfamily.atomic ca = new atomic();
                            output = output + "\n"+ca.load(a, arArray,analysis_property,mlp);
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("adat") && token[2].contains("derived1")&& token[3].equalsIgnoreCase("notmixed")){
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(token[1])){
                            columnfamily.derived ca = new derived();
                            output = output + "\n"+ca.createForderived1(a, arArray, analysis_property, mlp, mla);
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("adat") && token[2].contains("derived2")&& token[3].equalsIgnoreCase("notmixed")){
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(token[1])){
                            columnfamily.derived ca = new derived();
                            output = output + "\n"+ca.createForderived2(a, arArray, analysis_property, mlp, mla);
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("adat") && token[2].contains("containment")&& token[3].equalsIgnoreCase("notmixed")){
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(token[1])){
                            columnfamily.containment ca = new containment();
                            output = output + "\n"+ca.createForContainer(a, arArray, analysis_property, mlp, mla);
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("adat") && token[2].contains("specialization")&& token[3].equalsIgnoreCase("notmixed")){
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(token[1])){
                            columnfamily.specialized ca = new specialized();
                            output = output + "\n"+ca.createForspecialized(a, arArray, analysis_property, mlp, mla);
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("adat") && token[2].contains("complex")&& token[3].equalsIgnoreCase("notmixed")){
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(token[1])){
                            columnfamily.complex ca = new complex();
                            output = output + "\n"+ca.createForComplex(a, arArray, analysis_property, mlp, mla);
                            break;
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("adat") && token[3].equalsIgnoreCase("mixed")){
                    
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(token[1])){
                            mixedlist_col.put(a, token[2]);                                
                            break;
                        }
                    }
                }
            }
            if(!mixedlist_col.isEmpty()){                
                columnfamily.mixed ca = new mixed();
                output = output + "\n"+ ca.createMixed(mixedlist_col, mla, analysis_property, arArray, mlp);
            } 
        }
            
        
        catch(FileNotFoundException ffe){
            
        }
        catch(IOException ie){
            
        }
    }
    private static void forStarRelational(){
        //Create relations
        
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(ObjectList))) {
            while ((line = br.readLine()) != null) {
                String[] token = line.split(",");
                if(token[0].equalsIgnoreCase("pan")){
                    String panName = token[1];           
                   
                    for(Pan p:panArray){
                        if(p.name.equalsIgnoreCase(panName)){                           
                            if(token[2].equalsIgnoreCase("atomic")){
                               panToRelations pr = new panToRelations(p);
                               output = output + "\n"+ pr.createRelation(p);
                               break;
                            }
                            else if(token[2].equalsIgnoreCase("specialization1")){
                                PTR_specialized pr = new PTR_specialized(p);
                                output = output + "\n"+pr.createSpecializedLevelCase1(p, mlp);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("specialization2")){
                                PTR_specialized pr = new PTR_specialized(p);
                                output = output + "\n"+ pr.createSpecializedLevelCase2(p, mlp);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("specialization3")){
                                PTR_specialized pr = new PTR_specialized(p);
                                output = output + "\n"+pr.createSpecializedLevelCase3(p, mlp);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("containment")){
                                PTR_Containment pr = new PTR_Containment(p);
                                output = output + "\n"+ pr.createContentForContainer(mlp, p, analysis_property);
                                break;
                            }
                            else if(token[2].equalsIgnoreCase("complex")){
                                PTR_complex pr = new PTR_complex(p);
                                output = output + "\n"+ pr.createComplexLevel(mlp);
                                break;
                            }
                        }
                    }
                }
                else if(token[0].equalsIgnoreCase("adat")){
                    String adatName = token[1];
                    for(Adat a:adatArray){
                        if(a.name.equalsIgnoreCase(adatName)){
                            if(token[2].equalsIgnoreCase("atomic")&& token[3].equalsIgnoreCase("notmixed")){
                                adatToRelations arl = new adatToRelations(a);
                                output = output + "\n"+arl.createRelation(a) + arl.createRelationships(a,arArray)
                                        + arl.createAnalysisProperty(a,analysis_property)+ arl.createDependent(a, arArray);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("specialization")&& token[3].equalsIgnoreCase("notmixed")){
                                ATR_specialized arl = new ATR_specialized(a);
                                output = output + "\n"+arl.createSpecializedLevel(mla, analysis_property, arArray);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("containment")&& token[3].equalsIgnoreCase("notmixed")){
                                ATR_Containment arl = new ATR_Containment(a);
                                output = output + "\n"+ arl.createForContent(mla, a, analysis_property, arArray);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("derived1")&& token[3].equalsIgnoreCase("notmixed")){
                                ATR_Derived arl = new ATR_Derived(a);
                                output = output + "\n"+arl.createDerivedCase1(a, mla, analysis_property, arArray);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("derived2")&& token[3].equalsIgnoreCase("notmixed")){
                                ATR_Derived arl = new ATR_Derived(a);
                                output = output + "\n"+arl.createDerivedCase2(a, mla, analysis_property, arArray);
                                 break;
                            }
                            else if(token[2].equalsIgnoreCase("complex1")&& token[3].equalsIgnoreCase("notmixed")){
                                ATR_complex arl = new ATR_complex(a);
                                output = output + "\n"+arl.createComplexCase1(a, mla, analysis_property, arArray);
                                break;
                            }
                            else if(token[2].equalsIgnoreCase("complex2")&& token[3].equalsIgnoreCase("notmixed")){
                                ATR_complex arl = new ATR_complex(a);
                                output = output + "\n"+arl.createComplexCase2(a, mla, analysis_property, arArray);
                                break;
                            }
                            else if(token[3].contentEquals("mixed")){                                
                                mixedlist.put(a, token[2]);                                
                                break;
                            }
                        }
                        
                    }
                }                
            }
            if(!mixedlist.isEmpty()){
                Adat a = new Adat();
                ATR_Mixed arm = new ATR_Mixed(a);
                output = output + "\n"+arm.createMixed(mixedlist, mla, analysis_property, arArray);
            }
        }
        catch(FileNotFoundException ffe){
            
        }
        catch(IOException ie){
            
        }
    }
    
    public static void main(String[] args) {
        //creating Pan from csv (in the form Pan,attribute, change type)
        createPan();        
        //create ADAT from csv (in the form Adat,nature,attribute,data kind)
        createAdat();
        String line;
        String cvsSplitBy = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(conversionChoiceFile))) {
            while ((line = br.readLine()) != null) {
                String[] token = line.split(cvsSplitBy);
                if(token[0].equalsIgnoreCase("starrelational") && token[1].equals("1"))
                    forStarRelational();
                else if(token[0].contains("column") && token[1].equals("1"))
                    forColumnFamily();
            }
            writefile(output);
        }
       catch (IOException e){
           
       }
    }
   
   
}
