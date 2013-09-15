package com.mahout.recommender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

public class SlopeOne {

	public static void main(String[] args) throws IOException, TasteException {

		RandomUtils.useTestSeed();
		String path = System.getProperty("user.dir") + "/Datasets/ml-100k/ua.base";

		DataModel model = new FileDataModel(new File(path));

		Recommender recommender = new SlopeOneRecommender(model);

		File f = new File(System.getProperty("user.dir") + "/Output/SlopeOne.txt");
		f.createNewFile();
		FileWriter fs = new FileWriter(f);
		BufferedWriter bs = new BufferedWriter(fs);

		for (int i=1; i< 944; i++)
		{			
			List<RecommendedItem> recommendations = recommender.recommend(i, 5);
			bs.write("Recommendation of Top 5 movies for User " + i +"\r\n");

			for (RecommendedItem recommendation : recommendations) 
			{
				bs.write(recommendation.toString() + "\r\n");
			}

			bs.write("\r\n\n");
		}
		System.out.println("\nPlease refer to /Output/SlopeOne.txt for recommendations\n\n");
		bs.close();
		bs = null;
		
		evaluateScore(model);

	}
	public static void evaluateScore(DataModel model) throws TasteException, IOException {
		RecommenderEvaluator evaluator = new RMSRecommenderEvaluator ();
		RecommenderIRStatsEvaluator evaluatorPR = new GenericRecommenderIRStatsEvaluator();
		
		RecommenderBuilder builder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(DataModel model) throws TasteException {
				return new SlopeOneRecommender(model);
			}
		};
		
		double score = evaluator.evaluate(builder, null, model, 0.7, 1.0);
		IRStatistics stats = evaluatorPR.evaluate(builder, null, model,null, 5, GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD, 1.0);
		
		File f = new File(System.getProperty("user.dir") + "/Output/Evaluation.txt");
		f.createNewFile();
		FileWriter fs = new FileWriter(f,true);
		BufferedWriter bs = new BufferedWriter(fs);

		bs.write("Recommendation Type: Slope One Recommendations\r\n");
		bs.write("Evaluation Type: Root Mean Square Evaluation\r\n");
		bs.write("Evaluation score is " + score + "\r\n");
		bs.write("Precision at 5: " + stats.getPrecision()+ "\r\n");
		bs.write("Recall at 5: " + stats.getRecall() + "\r\n\n");
		
		System.out.println("\nRecommendation evaluation score is " + score);
		System.out.println("Please refer to Output/Evaluation.txt for detailed report\n");
		
		bs.close();
		bs = null;
	}
}
