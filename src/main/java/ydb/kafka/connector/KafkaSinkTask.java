package ydb.kafka.connector;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.NopAuthProvider;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.TableClient;
import tech.ydb.table.transaction.TxControl;
import ydb.kafka.connector.config.KafkaSinkConnectorConfig;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class KafkaSinkTask extends SinkTask {
    private final static Logger log = LoggerFactory.getLogger(KafkaSinkTask.class);
    private String connectorName;
    private String connectionString;
    private GrpcTransport transport;
    private AuthProvider authProvider = NopAuthProvider.INSTANCE;
    private TableClient tableClient;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        try {
            log.info("Task config setting : " + props.toString());
            KafkaSinkConnectorConfig config;
            connectorName = props.get("name");
            config = new KafkaSinkConnectorConfig(props);

            connectionString = KafkaSinkConnectorConfig.SINK_SERVER_CONNECTION_STRING;
            transport = GrpcTransport.forConnectionString(connectionString).withAuthProvider(authProvider).build();
            tableClient = TableClient.newClient(transport).build();

        } catch (Exception e) {
            throw new ConnectException(e.getMessage(), e);
        }

    }

    @Override
    public void put(Collection<SinkRecord> records) {
        for (SinkRecord record : records) {
            if (record.value() != null) {
                try {
                    String value = record.value().toString();
                    sendRecord(value);
                } catch (Exception e) {
                    log.error(e.getMessage() + " / " + connectorName, e);
                }
            }
        }

    }

    private void sendRecord(String value) {
        Random rand = new Random(); // fixme сделать нормальный id
        String query // fixme сделать нормальный запрос
                = "UPSERT INTO mytopic (id, data) "
                + "VALUES (" + rand.nextInt() + ", \"" + value + "\");";
        SessionRetryContext retryCtx = SessionRetryContext.create(tableClient).build();

        // Begin new transaction with SerializableRW mode
        TxControl txControl = TxControl.serializableRw().setCommitTx(true);

        // Executes data query with specified transaction control settings.
        retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join().getValue();


    }

    @Override
    public void flush(Map<TopicPartition, OffsetAndMetadata> offsets) {
    }

    @Override
    public void stop() {
//        producer.flush(); // fixme добавить закрытие ресурсов коннекшна к бд
//        producer.close();
    }
}