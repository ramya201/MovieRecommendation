package com.mahout.svdtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;

import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;

public class InputFormatter {
	public static int CARDINALITY;

	/**
	 * 
	 * @param args[0] - input csv file
	 * @param args[1] - cardinality (length of vector)
	 * @param args[2] - output file for svd
	 */
	public static void main(String[] args){
		String rootPath = System.getProperty("user.dir");
		
		try {
			//CARDINALITY = Integer.parseInt(args[0]);
			CARDINALITY = 10;
			final Configuration conf = new Configuration();
			final FileSystem fs = FileSystem.get(conf);
			final SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, new Path(rootPath + "/Output/SvdInput" ), IntWritable.class, VectorWritable.class, CompressionType.BLOCK);

			final IntWritable key = new IntWritable();
			final VectorWritable value = new VectorWritable();


			String thisLine;

			BufferedReader br = new BufferedReader(new FileReader(rootPath + "/Datasets/svd/Matrix1.csv"));
			Vector vector = null;
			int from = -1,to  =-1;
			int last_to = -1;
			float val = 0;
			int total = 0;
			int nnz = 0;
			int e = 0;
			int max_to =0;
			int max_from = 0;

			while ((thisLine = br.readLine()) != null) { 

				StringTokenizer st = new StringTokenizer(thisLine, ",");
				while(st.hasMoreTokens()) {
					from = Integer.parseInt(st.nextToken());
					to = Integer.parseInt(st.nextToken()); 
					val = Float.parseFloat(st.nextToken());
					if (max_from < from) max_from = from;
					if (max_to < to) max_to = to;
					if (from < 0 || to < 0 || from > CARDINALITY || val == 0.0)
						throw new NumberFormatException("wrong data" + from + " to: " + to + " val: " + val);
				}

				//we are working on an existing column, set non-zero rows in it
				if (last_to != to && last_to != -1){
					value.set(vector);

					writer.append(key, value); //write the older vector
					e+= vector.getNumNondefaultElements();
				}
				//a new column is observed, open a new vector for it
				if (last_to != to){
					vector = new SequentialAccessSparseVector(CARDINALITY); 
					key.set(to); // open a new vector
					total++;
				}

				vector.set(from, val);
				nnz++;

				if (nnz % 1000000 == 0){
					System.out.println("Col" + total + " nnz: " + nnz);
				}
				last_to = to;

			} 

			value.set(vector);
			writer.append(key,value);//write last row
			e+= vector.getNumNondefaultElements();
			total++;

			writer.close();
			System.out.println("Wrote a total of " + total + " cols " + " nnz: " + nnz);
			if (e != nnz)
				System.err.println("Bug:missing edges! we only got" + e);

			System.out.println("Highest column: " + max_to + " highest row: " + max_from );
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
