package com.example.rob.bio_uima.uima;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLInputSource;

public class BioUima {

   private static final String INPUT_FILE = "./data/dna.txt";

   /**
    * The main entry point for the BioUIMA application.
    * 
    * Complete the implementation of the ProteinSequenceAnnotator.
    * There are some helpful comments in that src file to get you
    * started. Don't worry about the biology, you should be able to
    * complete the implementation just from reading the comments. If
    * you need help there is plenty of information on the web about how
    * to translate DNA sequences to protein sequences.
    * 
    * The second annotator should read the protein sequences computed
    * by the first annotator and compute a minimum cost alignment using
    * the Levenshtein distance where the cost for insertion and deletion
    * are 1 and the cost for substitution is 2. Note that this is
    * equivalent to computing the minimum edit distance between two strings.
    * 
    * See: http://en.wikipedia.org/wiki/Levenshtein_distance
    * 
    * The DNA sequences you need to work with are in the 'data' directory.
    * There are 5 DNA sequences in one file. You are to perform the
    * translation and alignment on all possible pairs of sequences.
    * 
    * We would like you to implement this using multi-Sofa, that is
    * you should create multiple views on the CAS to distinguish DNA from
    * protein sequences. You can print results to STDOUT or
    * serialize the CAS. Don't worry about upper/lower case.
    * We'll be impressed if you use the UIMA type syatem, implement your
    * own FlowController and take advantage of other UIMA concepts
    * that are applicable or describe in detail how your application
    * could take advantage of things like UIMA-AS.
    * 
    * If you have trouble with the first annotator, one alternative
    * would be to just compute a simple reversal of the original DNA
    * sequences and store those in a separate CAS view. Perform an alignment
    * on these reversed strings rather than the protein strings.
    * 
    * @param args
    */
   public static void main(String[] args) throws Exception {
      // Create the AE.
      XMLInputSource input = new XMLInputSource("desc/BioDescriptor.xml");
      AnalysisEngineDescription desc = UIMAFramework.getXMLParser().parseAnalysisEngineDescription(input);
      AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(desc);

      // Init CAS.
      JCas jCas = ae.newJCas();
      String dna = readInputFile(INPUT_FILE);
      JCas dnaView = jCas.createView(CasView.DNA);
      dnaView.setDocumentText(dna);

      // Process the CAS.
      ae.process(jCas);

      // Print results.
      JCas alignment = jCas.getView(CasView.ALIGNMENTS);
      String[] seqs = alignment.getDocumentText().split(" ");
      printSeqs(seqs);
   }

   /**
    * Prints the each pair-wise alignment to standard output.
    * 
    * @param seqs
    */
   private static void printSeqs(String[] seqs) {
      int i = 0;
      while (i < seqs.length) {
         System.out.println("=== Alignment " + (i / 2 + 1) + " ===");
         System.out.println(seqs[i++]);
         System.out.println(seqs[i++]);
         System.out.println();
      }
   }

   private static String readInputFile(String fileName) throws IOException {
      FileInputStream fis = null;
      BufferedReader br = null;
      StringBuilder output = new StringBuilder();

      try {
         fis = new FileInputStream(fileName);
         br = new BufferedReader(new InputStreamReader(fis));
         String line = br.readLine();
         while (line != null) {
            if (!(line.equals("") || line.charAt(0) == '#')) {
               output.append(line);
               output.append(" ");
            }
            line = br.readLine();
         }
      }
      finally {
         if (br != null) {
            br.close();
         }
      }

      return output.toString();
   }
}
