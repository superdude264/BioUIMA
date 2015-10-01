package com.example.rob.bio_uima.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Converts a DNA sequence into a protein sequence using the conversions found on the Wikipedia <a
 * href="http://en.wikipedia.org/wiki/DNA_codon_table">DNA codon table</a> article.
 * This implementation works by iterating through the sequence, performing HashMap look-ups to match codons to proteins.
 * 
 * @author Rob
 */
public class ProteinSequencer {

   private static final ProteinSequencer INSTANCE             = new ProteinSequencer();
   private static final int              CODON_LENGTH         = 3;
   private static final String           START_CODON          = "ATG";
   private static final String           STOP_CODON_1         = "TAA";
   private static final String           STOP_CODON_2         = "TGA";
   private static final String           STOP_CODON_3         = "TAG";
   private static final Pattern          DNA_SEQUENCE_PATTERN = Pattern.compile("\\b[ACGT]+");

   private final Map<String, String>     codonTable           = buildCodonTable();

   public static ProteinSequencer getInstance() {
      return INSTANCE;
   }

   private static Map<String, String> buildCodonTable() {
      Map<String, String> table = new HashMap<String, String>();
      // Standard codons.
      // TODO: Move to file.
      table.put("GCA", "A");
      table.put("GCC", "A");
      table.put("GCG", "A");
      table.put("GCT", "A");
      table.put("TGC", "C");
      table.put("TGT", "C");
      table.put("GAC", "D");
      table.put("GAT", "D");
      table.put("GAA", "E");
      table.put("GAG", "E");
      table.put("TTC", "F");
      table.put("TTT", "F");
      table.put("GGA", "G");
      table.put("GGC", "G");
      table.put("GGG", "G");
      table.put("GGT", "G");
      table.put("CAC", "H");
      table.put("CAT", "H");
      table.put("ATA", "I");
      table.put("ATC", "I");
      table.put("ATT", "I");
      table.put("AAA", "K");
      table.put("AAG", "K");
      table.put("CTA", "L");
      table.put("CTC", "L");
      table.put("CTG", "L");
      table.put("CTT", "L");
      table.put("TTA", "L");
      table.put("TTG", "L");
      table.put("ATG", "M"); // Start codon.
      table.put("AAC", "N");
      table.put("AAT", "N");
      table.put("CCA", "P");
      table.put("CCC", "P");
      table.put("CCG", "P");
      table.put("CCT", "P");
      table.put("CAA", "Q");
      table.put("CAG", "Q");
      table.put("AGA", "R");
      table.put("AGG", "R");
      table.put("CGA", "R");
      table.put("CGC", "R");
      table.put("CGG", "R");
      table.put("CGT", "R");
      table.put("AGC", "S");
      table.put("AGT", "S");
      table.put("TCA", "S");
      table.put("TCC", "S");
      table.put("TCG", "S");
      table.put("TCT", "S");
      table.put("ACA", "T");
      table.put("ACC", "T");
      table.put("ACG", "T");
      table.put("ACT", "T");
      table.put("GTA", "V");
      table.put("GTC", "V");
      table.put("GTG", "V");
      table.put("GTT", "V");
      table.put("TGG", "W");
      table.put("TAC", "Y");
      table.put("TAT", "Y");
      // Stop codons (Do not have a value).
      table.put(STOP_CODON_1, "");
      table.put(STOP_CODON_2, "");
      table.put(STOP_CODON_3, "");
      return table;
   }

   private ProteinSequencer() {
      // Do nothing.
   }

   /**
    * Converts a DNA sequences into a protein sequence.
    * 
    * @param dnaSeq A non-null, upper-case string that is a multiple of the condon size (3) and contains only valid
    *           nucleotide characters (A, C, G, T).
    * @return The corresponding protein sequence as a string.
    */
   public String findLongestProteinOrf(String dnaSeq) { // TODO: Other strand.
      dnaSeq = checkAndClean(dnaSeq);
      List<String> orfs = new ArrayList<String>();
      orfs.add(getProteinOrfs(dnaSeq)); // Frame 1.
      orfs.add(getProteinOrfs(dnaSeq.substring(1))); // Frame 2.
      orfs.add(getProteinOrfs(dnaSeq.substring(2))); // Frame 3.
      return findLongest(orfs);
   }

   /**
    * 
    * @param dnaSeq A DNA sequence.
    * @return The longest open-reading frame of the protein sequence generated by the given DNA sequence.
    */
   private String getProteinOrfs(String dnaSeq) {
      // Split into groups of three w/ possible leftovers in final group.
      String[] codons = dnaSeq.split("(?<=\\G.{" + CODON_LENGTH + "})");
      List<String> orfs = new ArrayList<String>();
      StringBuilder orfBuf = new StringBuilder();
      boolean inFrame = false;

      for (String codon : codons) {
         if (codon.length() != CODON_LENGTH) {
            // Leftover characters from the grouping can be ignored.
            continue;
         }
         else if (isStartCodon(codon)) {
            // Add start codon & open the frame.
            String protein = codonTable.get(codon);
            orfBuf.append(protein);
            inFrame = true;
         }
         else if (inFrame && isStopCodon(codon)) {
            // Clear the buffer, & close the frame.
            orfBuf.setLength(0);
            inFrame = false;
         }
         else if (inFrame) {
            // Add value to the ORF.
            String protein = codonTable.get(codon);
            orfBuf.append(protein);
         }
      }

      // Add remaining codons in ORF buffer to ORF list.
      orfs.add(orfBuf.toString());

      return findLongest(orfs);
   }

   /**
    * 
    * @param strCol A list of strings.
    * @return The longest string in a list of strings.
    */
   private String findLongest(List<String> strCol) {
      String longestStr = "";
      for (String str : strCol) {
         // TODO: ORF length ties?
         if (str.length() > longestStr.length()) {
            longestStr = str;
         }
      }

      return longestStr;
   }

   /**
    * 
    * @param codon
    * @return True if the codon is a valid start codon (ATG).
    */
   private boolean isStartCodon(String codon) {
      return (START_CODON.equals(codon));
   }

   /**
    * 
    * @param codon
    * @return True if the codon is a valid stop codon (TAA, TGA, TAG).
    */
   private boolean isStopCodon(String codon) {
      return (STOP_CODON_1.equals(codon) || STOP_CODON_2.equals(codon) || STOP_CODON_3.equals(codon));
   }

   /**
    * Ensures the DNA sequence exists in all caps with no padding and contains only valid nucleotide characters (A, C,
    * G, T).
    * 
    * @param dnaSeq The sequence string.
    * @return The sequence string in proper format or an exception.
    */
   private String checkAndClean(String dnaSeq) {
      if (dnaSeq == null || dnaSeq.isEmpty()) {
         throw new IllegalArgumentException("DNA sequence string must not be null or empty.");
      }

      dnaSeq = dnaSeq.toUpperCase().trim();

      if (!DNA_SEQUENCE_PATTERN.matcher(dnaSeq).matches()) {
         throw new IllegalArgumentException("DNA sequence contains invalid characters.  Sequences should only contain 'A', 'C', 'G', & 'T'.");
      }

      return dnaSeq;
   }

}
