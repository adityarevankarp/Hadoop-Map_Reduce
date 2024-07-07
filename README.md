# Hadoop-Map_Reduce
### Requirements (Windows 11) Setup
#### - Java 8 runtime environment (JRE)
#### - Java 8 development Kit (JDK)
#### - Hadoop binaries
#### - Winrar or any Unzipper.
#### ----------------------------------
### Follow this Youtube Tutorial for the complete setup by "Technical Windows"
#### - "[https://www.youtube.com/watch?v=2Gmk7OTU2vc&t=324s](https://www.youtube.com/watch?v=7TTcjo2cMEo)"
#### - Install Java (uninstalling Previous Versions of Java is not requried)
### Configure your environmet variables
### Configure Hadoop enviroment (Detailed Tutorial Link above).
#### Run Command Prompt as administrator
#### - Formatting the name node
```Shell
hdfs namenode -format
```
#### - starting of the name node and data node (Services)
```Shell
cd C:\hadoop\sbin
start-dfs.cmd
```
![image](https://github.com/adityarevankarp/Hadoop-Map_Reduce/assets/57789526/dc3bd501-c8b1-4ebd-8193-587bc2a00c94)

##### Now namenode and datanode has been started (Do not close any windows).
#### - To check all the instances running.
```Shell
jps
```
![image](https://github.com/adityarevankarp/Hadoop-Map_Reduce/assets/57789526/1faddac0-0f92-4aee-ad91-cac3000f7e91)
#### - Start Yarn
```Shell
start-yarn.cmd
```
#### - Download and setup Eclipse to generate jar file using java code.
#### - Close all instances of hadoop.
#### -----------------------------------------------------------------------
#### Problem - To output word count using hadoop using map reduce.
#### - Step 1. Start Hadoop
```Shell
start-all.cmd
```
```Shell
jps
```
![image](https://github.com/adityarevankarp/Hadoop-Map_Reduce/assets/57789526/a9b205e3-4f7c-47cc-8161-12429134c6aa)
#### - Go to browser and open "localhost:9870" Goto - Utilities/Browse the file system
#### - Creating of directory in HDFS named "input"
```Shell
hadoop fs -mkdir /input
```
![image](https://github.com/adityarevankarp/Hadoop-Map_Reduce/assets/57789526/9067d754-d56a-4853-958e-32ddac65b97f)
#### - Feeding an input txt file to process data (data set)
#### - Create a txt file locally anywhere in your machine and add some data eg-
![image](https://github.com/adityarevankarp/Hadoop-Map_Reduce/assets/57789526/befcc7cb-d064-441d-826a-25ea51a8eab0)
#### - Copy the path of the file - Rightclick/properties
#### - Now add this dataset file in the HDFS enviroment "input" directory
```Shell
hadoop fs -put "YourFilePath eg-C:\Users\admin\Documents\Files\input.txt /input
```
#### - Check in the HDFS gui ie localhost:9870 input dir
![image](https://github.com/adityarevankarp/Hadoop-Map_Reduce/assets/57789526/b0fa8cbe-f7ae-42a3-9c16-a00b4d2cfd2b)
#### - Creating a jar file in eclipse
#### - Open Eclipse
#### - File/new/JavaProject give a project name and select execution environment ie Javase-1.8
#### - Select the newly created project right click new/package give pkg name eg com.mapreduce.wc and create a java class
#### - Configure Build Path Detailed video below
#### - "https://www.youtube.com/watch?v=htbYT7_TZKA&t=670s"
```Java
package com.mapreduce.wc;
import java.io.IOException; 
import org.apache.hadoop.conf.Configuration; 
import org.apache.hadoop.fs.Path; 
import org.apache.hadoop.io.IntWritable; 
import org.apache.hadoop.io.LongWritable; 
import org.apache.hadoop.io.Text; 
import org.apache.hadoop.mapreduce.Job; 
import org.apache.hadoop.mapreduce.Mapper; 
import org.apache.hadoop.mapreduce.Reducer; 
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser; 
public class WeatherDataProcessing { 
  public static void main(String [] args) throws Exception 
  { 
    Configuration c=new Configuration(); 
    String[] files=new GenericOptionsParser(c,args).getRemainingArgs(); 
    Path input=new Path(files[0]); 
    Path output=new Path(files[1]); 
    Job j=new Job(c,"WeatherDataProcessing"); 
    j.setJarByClass(WeatherDataProcessing.class); 
    j.setMapperClass(MapForWordCount.class); 
    j.setReducerClass(ReduceForWordCount.class); 
    j.setOutputKeyClass(Text.class); 
    j.setOutputValueClass(IntWritable.class); 
    FileInputFormat.addInputPath(j, input); 
    FileOutputFormat.setOutputPath(j, output); 
    System.exit(j.waitForCompletion(true)?0:1); 
  } 
public static class MapForWordCount extends Mapper<LongWritable, Text, Text, 
 IntWritable>{ 
  public void map(LongWritable key, Text value, Context con) throws 
IOException, InterruptedException
  { 
    String line = value.toString(); 
    String[] words=line.split(" "); 
    for(String word: words ) 
    { 
      Text outputKey = new Text(word.toUpperCase().trim()); 
      IntWritable outputValue = new IntWritable(1); 
      con.write(outputKey, outputValue); 
    } 
  } 
} 
public static class ReduceForWordCount extends Reducer<Text, IntWritable, Text, 
IntWritable> 
{ 
  public void reduce(Text word, Iterable<IntWritable> values, Context con) 
 throws IOException, InterruptedException 
  { 
    int sum = 0; 
    for(IntWritable value : values) 
    { 
      sum += value.get();
    } 
    con.write(word, new IntWritable(sum)); 
  } 
 } 
}
```
#### - check pkg name matches. and save check there is no errors.
### Creating of the jar file
#### - Select Project file right click /export/Java/JAR file . click next and save.
#### - Running the jar file
```Shell
hadoop jar C:\User\admin\dco...Your_Jar_file_path com.mapreduce.wc/WordCount /input/input.txt /output
```
#### - pkg/classname /hdfsinputdir/txtfilename /output_dir_name
#### - Wait untill hadoop mapreduces.
#### - Check for output dir in hdfs gui or see output in cmd
```Shell
hadoop dfs -cat /output/*
```
![image](https://github.com/adityarevankarp/Hadoop-Map_Reduce/assets/57789526/4166ebb1-c887-452b-bb81-114d664597b4)
## Problem 2: Map Reducing an Weather Dataset (Dataset given in the repo)
#### - Follow exact steps given above.
```Java
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
```







