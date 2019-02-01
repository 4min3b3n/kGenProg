package jp.kusumotolab.kgenprog.ga.variant;

import java.util.Collections;
import java.util.List;

public class MutationHistoricalElement implements HistoricalElement {

  private final Variant parent = null;
  private final Base appendedBase = null;

  public MutationHistoricalElement(final Variant parent, final Base appendedBase) {
    // EXP-FOR-FSE
//    this.parent = parent;
//    this.appendedBase = appendedBase;
  }

  @Override
  public List<Variant> getParents() {
    return Collections.singletonList(parent);
  }

  @Override
  public String getOperationName() {
    return appendedBase.getOperation()
        .getName();
  }

  public Base getAppendedBase() {
    return appendedBase;
  }
}
