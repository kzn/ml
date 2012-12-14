package ru.iitp.proling.ml.sgd;

public class LossFunctions {
	public static class LogLoss implements LossFunction {
		// logloss(a,y) = log(1+exp(-a*y))
		@Override
		public double loss(double a, double y) {
			double z = a * y;
			if (z > 18) 
				return Math.exp(-z);
			if (z < -18)
				return -z;
			return Math.log(1 + Math.exp(-z));
		}


		// -dloss(a,y)/da
		@Override
		public double dloss(double a, double y) {
			double z = a * y;
			if (z > 18) 
				return y * Math.exp(-z);
			if (z < -18)
				return y;
			return y / (1 + Math.exp(z));
		}
	}

	public static class SquaredHingeLoss implements LossFunction {
		// squaredhingeloss(a,y) = 1/2 * max(0, 1-a*y)^2
		@Override
		public double loss(double a, double y) {
			double z = a * y;
			if (z > 1)
				return 0;
			double d = 1 - z;
			return 0.5 * d * d;

		}
		
		// -dloss(a,y)/da
		@Override
		public double dloss(double a, double y) {
			double z = a * y;
			if (z > 1) 
				return 0;
			return y * (1 - z);
		}


	}



	public static class SmoothHingeLoss implements LossFunction {
		// smoothhingeloss(a,y) = ...
		@Override
		public double loss(double a, double y) {
			double z = a * y;
			if (z > 1)
				return 0;
			if (z < 0)
				return 0.5 - z;
			double d = 1 - z;
			return 0.5 * d * d;
		}
		
		// -dloss(a,y)/da
		@Override
		public double dloss(double a, double y) {
			double z = a * y;
			if (z > 1) 
				return 0;
			if (z < 0)
				return y;
			return y * (1 - z);
		}
	};


	public static class HingeLoss implements LossFunction {
		// hingeloss(a,y) = max(0, 1-a*y)
		@Override
		public double loss(double a, double y) {
			double z = a * y;
			if (z > 1) 
				return 0;
			return 1 - z;
		}

		// -dloss(a,y)/da
		@Override
		public double dloss(double a, double y)	{
			double z = a * y;
			if (z > 1) 
				return 0;
			return y;
		}

	}


}
