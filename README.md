# ADAPT: Hierarchical Data Warehouse Model
Data Warehouse Logical Design Tool that converts a Hierarchical Data Warehouse model into star schema and Cassandra and HBase logical schema.
The conversion is based on ADAPT model [Prakash D., Prakash N., A Conceptual Model for Data Warehousing. Proc. ENASE 2024, 87-98, 2024] This meta-model contains:
1. Parameters of Analysis also called PANs
2. Analyzable Data types also called ADATs
3. "is-analyzed-by" relationship properties


Upload the model as CSV files in the following folders/File:
1. Folder containing PAN CSV Files
2. CSV File containing PAN-PAN relationhips
3. Folder contaning ADAT CSV Files
4. File containing analysisProperty of "is-analyzed-by"
5. File containing Adat Adat relationship
6. File containing type of PAN
7. File containing the choice of conversion, whether to star or column store
8. Output path

Reference these folders/File in the corresponding staic variables in the class Tological.
