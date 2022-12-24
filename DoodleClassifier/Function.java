package DoodleClassifier;

public class Function {
	String strType;
	int type;
	// 0 - Sigmoid
	// 1 - Hyperbolic Tangent
	// Add MORE HERE
	boolean derivative;
	public Function(String type, boolean derivative) {
		this.strType = type;
		this.type = -1;
		if(type.equalsIgnoreCase("Sigmoid") || type.equals("Sigma") || 
				type.equalsIgnoreCase("s")) {
			this.type = 0;
		} else if(type.equalsIgnoreCase("Hyperbolic Tangent") || type.equalsIgnoreCase("tanh")
				|| type.equalsIgnoreCase("th") || type.equalsIgnoreCase("Hyper Tan")) {
			this.type = 1;
		} else {
			System.out.println("!!! No function of type \"" + type + "\" found");
		}
		this.derivative = derivative;
	}
	
	double f(double x) {
		if(type == 0) {
			if(derivative) {
				return dsigmoid(x);
			} else {
				return sigmoid(x);
			}
		} else if(type == 1) {
			if(derivative) {
				return tanh(x);
			} else {
				return dtanh(x);
			}
		}
		return 0x3f3f3f;
	}
	
	double f(double x, double i, double j) {
		if(type == 0) {
			if(derivative) {
				return dsigmoid(x);
			} else {
				return sigmoid(x);
			}
		} else if(type == 1) {
			if(derivative) {
				return tanh(x);
			} else {
				return dtanh(x);
			}
		}
		return 0x3f3f3f;
	}
	
	double sigmoid(double x) {
		return 1 / (1 + Math.exp(-x));
	}
	
	double dsigmoid(double y) {
		return y * (1 - y);
	}
	
	double tanh(double x) {
		return Math.tanh(x);
	}
	
	double dtanh(double y) {
		return 1 - (y * y);
	}
}
