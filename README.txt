Notes -
1. Dataset used - GroupLens 100k dataset consisting of 100,000 ratings (1-5) from 943 users on 1682 movies. 
2. Algorithms implemented - User Based Collaborative Filtering, Item Based Collaborative Filtering, Slope One Recommenders
3. Enables a comparison of efficiency of various algorithms with differing parameters.
4. Most efficient algorithm for given data set, concluded by observing statistics of experimental runs -

Instructions to Run -
1. In Eclipse, File > Import > Maven > Existing Maven Projects
2. Ensure the appropriate MovieLens dataset exists in root>Datasets>ml-100k
3. Run each of the programs UserBased.java, ItemBased.java & SlopeOne.java in turn
4. Recommendation results for each run will be available at Output/UserBased.txt or Output/ItemBased.txt or Output/SlopeOne.txt respectively
5. Evaluation score for each run is appended to the file Output/Evaluation.txt
6. Input Parameters -
	Dataset - ua.csv or ub.base (Both are training datasets derived from u.data)
	Nearest Neighborhood N - Any number between 2-800
	Threshold - Any number between 0.1 - 0.99