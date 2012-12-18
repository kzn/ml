package name.kazennikov.ml.sgd;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.List;

import name.kazennikov.ml.core.Instance;


public class ASGD {
	LossFunction loss;
	
	double lambda;
	int tStart;
	double eta0;
	int dim;


	double  mu0 = 1;

	double[] w;
	double  wDivisor;
	double  wBias;

	double[] a;
	double  aDivisor;
	double  wFraction;
	double  aBias;
	int t;
	boolean bias;
	boolean regularizedBias;

	public ASGD(LossFunction loss, double lambda, int tstart, double eta0, int dim) {
		this.loss = loss;
		this.lambda = lambda;
		this.tStart = tstart;
		this.eta0 = eta0;
		this.dim = dim;
		this.w = new double[dim + 1];
		this.a = new double[dim + 1];
		
		this.aDivisor = 1;
		this.wDivisor = 1;
		this.mu0 = 1;
	}
	

	
	/**
	 * Renorm the weight vector
	 */
	public void renorm() {
	  if (wDivisor != 1.0 || aDivisor != 1.0 || wFraction != 0) {
	      DenseVector.combine(a, 1/aDivisor, w, wFraction/aDivisor);
	      DenseVector.scale(w, 1/wDivisor);
	      wDivisor = aDivisor = 1;
	      wFraction = 0;
	    }
	}
	
	
	/// Compute the norm of the normal weights
	public double wnorm() {
	  double norm = DenseVector.dot(w,w) / wDivisor / wDivisor;

	  if(regularizedBias)
		  norm += wBias * wBias;

	  return norm;
	}

	/// Compute the norm of the averaged weights
	public double anorm() {
	  renorm(); // this is simpler!
	  double norm = DenseVector.dot(a,a);
	  
	  if(regularizedBias)
		  norm += aBias * aBias;
	  
	  return norm;
	}

	/// Compute the output for one example
	public double testOne(Instance x, double y/*, double *ploss, double *pnerr*/)
	{
	  // Same as dot(a,x) + aBias after renormalization
	  double s = DenseVector.dot(a, x);
	  if (wFraction != 0) 
	    s += DenseVector.dot(w,x) * wFraction;
	  s = s / aDivisor + aBias;
//	  // accumulate loss and errors
//	  if (ploss)
//	    *ploss += LOSS::loss(s, y);
//	  if (pnerr)
//	    *pnerr += (s * y <= 0) ? 1 : 0;
	  return s;
	}

	/// Perform one iteration of the SGD algorithm with specified gain
	
	public void trainOne(Instance x, double y, double eta, double mu)
	{
	  // Renormalize if needed
	  if (aDivisor > 1e5 || wDivisor > 1e5) 
		  renorm();
	  
	  // Forward
	  double s = DenseVector.dot(w, x) / wDivisor + wBias;
	  // SGD update for regularization term
	  wDivisor = wDivisor / (1 - eta * lambda);
	  // SGD update for loss term
	  double d = loss.dloss(s, y);
	  double etd = eta * d * wDivisor;
	  if (etd != 0)
	    DenseVector.add(w, x, etd);

	  // Averaging
	  if (mu >= 1)
	    {
	      DenseVector.clear(a);
	      aDivisor = wDivisor;
	      wFraction = 1;
	    }
	  else if (mu > 0)
	    {
	      if (etd != 0)
	        DenseVector.add(a, x, - wFraction * etd);
	      aDivisor = aDivisor / (1 - mu);
	      wFraction = wFraction + mu * aDivisor / wDivisor;
	    }

	  // same for the bias
	  if(bias) {
		  double etab = eta * 0.01;
		  if(regularizedBias)
			  wBias *= (1 - etab * lambda);

		  wBias += etab * d;
		  aBias += mu * (wBias - aBias);
	  }
	}


	/// Perform a training epoch
	void train(int imin, int imax, List<Instance> xp, TDoubleArrayList yp) {
		System.out.printf("Training on [%d,%d]%n", imin, imax);

		for (int i=imin; i < imax; i++) {
			double eta = eta0 / Math.pow(1 + lambda * eta0 * t, 0.75);
			double mu = (t <= tStart) ? 1.0 : mu0 / (1 + mu0 * (t - tStart));
			trainOne(xp.get(i), yp.get(i), eta, mu);
			t += 1;
		}

		System.out.printf("wNorm=%.4f aNorm=%.4f", wnorm(), anorm());

		if(bias) {
			System.out.printf(" wBias=%.4f aBias=%.4f", wBias, aBias);
		}

		System.out.println();
	}
	
	public double[] w() {
		renorm();
		return w;
	}
	
	public double[] a() {
		renorm();
		return a;
	}


//	/// Perform a test pass
//	void 
//	SvmAsgd::test(int imin, int imax, const xvec_t &xp, const yvec_t &yp, const char *prefix)
//	{
//	  cout << prefix << "Testing on [" << imin << ", " << imax << "]." << endl;
//	  assert(imin <= imax);
//	  double nerr = 0;
//	  double loss = 0;
//	  for (int i=imin; i<=imax; i++)
//	    testOne(xp.at(i), yp.at(i), &loss, &nerr);
//	  nerr = nerr / (imax - imin + 1);
//	  loss = loss / (imax - imin + 1);
//	  double cost = loss + 0.5 * lambda * wnorm();
//	  cout << prefix 
//	       << "Loss=" << setprecision(12) << loss
//	       << " Cost=" << setprecision(12) << cost 
//	       << " Misclassification=" << setprecision(4) << 100 * nerr << "%." 
//	       << endl;
//	}
//
	/// Perform one epoch with fixed eta and return cost
	
//	void determineEta0(int imin, int imax, List<Instance> xp, TDoubleArrayList yp) {
//	  double factor = 2.0;
//	  double loEta = 1;
//	  double loCost = evaluateEta(imin, imax, xp, yp, loEta);
//	  double hiEta = loEta * factor;
//	  double hiCost = evaluateEta(imin, imax, xp, yp, hiEta);
//	  if (loCost < hiCost)
//	    while (loCost < hiCost)
//	      {
//	        hiEta = loEta;
//	        hiCost = loCost;
//	        loEta = hiEta / factor;
//	        loCost = evaluateEta(imin, imax, xp, yp, loEta);
//	      }
//	  else if (hiCost < loCost)
//	    while (hiCost < loCost)
//	      {
//	        loEta = hiEta;
//	        loCost = hiCost;
//	        hiEta = loEta * factor;
//	        hiCost = evaluateEta(imin, imax, xp, yp, hiEta);
//	      }
//	  eta0 = loEta;
//	  System.out.printf("Using eta0=%.4f%n", eta0);
//	}
//
//
//
//	double evaluateEta(int imin, int imax, List<Instance> xp, TDoubleArrayList yp, double eta) {
//	  SvmAsgd clone(*this); // take a copy of the current state
//	  assert(imin <= imax);
//	  for (int i=imin; i<=imax; i++)
//	    clone.trainOne(xp.at(i), yp.at(i), eta, 1.0);
//	  double loss = 0;
//	  double cost = 0;
//	  for (int i=imin; i<=imax; i++)
//	    clone.testOne(xp.at(i), yp.at(i), &loss, 0);
//	  loss = loss / (imax - imin + 1);
//	  cost = loss + 0.5 * lambda * clone.wnorm();
//	  // cout << "Trying eta=" << eta << " yields cost " << cost << endl;
//	  return cost;
//	}
//
//
//	// --- Command line arguments
//
//	const char *trainfile = 0;
//	const char *testfile = 0;
//	bool normalize = true;
//	double lambda = 1e-5;
//	int epochs = 5;
//	int maxtrain = -1;
//	int avgstart = 1;
//
//
//	void
//	usage(const char *progname)
//	{
//	  const char *s = ::strchr(progname,'/');
//	  progname = (s) ? s + 1 : progname;
//	  cerr << "Usage: " << progname << " [options] trainfile [testfile]" << endl
//	       << "Options:" << endl;
//	#define NAM(n) "    " << setw(16) << left << n << setw(0) << ": "
//	#define DEF(v) " (default: " << v << ".)"
//	  cerr << NAM("-lambda x")
//	       << "Regularization parameter" << DEF(lambda) << endl
//	       << NAM("-epochs n")
//	       << "Number of training epochs" << DEF(epochs) << endl
//	       << NAM("-dontnormalize")
//	       << "Do not normalize the L2 norm of patterns." << endl
//	       << NAM("-maxtrain n")
//	       << "Restrict training set to n examples." << endl
//	       << NAM("-avgstart x")
//	       << "Only start averaging after x epochs." << DEF(avgstart) << endl;
//	#undef NAM
//	#undef DEF
//	  ::exit(10);
//	}
//
//	void
//	parse(int argc, const char **argv)
//	{
//	  for (int i=1; i<argc; i++)
//	    {
//	      const char *arg = argv[i];
//	      if (arg[0] != '-')
//	        {
//	          if (trainfile == 0)
//	            trainfile = arg;
//	          else if (testfile == 0)
//	            testfile = arg;
//	          else
//	            usage(argv[0]);
//	        }
//	      else
//	        {
//	          while (arg[0] == '-') 
//	            arg += 1;
//	          string opt = arg;
//	          if (opt == "lambda" && i+1<argc)
//	            {
//	              lambda = atof(argv[++i]);
//	              assert(lambda>0 && lambda<1e4);
//	            }
//	          else if (opt == "epochs" && i+1<argc)
//	            {
//	              epochs = atoi(argv[++i]);
//	              assert(epochs>0 && epochs<1e6);
//	            }
//	          else if (opt == "dontnormalize")
//	            {
//	              normalize = false;
//	            }
//	          else if (opt == "maxtrain" && i+1 < argc)
//	            {
//	              maxtrain = atoi(argv[++i]);
//	              assert(maxtrain > 0);
//	            }
//	          else if (opt == "avgstart" && i+1 < argc)
//	            {
//	              avgstart = atof(argv[++i]);
//	              assert(avgstart > 0);
//	            }
//	          else
//	            {
//	              cerr << "Option " << argv[i] << " not recognized." << endl;
//	              usage(argv[0]);
//	            }
//
//	        }
//	    }
//	  if (! trainfile)
//	    usage(argv[0]);
//	}
//
//	void 
//	config(const char *progname)
//	{
//	  cout << "# Running: " << progname;
//	  cout << " -lambda " << lambda;
//	  cout << " -epochs " << epochs;
//	  cout << " -avgstart " << avgstart;
//	  if (! normalize) cout << " -dontnormalize";
//	  if (maxtrain > 0) cout << " -maxtrain " << maxtrain;
//	  cout << endl;
//	#define NAME(x) #x
//	#define NAME2(x) NAME(x)
//	  cout << "# Compiled with: "
//	       << " -DLOSS=" << NAME2(LOSS)
//	       << " -DBIAS=" << BIAS
//	       << " -DREGULARIZED_BIAS=" << REGULARIZED_BIAS
//	       << endl;
//	}
//
//	// --- main function
//
//	int dims;
//	xvec_t xtrain;
//	yvec_t ytrain;
//	xvec_t xtest;
//	yvec_t ytest;
//
//	int main(int argc, const char **argv)
//	{
//	  parse(argc, argv);
//	  config(argv[0]);
//	  if (trainfile)
//	    load_datafile(trainfile, xtrain, ytrain, dims, normalize, maxtrain);
//	  if (testfile)
//	    load_datafile(testfile, xtest, ytest, dims, normalize);
//	  cout << "# Number of features " << dims << "." << endl;
//	  // prepare svm
//	  int imin = 0;
//	  int imax = xtrain.size() - 1;
//	  int tmin = 0;
//	  int tmax = xtest.size() - 1;
//	  SvmAsgd svm(dims, lambda, avgstart * (imax-imin+1));
//	  Timer timer;
//	  // determine eta0 using sample
//	  int smin = 0;
//	  int smax = imin + min(1000, imax);
//	  timer.start();
//	  svm.determineEta0(smin, smax, xtrain, ytrain);
//	  timer.stop();
//	  // train
//	  for(int i=0; i<epochs; i++)
//	    {
//	      cout << "--------- Epoch " << i+1 << "." << endl;
//	      timer.start();
//	      svm.train(imin, imax, xtrain, ytrain);
//	      timer.stop();
//	      cout << "Total training time " << setprecision(6) 
//	           << timer.elapsed() << " secs." << endl;
//	      svm.test(imin, imax, xtrain, ytrain, "train: ");
//	      if (tmax >= tmin)
//	        svm.test(tmin, tmax, xtest, ytest, "test:  ");
//	    }
//	  svm.renorm();
//	  // Linear classifier is in svm.a and svm.aBias
//	  return 0;
//	}
	

}
