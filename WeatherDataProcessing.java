package com.weatherpkg.wc;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WeatherDataProcessing {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: WeatherDataProcessing <in> <out>");
            System.exit(2);
        }
        Job job = new Job(conf, "weather data processing");
        job.setJarByClass(WeatherDataProcessing.class);
        job.setMapperClass(WeatherMapper.class);
        job.setReducerClass(WeatherReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class WeatherMapper extends Mapper<LongWritable, Text, Text, DoubleWritable> {
        private static final int AVG_TEMP_IDX = 0;
        private static final int MAX_TEMP_IDX = 1;
        private static final int MIN_TEMP_IDX = 2;
        private static final String AVG_TEMP_KEY = "AvgTemp";
        private static final String MAX_TEMP_KEY = "MaxTemp";
        private static final String MIN_TEMP_KEY = "MinTemp";

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            // Skip the header line
            if (line.startsWith("AvgTemp")) return;

            String[] values = line.split("\\s+");
            double avgTemp = Double.parseDouble(values[AVG_TEMP_IDX]);
            double maxTemp = Double.parseDouble(values[MAX_TEMP_IDX]);
            double minTemp = Double.parseDouble(values[MIN_TEMP_IDX]);

            context.write(new Text(AVG_TEMP_KEY), new DoubleWritable(avgTemp));
            context.write(new Text(MAX_TEMP_KEY), new DoubleWritable(maxTemp));
            context.write(new Text(MIN_TEMP_KEY), new DoubleWritable(minTemp));
        }
    }

    public static class WeatherReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {
        public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
            double sum = 0;
            int count = 0;
            double minTemp = Double.MAX_VALUE;
            double maxTemp = Double.MIN_VALUE;

            for (DoubleWritable val : values) {
                double temp = val.get();
                sum += temp;
                count++;
                if (key.toString().equals("MinTemp")) {
                    if (temp < minTemp) {
                        minTemp = temp;
                    }
                } else if (key.toString().equals("MaxTemp")) {
                    if (temp > maxTemp) {
                        maxTemp = temp;
                    }
                }
            }
            
            double average = sum / count;
            context.write(key, new DoubleWritable(average));
            
            if (key.toString().equals("MinTemp")) {
                context.write(new Text("OverallMinTemp"), new DoubleWritable(minTemp));
            } else if (key.toString().equals("MaxTemp")) {
                context.write(new Text("OverallMaxTemp"), new DoubleWritable(maxTemp));
            }
        }
    }
}
