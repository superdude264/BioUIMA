package bio.core;

/**
 * Computes the minimum cost for alignment between two strings using the costs for each operation outlined in the
 * constructor. Currently all operations have a fixed integer cost. The alignments are computed using Hirschberg's
 * algorithm, which runs in O(nm) time and requires O(min{n,m}) space.
 * 
 * <p>
 * <ul>
 * <li>See: <a
 * href="http://en.wikipedia.org/w/index.php?title=Hirschberg%27s_algorithm&oldid=621812465#Algorithm_description"
 * >Hirschberg's algorithm (Wikipedia, 8/26/2014)</a></li>
 * <li>See: <a href=
 * "http://en.wikipedia.org/w/index.php?title=Needleman%E2%80%93Wunsch_algorithm&oldid=622774955#A_modern_presentation"
 * >Needleman-Wunsch algorithm (Wikipedia, 8/26/2014)</a></li>
 * <li>See: <a href="http://www.cs.nthu.edu.tw/~wkhon/algo09/tutorials/tutorial-hirschberg.pdf">Overview of Hirschberg's
 * algorithm</a></li>
 * </ul>
 */
public class SequenceAligner {

   private final int insertionCost;
   private final int deletionCost;
   private final int matchedSubstitutionCost;
   private final int unmatchedSubstitutionCost;

   /**
    * 
    * @param insertionCost A negative integer representing the cost inserting an item in the sequence.
    * @param deletionCost A negative integer representing the cost of deleting an item from the sequence.
    * @param matchedSubstitutionCost An integer representing the cost when no substitution needs to be made (i.e there
    *           is a match; normally positive).
    * @param unmatchedSubstitutionCost A negative integer represent the cost when a substitution needs to be made.
    */
   public SequenceAligner(int insertionCost, int deletionCost, int matchedSubstitutionCost, int unmatchedSubstitutionCost) {
      this.insertionCost = insertionCost;
      this.deletionCost = deletionCost;
      this.matchedSubstitutionCost = matchedSubstitutionCost;
      this.unmatchedSubstitutionCost = unmatchedSubstitutionCost;
   }

   /**
    * Computes the alignment of two text sequences using Hirschberg's algorithm.
    * 
    * @param seq1
    * @param seq2
    * @return
    */
   public String[] computeAlignments(String seq1, String seq2) {
      StringBuilder[] res = hirschberg(seq1, seq2);
      return new String[] { res[0].toString(), res[1].toString() };
   }

   protected StringBuilder[] hirschberg(String sequence1, String sequence2) {
      StringBuilder seqX = new StringBuilder(sequence1);
      StringBuilder seqY = new StringBuilder(sequence2);
      StringBuilder alignmentX = new StringBuilder();
      StringBuilder alignmentY = new StringBuilder();

      if (seqX.length() == 0) {
         for (int i = 0; i < seqY.length(); i++) {
            alignmentX.append('-');
            alignmentY.append(seqY.charAt(i));
         }
      }
      else if (seqY.length() == 0) {
         for (int i = 0; i < seqX.length(); i++) {
            alignmentX.append(seqX.charAt(i));
            alignmentY.append('-');
         }
      }
      else if (seqX.length() == 1 || seqY.length() == 1) {
         StringBuilder[] res = needlemanWunsch(seqX, seqY);
         alignmentX = res[0];
         alignmentY = res[1];
      }
      else {
         int xLen = seqX.length();
         int xMid = xLen / 2;
         int yLen = seqY.length();

         // Find where to split Y.
         int[] leftCost = cost(seqX.substring(0, xMid), seqY); // Check bounds.
         int[] rightCost = cost(reverse(seqX.substring(xMid, xLen)), reverse(seqY)); // Check bounds
         int ySplit = partitionY(leftCost, rightCost, yLen);

         // Find the alignments.
         StringBuilder[] left = hirschberg(seqX.substring(0, xMid), seqY.substring(0, ySplit));
         StringBuilder[] right = hirschberg(seqX.substring(xMid, xLen), seqY.substring(ySplit, yLen));
         alignmentX = left[0].append(right[0]);
         alignmentY = left[1].append(right[1]);
      }

      return new StringBuilder[] { alignmentX, alignmentY };
   }

   /**
    * Returns a position 'p' such that <code>p = arg_max(leftCost + reverse(rightCost))</code>.
    * 
    * @param leftCost
    * @param rightCost
    * @param yLen
    * @return
    */
   private int partitionY(int[] leftCost, int[] rightCost, int yLen) {
      int maxScore = 0;
      int partitionIndex = 0;

      for (int i = 0; i <= yLen; i++) {
         int currentScore = leftCost[i] + rightCost[yLen - i];
         if (currentScore >= maxScore) { // TODO: Deal w/ multiple optimal alignments?
            maxScore = currentScore;
            partitionIndex = i;
         }
      }

      return partitionIndex;
   }

   /**
    * Find the alignments for the given strings.
    * 
    * @param seqX
    * @param seqY
    * @return
    */
   protected StringBuilder[] needlemanWunsch(StringBuilder seqX, StringBuilder seqY) {
      StringBuilder alignmentX = new StringBuilder();
      StringBuilder alignmentY = new StringBuilder();
      int i = seqX.length();
      int j = seqY.length();
      int[][] cost = computeCostMatrix(seqX, seqY);

      while (i > 0 || j > 0) {
         // X_i & Y_j are aligned.
         if (i > 0 && j > 0 && cost[i][j] == cost[i - 1][j - 1] + substitutionCost(seqX.charAt(i - 1), seqY.charAt(j - 1))) {
            // Prepend characters to the front of the alignments.
            alignmentX.insert(0, seqX.charAt(i - 1));
            alignmentY.insert(0, seqY.charAt(j - 1));
            i = i - 1;
            j = j - 1;
         }
         // X_i is aligned with a gap in Y.
         else if (i > 0 && cost[i][j] == cost[i - 1][j] + deletionCost(seqX.charAt(i - 1))) {
            alignmentX.insert(0, seqX.charAt(i - 1));
            alignmentY.insert(0, '-');
            i = i - 1;
         }
         // Y_j is aligned with a gap in X.
         else if (j > 0 && cost[i][j] == cost[i][j - 1] + deletionCost(seqY.charAt(j - 1))) {
            alignmentX.insert(0, '-');
            alignmentY.insert(0, seqY.charAt(j - 1));
            j = j - 1;
         }
      }

      return new StringBuilder[] { alignmentX, alignmentY };
   }

   /**
    * Builds the cost matrix for Needleman-Wunsch
    * 
    * @param seqX
    * @param seqY
    * @return
    */
   protected int[][] computeCostMatrix(CharSequence seqX, CharSequence seqY) {
      int rows = seqX.length() + 1;
      int cols = seqY.length() + 1;
      int[][] score = new int[rows][cols];

      for (int j = 1; j <= seqY.length(); j++) {
         score[0][j] = score[0][j - 1] + insertionCost(seqY.charAt(j - 1));
      }
      for (int i = 1; i <= seqX.length(); i++) {
         score[i][0] = score[i - 1][0] + deletionCost(seqX.charAt(i - 1));
         for (int j = 1; j <= seqY.length(); j++) {
            int costSub = score[i - 1][j - 1] + substitutionCost(seqX.charAt(i - 1), seqY.charAt(j - 1));
            int costDel = score[i - 1][j] + deletionCost(seqX.charAt(i - 1));
            int costIns = score[i][j - 1] + insertionCost(seqY.charAt(j - 1));
            // max(costSub, costDel, costIns)
            score[i][j] = Math.max(Math.max(costSub, costDel), costIns);
         }
      }

      return score;
   }

   /**
    * Returns the last row of Needleman-Wunsch cost matrix.
    * 
    * @param seqX
    * @param seqY
    * @return
    */
   private int[] cost(CharSequence seqX, CharSequence seqY) {
      int[][] cost = computeCostMatrix(seqX, seqY);
      return cost[seqX.length()];
   }

   /**
    * Cost to do an insertion. Currently returns a simple fixed cost.
    * 
    * @param c The character to insert.
    * @return The cost of inserting 'c'.
    */
   private int insertionCost(char c) {
      return this.insertionCost;
   }

   /**
    * Cost to do a deletion. Currently returns a simple fixed cost.
    * 
    * @param c The character to delete.
    * @return The cost of deleting 'c'.
    */
   private int deletionCost(char c) {
      return this.deletionCost;
   }

   /**
    * Cost of replacing 'x' with 'y'.
    * 
    * @param x The character that will be swaped-out.
    * @param y The character that will be swaped-in.
    * @return The cost of replacing 'x' with 'y'.
    */
   private int substitutionCost(char x, char y) {
      if (x == y) {
         return this.matchedSubstitutionCost;
      }
      else {
         return this.unmatchedSubstitutionCost;
      }
   }

   /**
    * Reverses the given character sequence.
    * 
    * @param cs
    * @return
    */
   private String reverse(CharSequence cs) {
      return new StringBuilder(cs).reverse().toString();
   }

}
