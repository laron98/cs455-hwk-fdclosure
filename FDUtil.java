import java.util.Set;
import java.util.HashSet;

/**
 * This utility class is not meant to be instantitated, and just provides some
 * useful methods on FD sets.
 * 
 * @author Langston Aron
 * @version 10/25/2022
 */
public final class FDUtil {

  /**
   * Resolves all trivial FDs in the given set of FDs
   * 
   * @param fdset (Immutable) FD Set
   * @return a set of trivial FDs with respect to the given FDSet
   */
  public static FDSet trivial(final FDSet fdset) {
    // TODO: Obtain the power set of each FD's left-hand attributes. For each
    // element in the power set, create a new FD and add it to the a new FDSet.
	Set<FD> temp1 = fdset.getSet(); 
	FD[] triv = temp1.toArray(new FD[0]);
	FDSet trivial = new FDSet();
	for(int c = 0; c < triv.length; c++) {
		Set<Set<String>> right = powerSet(triv[c].getLeft());
		Set<String>[] temp3 = right.toArray(new Set[0]);
		for(int r = 1; r < temp3.length; r++) {
			FD temp2 = new FD();
			temp2.addToLeft(triv[c].getLeft());
			temp2.addToRight(temp3[r]);
			trivial.add(temp2);
		}
	}
	return trivial;
  }

  /**
   * Augments every FD in the given set of FDs with the given attributes
   * 
   * @param fdset FD Set (Immutable)
   * @param attrs a set of attributes with which to augment FDs (Immutable)
   * @return a set of augmented FDs
   */
  public static FDSet augment(final FDSet fdset, final Set<String> attrs) {
    // TODO: Copy each FD in the given set and then union both sides with the given
    // set of attributes, and add this augmented FD to a new FDSet.
	Set<FD> temp1 = fdset.getSet();
	FD[] aug = temp1.toArray(new FD[temp1.size()]);
	FDSet augment = new FDSet();
	for(int c = 0; c < aug.length; c++) {
		FD temp2 = new FD();
		temp2.addToLeft(aug[c].getLeft());
		temp2.addToLeft(attrs);
		temp2.addToRight(aug[c].getRight());
		temp2.addToRight(attrs);
		augment.add(temp2);
	}
    return augment;
  }

  /**
   * Exhaustively resolves transitive FDs with respect to the given set of FDs
   * 
   * @param fdset (Immutable) FD Set
   * @return all transitive FDs with respect to the input FD set
   */
  public static FDSet transitive(final FDSet fdset) {
    // TODO: Examine each pair of FDs in the given set. If the transitive property
    // holds on the pair of FDs, then generate the new FD and add it to a new FDSet.
    // Repeat until no new transitive FDs are found.
	Set<FD> temp1 = fdset.getSet();
	FD[] tran = temp1.toArray(new FD[temp1.size()]);
	FDSet transitive = new FDSet();
	for(int c = 0; c < tran.length; c++) {
		for(int r = 0; r < tran.length; r++) {
			if(tran[c].getRight().equals(tran[r].getLeft())) {
				FD temp2 = new FD();
				temp2.addToLeft(tran[c].getLeft());
				temp2.addToRight(tran[r].getRight());
				transitive.add(temp2);
				for(int k = 0; k < tran.length; k++) {
					if(temp2.getRight().equals(tran[k].getLeft())) {
						FD temp3 = new FD();
						temp3.addToLeft(tran[c].getLeft());
						temp3.addToRight(tran[k].getRight());
						transitive.add(temp3);
					}
				}
			}
		}
	}
    return transitive;
  }

  /**
   * Generates the closure of the given FD Set
   * 
   * @param fdset (Immutable) FD Set
   * @return the closure of the input FD Set
   */
  public static FDSet fdSetClosure(final FDSet fdset) {
    // TODO: Use the FDSet copy constructor to deep copy the given FDSet

    // TODO: Generate new FDs by applying Trivial and Augmentation Rules, followed
    // by Transitivity Rule, and add new FDs to the result.
    // Repeat until no further changes are detected.
	FDSet tempA = fdset;
	FDSet tempB = new FDSet();
	while(!tempA.equals(tempB)) {
		Set<FD> temp1 = tempA.getSet();
		FD[] aug = temp1.toArray(new FD[temp1.size()]);
		for(int c = 0; c < aug.length; c++) {
			tempB.addAll(augment(tempA, aug[c].getLeft()));
		}
		tempB.addAll(trivial(tempA));
		tempB.addAll(transitive(tempA));
	}
    return tempB;
  }

  /**
   * Generates the power set of the given set (that is, all subsets of
   * the given set of elements)
   * 
   * @param set Any set of elements (Immutable)
   * @return the power set of the input set
   */
  @SuppressWarnings("unchecked")
  public static <E> Set<Set<E>> powerSet(final Set<E> set) {

    // base case: power set of the empty set is the set containing the empty set
    if (set.size() == 0) {
      Set<Set<E>> basePset = new HashSet<>();
      basePset.add(new HashSet<>());
      return basePset;
    }

    // remove the first element from the current set
    E[] attrs = (E[]) set.toArray();
    set.remove(attrs[0]);

    // recurse and obtain the power set of the reduced set of elements
    Set<Set<E>> currentPset = FDUtil.powerSet(set);

    // restore the element from input set
    set.add(attrs[0]);

    // iterate through all elements of current power set and union with first
    // element
    Set<Set<E>> otherPset = new HashSet<>();
    for (Set<E> attrSet : currentPset) {
      Set<E> otherAttrSet = new HashSet<>(attrSet);
      otherAttrSet.add(attrs[0]);
      otherPset.add(otherAttrSet);
    }
    currentPset.addAll(otherPset);
    return currentPset;
  }
}
