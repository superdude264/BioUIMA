package bio.uima;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import bio.core.ProteinSequencer;

/**
 * This annotator should read DNA sequences from the CAS
 * and translate each sequence to protein sequences.
 * 
 * <ul>
 * <li>See: <a href="http://en.wikipedia.org/wiki/DNA_codon_table">DNA Codon Table (Wikipedia)</a></li>
 * <li>See: <a href="http://en.wikipedia.org/wiki/Open_reading_frame">Open Reading Frame (Wikipedia)</a></li>
 * </ul>
 */
public class ProteinSequenceAnnotator extends JCasAnnotator_ImplBase {

   /**
    * Translates each DNA sequence to a protein sequence considering all possible open reading frames and store in a CAS
    * view.
    */
   @Override
   public void process(JCas cas) throws AnalysisEngineProcessException {

      try {
         String[] dnaSeqs = cas.getView(CasView.DNA).getDocumentText().split(" ");
         StringBuilder proteinSeqs = new StringBuilder();

         for (String seq : dnaSeqs) {
            String protein = ProteinSequencer.getInstance().findLongestProteinOrf(seq);
            proteinSeqs.append(protein);
            proteinSeqs.append(" ");
         }

         JCas proteins = cas.createView(CasView.PROTEINS);
         proteins.setDocumentText(proteinSeqs.toString());
      }
      catch (CASException e) {
         e.printStackTrace();
      }
   }
}