package ydb.kafka.connector.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

// fixme не используется
public class KafkaSinkConnectorConfig extends AbstractConfig {

    public static final String SOURCE_TOPIC = "topics";
    public static final String SOURCE_TOPIC_DEFAULT_VALUE = "mytopic";
    public static final String SOURCE_TOPIC_DOC = "Define source topic";

    public static final String SINK_BOOTSTRAP_SERVER = "kafka.sink.bootstrap";
    public static final String SINK_BOOTSTRAP_SERVER_DEFAULT_VALUE = "localhost:9092";
    public static final String SINK_BOOTSTRAP_SERVER_DOC = "Define sink bootstrap";

    public static final String SINK_SERVER_CONNECTION_STRING = "grpc://localhost:2136/local";


    public static ConfigDef CONFIG = new ConfigDef()
//            .define(SOURCE_TOPIC, Type.STRING, SOURCE_TOPIC_DEFAULT_VALUE, Importance.HIGH, SOURCE_TOPIC_DOC)
//            .define(SINK_BOOTSTRAP_SERVER, Type.STRING, SINK_BOOTSTRAP_SERVER_DEFAULT_VALUE, Importance.HIGH, SINK_BOOTSTRAP_SERVER_DOC)
            ;

    public KafkaSinkConnectorConfig(Map<String, String> props) {
        super(CONFIG, props);
    }
}