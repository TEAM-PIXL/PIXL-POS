//package teampixl.com.pixlpos;
//
//import edu.stanford.nlp.pipeline.*;
//import edu.stanford.nlp.ling.CoreAnnotations;
//import edu.stanford.nlp.util.CoreMap;
//
//import java.util.List;
//import java.util.Properties;
//import java.util.stream.Collectors;
//
//public class CoreNLPTest {
//    public static void main(String[] args) {
//        // Initialize StanfordCoreNLP with properties
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
//        props.setProperty("ner.useSUTime", "true");
//
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//        // Sample text
//        String text = "What was today's revenue compared to yesterday's?";
//
//        // Create an Annotation object
//        Annotation document = new Annotation(text);
//
//        // Annotate the document
//        pipeline.annotate(document);
//
//        // Extract sentences
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        for (CoreMap sentence : sentences) {
//            // Correct way to get sentence text
//            String sentenceText = sentence.get(CoreAnnotations.TextAnnotation.class);
//            System.out.println("Sentence: " + sentenceText);
//
//            // Extract POS tags
//            List<String> posTags = sentence.get(CoreAnnotations.TokensAnnotation.class)
//                    .stream()
//                    .map(token -> token.get(CoreAnnotations.PartOfSpeechAnnotation.class))
//                    .collect(Collectors.toList());
//            System.out.println("POS Tags: " + posTags);
//
//            // Extract NER tags
//            List<String> nerTags = sentence.get(CoreAnnotations.TokensAnnotation.class)
//                    .stream()
//                    .map(token -> token.get(CoreAnnotations.NamedEntityTagAnnotation.class))
//                    .collect(Collectors.toList());
//            System.out.println("NER Tags: " + nerTags);
//        }
//    }
//}

