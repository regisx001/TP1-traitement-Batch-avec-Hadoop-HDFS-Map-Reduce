package com.hadoop.mapreduce.application;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TotalSales {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.err.println("Usage: TotalSales <input> <output>");
            System.exit(-1);
        }

        System.out.println("ARG0 = " + args[0]);
        System.out.println("ARG1 = " + args[1]);

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Total Sales Per Store");

        job.setJarByClass(TotalSales.class);

        job.setMapperClass(SalesMapper.class);
        job.setReducerClass(SalesReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}