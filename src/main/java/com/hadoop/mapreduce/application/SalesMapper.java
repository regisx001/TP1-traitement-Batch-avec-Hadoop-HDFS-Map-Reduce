package com.hadoop.mapreduce.application;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SalesMapper extends Mapper<Object, Text, Text, DoubleWritable> {

    private Text store = new Text();
    private DoubleWritable price = new DoubleWritable();

    public void map(Object key, Text value, Context context)
            throws IOException, InterruptedException {

        String line = value.toString();
        String[] fields = line.split("\\s+");

        if (fields.length >= 6) {

            String magasin = fields[2]; // store
            double cout = Double.parseDouble(fields[4]); // price

            store.set(magasin);
            price.set(cout);

            context.write(store, price);
        }
    }
}