package com.octane.util.junit.runner;

import com.octane.util.junit.framework.TestFailure;
import java.awt.Component;

/** A view to show a details about a failure */
public interface FailureDetailView {
  /** Returns the component used to present the TraceView */
  public Component getComponent();

  /** Shows details of a TestFailure */
  public void showFailure(TestFailure failure);

  /** Clears the view */
  public void clear();
}
