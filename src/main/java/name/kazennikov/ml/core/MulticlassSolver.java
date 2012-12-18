package name.kazennikov.ml.core;

import ru.iitp.proling.ml.core.MultiScorer;


public interface MulticlassSolver {
	public MultiScorer solve(MulticlassProblem problem);
}
