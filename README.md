# flink-tensorflow

cd flinkTf && mvn clean install

cd flink

bin/start-cluster.sh

bin/flink run -c com.alibaba.Predict /path/../flink-tensorflow/flinkTf/target/flink.tf-1.0-SNAPSHOT.jar --input /path/../flink-tensorflow/iris_training.csv --model /path/../flink-tensorflow/model


