package com.mahout.recommender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

public class UserBased {

	public static void main(String[] args) throws IOException, TasteException {
		int N = -1;
		double threshold = -1;

		RandomUtils.useTestSeed();
		String path = System.getProperty("user.dir");

		System.out.println("Specify data set");
		Scanner reader = new Scanner(System.in);
		String fileName = reader.nextLine();

		System.out.println("\nSelect Similarity (Enter corresponding number e.g. 1 for Pearson");
		System.out.println("1. Pearson Correlation");
		System.out.println("2. Spearman Correlation");
		System.out.println("3. Euclidean Distance");
		System.out.println("4. LogLikelihood Similarity");
		System.out.println("5. Tanimoto Coefficient");		
		int similarityChoice = reader.nextInt();

		System.out.println("\nSelect Neighborhood (Enter corresponding number e.g. 1 for Nearest Neighborhood");
		System.out.println("1. Nearest N Neighbors");
		System.out.println("2. Threshold Based");
		int neighborhoodChoice = reader.nextInt();

		switch (neighborhoodChoice) {
		case 1:
			System.out.println("\nSpecify no of nearest neighbours");
			N = reader.nextInt();
			break;
		case 2:
			System.out.println("\nSpecify threshold");
			threshold = reader.nextDouble();
			break;
		}

		DataModel model = new FileDataModel(new File(path + "/Datasets/ml-100k/" + fileName));

		UserSimilarity similarity = null;
		switch (similarityChoice) {
		case 1:
			similarity = new PearsonCorrelationSimilarity(model);
			break;
		case 2:
			similarity = new SpearmanCorrelationSimilarity(model);
			break;
		case 3:
			similarity = new EuclideanDistanceSimilarity(model);
			break;
		case 4:
			similarity = new LogLikelihoodSimilarity(model);
			break;
		case 5:
			similarity = new TanimotoCoefficientSimilarity(model);
			break;
		}
		UserNeighborhood neighborhood = null;		
		if (N != -1) {

			neighborhood = new NearestNUserNeighborhood(N, similarity, model);

			evaluateScore(model, similarityChoice, neighborhoodChoice, N);

		} else if (threshold != -1) {

			neighborhood = new ThresholdUserNeighborhood(threshold, similarity, model);
			evaluateScore(model, similarityChoice, neighborhoodChoice, threshold);
		}

		Recommender recommender = new GenericUserBasedRecommender (model, neighborhood, similarity);

		File f = new File(System.getProperty("user.dir") + "/Output/UserBased.txt");
		f.createNewFile();
		FileWriter fs = new FileWriter(f);
		BufferedWriter bs = new BufferedWriter(fs);

		for (int i=1; i< model.getNumUsers()+1; i++)
		{			
			List<RecommendedItem> recommendations = recommender.recommend(i, 5);
			bs.write("Recommendation of Top 5 movies for User " + i +"\r\n");

			for (RecommendedItem recommendation : recommendations) 
			{
				bs.write(recommendation.toString() + "\r\n");
			}
			bs.write("\r\n\n");
		}

		System.out.println("\nPlease refer to /Output/UserBased.txt for recommendations\n\n");
		bs.close();
		bs = null;

	}
	public static void evaluateScore(DataModel model, final int similarityChoice,final int neighborhoodChoice, final double value ) throws TasteException, IOException {

		HashMap<Integer,String> similarityTypes = new HashMap<Integer,String>();
		similarityTypes.put(1,"Pearson Correlation");
		similarityTypes.put(2, "Spearman Correlation");
		similarityTypes.put(3, "Euclidean Distance");
		similarityTypes.put(4, "LogLikelihood Similarity");
		similarityTypes.put(5, "Tanimoto Coefficient");

		HashMap<Integer,String> neighborhoodTypes = new HashMap<Integer,String>();
		neighborhoodTypes.put(1, "Nearest N Neighbors");
		neighborhoodTypes.put(2, "Threshold Based");

		RecommenderEvaluator evaluator = new RMSRecommenderEvaluator ();
		RecommenderIRStatsEvaluator evaluatorPR = new GenericRecommenderIRStatsEvaluator();

		RecommenderBuilder builder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException {

				UserSimilarity similarity = null;
				UserNeighborhood neighborhood = null;

				switch (similarityChoice) {
				case 1:
					similarity = new PearsonCorrelationSimilarity(model);
					break;
				case 2:
					similarity = new SpearmanCorrelationSimilarity(model);
					break;
				case 3:
					similarity = new EuclideanDistanceSimilarity(model);
					break;
				case 4:
					similarity = new LogLikelihoodSimilarity(model);
					break;
				case 5:
					similarity = new TanimotoCoefficientSimilarity(model);
					break;
				}

				switch (neighborhoodChoice) {
				case 1:
					neighborhood = new NearestNUserNeighborhood((int)value, similarity, model);
					break;
				case 2:
					neighborhood = new ThresholdUserNeighborhood(value, similarity, model);
					break;
				}
				return new GenericUserBasedRecommender (model, neighborhood, similarity);
			}
		};

		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		IRStatistics stats = evaluatorPR.evaluate(builder, null, model,null, 5, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);

		File f = new File(System.getProperty("user.dir") + "/Output/Evaluation.txt");
		f.createNewFile();
		FileWriter fs = new FileWriter(f,true);
		BufferedWriter bs = new BufferedWriter(fs);

		bs.write("Recommendation Type: User Based Recommendations\r\n");
		bs.write("Evaluation Type: Root Mean Square Evaluation\r\n");
		bs.write("Similarity Type: " + similarityTypes.get(similarityChoice) + "\r\n");
		bs.write("Neighborhood Type: " + neighborhoodTypes.get(neighborhoodChoice) + "\r\n");
		bs.write("For value = " + value + ", evaluation score is " + score + "\r\n");
		bs.write("Precision at 5: " + stats.getPrecision()+ "\r\n");
		bs.write("Recall at 5: " + stats.getRecall() + "\r\n\n");

		System.out.println("\nRecommendation evaluation score is " + score);
		System.out.println("Please refer to Output/Evaluation.txt for detailed report\n");

		bs.close();
		bs = null;
	}
}
