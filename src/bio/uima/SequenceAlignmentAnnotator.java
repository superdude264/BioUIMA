package bio.uima;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;

import bio.core.SequenceAligner;

public class SequenceAlignmentAnnotator extends JCasAnnotator_ImplBase {

   private final SequenceAligner sa = new SequenceAligner(-2, -2, 2, -1);

   @Override
   public void process(JCas cas) throws AnalysisEngineProcessException {
      try {
         String[] proteinSeqs = cas.getView(CasView.PROTEINS).getDocumentText().split(" ");
         StringBuilder alignments = new StringBuilder();

         for (String seq1 : proteinSeqs) {
            for (String seq2 : proteinSeqs) {
               if (!seq1.equals(seq2)) {
                  String[] alignment = sa.computeAlignments(seq1, seq2);
                  alignments.append(alignment[0]).append(" ").append(alignment[1]).append(" ");
               }
            }
         }

         JCas alignmentCas = cas.createView(CasView.ALIGNMENTS);
         alignmentCas.setDocumentText(alignments.toString());
      }
      catch (CASException e) {
         e.printStackTrace();
      }
   }
}