# flink-tensorflow

From the project, you can see three things. flinkTf is the flink java program which use the tensorflow java api to do inference job.

iris_test.csv is the predict dataset.

model is the tensorflow model.

To reproduce the issue. You may need to do the following steps please.

Firstly, download the flink of version 1.10.0 from https://mirror.bit.edu.cn/apache/flink/flink-1.10.0/flink-1.10.0-bin-scala_2.11.tgz. 

Secondly, git clone this project. Do the command line: cd flinkTf && mvn clean install

Then, cd flink-1.10.0 && bin/start-cluster.sh

bin/flink run -c com.alibaba.Predict /path/../flink-tensorflow/flinkTf/target/flink.tf-1.0-SNAPSHOT.jar --input /path/../flink-tensorflow/iris_test.csv --model /path/../flink-tensorflow/model

pay attention to the parm in the comman line!!

/path/../flink-tensorflow/flinkTf/target/flink.tf-1.0-SNAPSHOT.jar is the path where your jar is

/path/../flink-tensorflow/iris_test.csv is the path to the iris_test.csv

/path/../flink-tensorflow/model is the path to the model

After running the 'bin/flink run...' bash， it works fine the first time.

And you can see the result.csv in the /tmp/

But when you bin/flink run .. again, the flink program throws an error.

You can find log in the path /path/../flink-1.10.0/log/*-taskexecutor-0-yiwen.local.out

  [libprotobuf ERROR external/protobuf_archive/src/google/protobuf/descriptor_    database.cc:58] File already exists in database: tensorflow/core/protobuf/ea    ger_service.proto
  [libprotobuf FATAL external/protobuf_archive/src/google/protobuf/descriptor.    cc:1358] CHECK failed: GeneratedDatabase()->Add(encoded_file_descriptor, siz    e):
  libc++abi.dylib: terminating with uncaught exception of type google::protobu    f::FatalException: CHECK failed: GeneratedDatabase()->Add(encoded_file_descr    iptor, size):

