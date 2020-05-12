# flink-tensorflow

From the project, you can see three things. flinkTf is the flink java program which use the tensorflow java api to do inference job.

iris_test.csv is the predict dataset.

model is the tensorflow model.

To reproduce the issue. You may need to do the following steps please.

Firstly, download the flink of version 1.10.0 from https://mirror.bit.edu.cn/apache/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.11.tgz. and rename the directory to flink.

Secondly, git clone this project. Do the command line: cd flinkTf && mvn clean install

Then, cd flink-1.10.0 && bin/start-cluster.sh

bin/flink run -c com.alibaba.Predict /path/../flink-tensorflow/flinkTf/target/flink.tf-1.0-SNAPSHOT.jar --input /path/../flink-tensorflow/iris_test.csv --model /path/../flink-tensorflow/model

pay attention to the parm in the comman line!!

/path/../flink-tensorflow/flinkTf/target/flink.tf-1.0-SNAPSHOT.jar is the path where your jar is

/path/../flink-tensorflow/iris_test.csv is the path to the iris_test.csv

/path/../flink-tensorflow/model is the path to the model
