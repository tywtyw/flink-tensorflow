package com.alibaba;

import com.alibaba.flink.ml.lib.tensorflow.TFInferenceUDTF;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import org.apache.flink.api.java.utils.MultipleParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.Types;
import org.apache.flink.table.api.java.StreamTableEnvironment;
import org.apache.flink.table.factories.TableFactoryService;
import org.apache.flink.table.factories.TableSourceFactory;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.table.functions.TableFunction;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sources.TableSource;
import org.apache.flink.table.types.DataType;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class Predict {
    private static final Logger LOG = LoggerFactory.getLogger(Predict.class);

    public static class Source extends ScalarFunction{
        public float[] eval (float... objects){
            return objects;
        }
    }
    public static class MyFlatMapFunction extends TableFunction<Row> {

        public void eval(float[] array) {
            collect(Row.of(array[0], array[1], array[2]));
        }

        @Override
        public TypeInformation<Row> getResultType() {
            return org.apache.flink.table.api.Types.ROW(org.apache.flink.table.api.Types.FLOAT(), org.apache.flink.table.api.Types.FLOAT(), Types.FLOAT());
        }
    }

    public static void main(String[] args) throws Exception {
        final MultipleParameterTool params = MultipleParameterTool.fromArgs(args);
        String csvPath = null;
        if(params.has("input")){
            csvPath = params.get("input");
            LOG.info("Predict sample: {}", csvPath);
        }
        Preconditions.checkNotNull(csvPath, "Input should not be null.");

        EnvironmentSettings bsSettings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        StreamExecutionEnvironment bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
        bsEnv.setParallelism(1);
        StreamTableEnvironment bsTableEnv = StreamTableEnvironment.create(bsEnv, bsSettings);

        Map<String, String> args_source = new HashMap<>();
        args_source.put("connector.type","filesystem");
        args_source.put("connector.path", csvPath);
        args_source.put("format.type", "csv");
        args_source.put("connector.property-version", "1");
        args_source.put("format.property-version", "1");

        args_source.put("format.fields.0.type", "FLOAT");
        args_source.put("format.fields.1.type", "FLOAT");
        args_source.put("format.fields.2.type", "FLOAT");
        args_source.put("format.fields.3.type", "FLOAT");
        args_source.put("format.fields.4.type" , "INT");

        args_source.put("format.fields.0.name", "sepal_length");
        args_source.put("format.fields.1.name", "sepal_width");
        args_source.put("format.fields.2.name", "petal_length");
        args_source.put("format.fields.3.name", "petal_width");
        args_source.put("format.fields.4.name", "variety");

        args_source.put("schema.0.type", "FLOAT");
        args_source.put("schema.1.type", "FLOAT");
        args_source.put("schema.2.type", "FLOAT");
        args_source.put("schema.3.type", "FLOAT");
        args_source.put("schema.4.type", "INT");

        args_source.put("schema.0.name", "sepal_length");
        args_source.put("schema.1.name", "sepal_width");
        args_source.put("schema.2.name", "petal_length");
        args_source.put("schema.3.name", "petal_width");
        args_source.put("schema.4.name", "variety");
        TableSourceFactory factory = TableFactoryService.find(TableSourceFactory.class, args_source);
        TableSource tableSource = factory.createTableSource(args_source);
        bsTableEnv.registerTableSource("source", tableSource);
        Table input = bsTableEnv.from("source");
        bsTableEnv.registerTableSink("sink", new CsvTableSink("/tmp/result.csv",
                ",",
                1, org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE,new String[]{"a","b","c"},new DataType[]{DataTypes.FLOAT(),DataTypes.FLOAT(),DataTypes.FLOAT()}));
        String modelDir = null;
        if (params.has("model")){
            modelDir = params.get("model");
        }
        String inputNames = "aaa_input";
        String inputTypes = "DT_FLOAT";
        String inputRanks = "1";
        String outputNames = "bbb";
        String outputTypes = "DT_FLOAT";
        String outputRanks = "1";
        TFInferenceUDTF predictUDTF = new TFInferenceUDTF(modelDir, inputNames, inputTypes, inputRanks,
                outputNames, outputTypes, outputRanks,
                new Properties(), 1);
        bsTableEnv.registerFunction("source", new Source());
        bsTableEnv.registerFunction("predict", predictUDTF);
        bsTableEnv.registerFunction("flatmap", new MyFlatMapFunction());
        Table source = input.select("source(sepal_length, sepal_width, petal_length, petal_width) as aaa_input").select("aaa_input");
        Table s = source.joinLateral("predict(aaa_input) as bbb").select("bbb").flatMap("flatmap(bbb)").as("f1,f2,f3");
        s.insertInto("sink");
        bsTableEnv.execute("job");

    }
}

