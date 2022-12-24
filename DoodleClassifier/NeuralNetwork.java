package DoodleClassifier;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class NeuralNetwork {
	int input_nodes;
	int hidden_nodes;
	int output_nodes;
	
	Matrix weights_ih;
	Matrix weights_ho;
	Matrix bias_h;
	Matrix bias_o;
	
	double learningRate;
	
	Function activation_function;
	Function activation_function_d;
	
	public NeuralNetwork(int input_nodes, int hidden_nodes, int output_nodes) {
		this.input_nodes = input_nodes;
		this.hidden_nodes = hidden_nodes;
		this.output_nodes = output_nodes;
		
		weights_ih = new Matrix(this.hidden_nodes, this.input_nodes);
		weights_ho = new Matrix(this.output_nodes, this.hidden_nodes);
		weights_ih.randomize();
		weights_ho.randomize();
		
		bias_h = new Matrix(this.hidden_nodes, 1);
		bias_o = new Matrix(this.output_nodes, 1);
		bias_h.randomize();
		bias_o.randomize();
		
		learningRate = 0.1;
		setActivationFunction("Sigmoid");
	}
	
	double [] predict(Matrix input) throws Exception {
		Matrix hidden = Matrix.multiply(weights_ih, input);
		hidden.add(bias_h);
		hidden.map(activation_function);
		
		Matrix outputs = Matrix.multiply(weights_ho, hidden);
		outputs.add(bias_o);
		outputs.map(activation_function);
		
		return outputs.toArray();
	}
	
	double [] predict(double [] input_array) throws Exception {
		Matrix input = Matrix.fromArray(input_array);
		Matrix hidden = Matrix.multiply(weights_ih, input);
		hidden.add(bias_h);
		hidden.map(activation_function);
		
		Matrix outputs = Matrix.multiply(weights_ho, hidden);
		outputs.add(bias_o);
		outputs.map(activation_function);
		
		return outputs.toArray();
	}
	
	void train(double [] input_array, double [] target_array) throws Exception {
		Matrix inputs = Matrix.fromArray(input_array);
		Matrix hidden = Matrix.multiply(weights_ih, inputs);
		hidden.add(bias_h);
		hidden.map(activation_function);
		
		Matrix outputs = Matrix.multiply(weights_ho, hidden);
		outputs.add(bias_o);
		outputs.map(activation_function);
		
		// Calculate the output error and change the 
		// weights of the hidden - output by the 
		// derivative of the outputs * error * learning rate * weight of hidden node
		Matrix targets = Matrix.fromArray(target_array);
		Matrix output_errors = Matrix.subtract(targets, outputs);
		
		Matrix gradients = Matrix.map(outputs, activation_function_d);
		gradients.multiply(output_errors);
		gradients.multiply(learningRate);
		
		Matrix hidden_T = Matrix.transpose(hidden);
		Matrix weight_ho_deltas = Matrix.multiply(gradients, hidden_T);
		
		weights_ho.add(weight_ho_deltas);
		bias_o.add(gradients);
		
		Matrix who_t = Matrix.transpose(weights_ho);
		Matrix hidden_errors = Matrix.multiply(who_t, output_errors);
		
		Matrix hidden_gradient = Matrix.map(hidden, activation_function_d);
		hidden_gradient.multiply(hidden_errors);;
		hidden_gradient.multiply(learningRate);
		
		Matrix inputs_T = Matrix.transpose(inputs);
		Matrix weight_ih_deltas = Matrix.multiply(hidden_gradient, inputs_T);
		
		weights_ih.add(weight_ih_deltas);
		bias_h.add(hidden_gradient);
	}
	
	void mutate(Function e) {
		weights_ih.map(e);
		weights_ho.map(e);
		bias_h.map(e);
		bias_o.map(e);
	}
	
	void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}
	
	void setActivationFunction(String type) {
		activation_function = new Function(type, false);
		activation_function_d = new Function(type, true);
	}
	void serialize(String name) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(name));
		pw.println(input_nodes);
		pw.println(hidden_nodes);
		pw.println(output_nodes);
		pw.println(learningRate);
		pw.println(activation_function.strType);
		weights_ih.serialize(pw);
		weights_ho.serialize(pw);
		bias_h.serialize(pw);
		bias_o.serialize(pw);
		pw.close();
	}
	
	void deserialize(String name) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
		StringTokenizer st;
		st = new StringTokenizer(br.readLine().trim());
		input_nodes = Integer.parseInt(st.nextToken());
		st = new StringTokenizer(br.readLine().trim());
		hidden_nodes = Integer.parseInt(st.nextToken());
		st = new StringTokenizer(br.readLine().trim());
		output_nodes = Integer.parseInt(st.nextToken());
		st = new StringTokenizer(br.readLine().trim());
		learningRate = Double.parseDouble(st.nextToken());
		String strType = br.readLine().trim();
		activation_function = new Function(strType, false);
		activation_function_d = new Function(strType, true);
		weights_ih.deserialize(br, st);
		weights_ho.deserialize(br, st);
		bias_h.deserialize(br, st);
		bias_o.deserialize(br, st);
	}
}
