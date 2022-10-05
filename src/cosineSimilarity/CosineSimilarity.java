import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class CosineSimilarity {
    private final ArrayList<ArrayList<String>> docs;
    private final Map<Integer, String> docsName;
    private final ArrayList<String> docsList;
    CosineSimilarity() {
        docs = new ArrayList<>();
        docsName = new HashMap<>();
        docsList = new ArrayList<>();
    }
//here we build vectors for documents
    private void buildVectors(String[] documents) {
        for (int i = 0; i < documents.length; i++) {
            try {
                String file;
                ArrayList<String> document = null;
                docsName.put(i, documents[i].substring(6, 9));
                docsList.add(documents[i].substring(6, 9));
                if ((file = Files.readString(Paths.get(documents[i]))) != null) {
                    document = new ArrayList<>(Arrays.asList(file.split("\\W+")));
                    for (int j = 0; j < document.size(); j++) {
                        document.set(j, document.get(j).toLowerCase());
                    }
                }
                docs.add(document);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ArrayList<String> Construct_Table() {
        HashSet<String> Table_tmp= new HashSet<>();
        for (ArrayList<String> doc : docs)
            Table_tmp.addAll(doc);

        ArrayList<String> table = new ArrayList<>(Table_tmp);
        Collections.sort(table);
        return table;
    }

    private HashMap<String, ArrayList<Integer>>TermFreq_Vector(ArrayList<String> table) {
        HashMap<String, ArrayList<Integer>> TF_Vector = new HashMap<>();
        for (int i = 0; i < docs.size(); i++) {
            ArrayList<Integer> freqs = new ArrayList<>();
            for (String word : table) {
                int counter = Collections.frequency(docs.get(i), word);
                freqs.add(counter);
            }
            TF_Vector.put(docsName.get(i), freqs);
        }
        return TF_Vector;
    }
    private double Get_Magnitude(ArrayList<Integer> Freqs) {

        double Mag = 0.0;
        for (int i : Freqs)
            Mag = Mag+ Math.pow(i, 2);
        //then we get the sqrt for the magnitude
        Mag = Math.sqrt(Mag);
        return Mag;
    }

    private HashMap<String, Double> GetDotProduct_BetweenDocToOthers(String documentName, HashMap<String, ArrayList<Integer>> TermFreq_Vector) {
        ArrayList<Integer> docFreqs = TermFreq_Vector.get(documentName);
        HashMap<String, Double> Products = new HashMap<>();
        for (String doc : TermFreq_Vector.keySet()) {
            if (docsList.indexOf(doc) > docsList.indexOf(documentName)) {
                double dot_Product = CalcDotProduct_between_2vectors(docFreqs, TermFreq_Vector.get(doc));
                Products.put((documentName + " and " + doc + " = "), dot_Product);
            }
        }
        return Products;
    }

    private double CalcDotProduct_between_2vectors(ArrayList<Integer> vector1, ArrayList<Integer> vector2) {
        double V1V2 = 0.0;
        for (int i = 0; i < vector1.size(); i++)
            V1V2 = V1V2+ (vector1.get(i) * vector2.get(i));

        return V1V2;
    }

    private HashMap<String, Double> Calc_Cosine_Simlarity(HashMap<String, Double> dop, HashMap<String, Double> magnitudes) {
        HashMap<String, Double> Cosine_Similarities = new HashMap<>();
        for (String product : dop.keySet()) {
            String[]split_Names = product.split(" ");
            double ans = dop.get(product) / ((double)magnitudes.get(split_Names[0]) * magnitudes.get(split_Names[2]));
            Cosine_Similarities.put(product, ans);
        }
        return Cosine_Similarities;
    }
    HashMap<String, Double>Get_Cosine_Similarity(String[] documents)
    {
        buildVectors(documents);
        ArrayList<String> table= Construct_Table();

        HashMap<String, ArrayList<Integer>> TF_Vector=TermFreq_Vector(table);
        HashMap<String, Double> Products = new HashMap<>();
        HashMap<String, Double> Mags = new HashMap<>();
        for(String name : TF_Vector.keySet())
        {
            Products.putAll(GetDotProduct_BetweenDocToOthers(name, TF_Vector));
            Mags.put(name, Get_Magnitude(TF_Vector.get(name)));
        }

        HashMap<String, Double> Cosine_Similarities = Calc_Cosine_Simlarity(Products, Mags);
        return sort_Descendly(Cosine_Similarities);

    }
    private HashMap<String, Double> sort_Descendly(HashMap<String, Double> Cosine_similarities) {
        List<Map.Entry<String, Double>> Similarities_list = new LinkedList<>(Cosine_similarities.entrySet());
        Collections.sort(Similarities_list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        HashMap<String, Double> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Double> s : Similarities_list) {
            temp.put(s.getKey(), s.getValue());
        }
        return temp;
    }
    public static void main(String args[]){
        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        String[] docs = {
                "files/100.txt",
                "files/101.txt",
                "files/102.txt",
                "files/103.txt"
        };
        HashMap<String, Double> Cosine_Similarities = cosineSimilarity.Get_Cosine_Similarity(docs);

        System.out.println("Cosine Similarities: ");
        for(String similarity : Cosine_Similarities.keySet())
        {
            System.out.println(similarity + " " + Cosine_Similarities.get(similarity).toString());

        }
        System.out.println("----------------------------------");
    }
}
