package online.util;

import online.model.Embedding;

public class Utility {
    public static Embedding parseEmbStr(String embStr){
        String[] embStrings = embStr.split("\\s");
        Embedding emb = new Embedding();
        for(String element : embStrings) {
            emb.add_element(Float.parseFloat(element));
        }
        return emb;
    }
}
