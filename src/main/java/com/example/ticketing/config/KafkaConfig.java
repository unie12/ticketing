package com.example.ticketing.config;

import com.example.ticketing.model.user.ReviewActivityEvent;
import com.example.ticketing.model.user.SearchActivityEvent;
import com.example.ticketing.model.user.StoreViewActivityEvent;
import com.example.ticketing.model.user.UserActivityEvent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.TypeNameIdResolver;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ObjectMapper kafkaObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // 다형성 처리 비활성화
        mapper.deactivateDefaultTyping();
        return mapper;
    }


    /**
     * 고려해야할 사항
     * 1. 컨슈머 리밸런싱 전략 설정 + heartbeat, max_poll 등
     * 2. 브로커와 클러스터 구성 -> 현재 단일 브로커인데 클러스터 구성 확장 가능
     * 3. 다중 프로듀서-컨슈머 설정 -> 동시 처리 스레드 + 배치 리스닝 + 에러 핸들링 등
     * 4. 토픽 설정 -> cleanup_policy, retention_ms, segment_bytes, segment_ms 등
     * 5. 모니터링 및 메트릭스 추가
     * 6. 메시지 전송 신뢰성
     * 7. 파티셔닝 전략 (사용자 id 기반.. ++)
     */

    /**
     * producer 설정
     */
    @Bean
    public <T extends UserActivityEvent> ProducerFactory<String, T> producerFactory() {
        JsonSerializer<T> serializer = new JsonSerializer<>(kafkaObjectMapper());
        serializer.setAddTypeInfo(true);

        return new DefaultKafkaProducerFactory<>(
                getDefaultProducerConfig(),
                new StringSerializer(),
                serializer
        );
    }


    private Map<String, Object> getDefaultProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1); // 순서 보장을 위해

        return config;
    }

    /**
     * consumer 설정
     */
    @Bean
    public <T extends UserActivityEvent> ConsumerFactory<String, T> consumerFactory(Class<T> eventType) {
        Map<String, Object> props = getDefaultConsumerConfig();
        JsonDeserializer<T> deserializer = new JsonDeserializer<>(eventType, kafkaObjectMapper());
        deserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    private Map<String, Object> getDefaultConsumerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analytics-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000);
        config.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                Collections.singletonList(RoundRobinAssignor.class));

        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return config;
    }

    /**
     * Listener Container Factory
     * @KafkaListener 어노테이션이 사용할 컨테이너 팩토리
     * 메시지 리스너의 동시 처리 설정
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SearchActivityEvent> searchListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SearchActivityEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(SearchActivityEvent.class));
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(searchKafkaTemplate()),
                new FixedBackOff(1000L, 2)
        ));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StoreViewActivityEvent> storeViewListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StoreViewActivityEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(StoreViewActivityEvent.class));
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(storeViewKafkaTemplate()),
                new FixedBackOff(1000L, 2)
        ));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReviewActivityEvent> reviewListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReviewActivityEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory(ReviewActivityEvent.class));
        factory.setConcurrency(3);
        factory.setBatchListener(true);
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(reviewKafkaTemplate()),
                new FixedBackOff(1000L, 2)
        ));
        return factory;
    }

    /**
     * Kafka template
     * 메시지 프로듀서의 편리한 사용을 위한 템플릿
     * 높은 수준의 추상화 제공
     */

    @Bean
    public KafkaTemplate<String, SearchActivityEvent> searchKafkaTemplate() {
        return createKafkaTemplate("search-events");
    }

    @Bean
    public KafkaTemplate<String, StoreViewActivityEvent> storeViewKafkaTemplate() {
        return createKafkaTemplate("store-views");
    }

    @Bean
    public KafkaTemplate<String, ReviewActivityEvent> reviewKafkaTemplate() {
        return createKafkaTemplate("review-events");
    }

    private <T extends UserActivityEvent> KafkaTemplate<String, T> createKafkaTemplate(String defaultTopic) {
        KafkaTemplate<String, T> template = new KafkaTemplate<>(producerFactory());
        template.setDefaultTopic(defaultTopic);
        template.setObservationEnabled(true);
        return template;
    }

    private <T> ProducerListener<String, T> getProducerListener() {
        return new ProducerListener<>() {
            @Override
            public void onSuccess(ProducerRecord<String, T> producerRecord, RecordMetadata metadata) {
                log.debug("Message sent Successfully: topic={}, partition={}, offset={}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }

            @Override
            public void onError(ProducerRecord<String, T> record,
                                RecordMetadata metadata,
                                Exception exception) {
                log.error("Failed to send message: topic={}, value={}",
                        record.topic(), record.value(), exception);
            }
        };
    }
    /**
     * admin 설정
     * kafka 관리 작업을 위한 admin 클라이언트
     * 토픽 생성, 삭제 등의 관리 작업 수행
     */
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    /**
     * 토픽 설정
     *
     * 1. 토픽 이름: user-activities
     * 2. 파티션 수: 3 (병렬 처리)
     * 3. 복제 팩터: 1 (개발 환경용)
     */
    @Bean
    public NewTopic searchEventsTopic() {
        return TopicBuilder.name("search-events")
                .partitions(6)
                .configs(Map.of(
                        TopicConfig.RETENTION_MS_CONFIG, "86400000",
                        TopicConfig.CLEANUP_POLICY_CONFIG, "delete"
                ))
                .build();
    }

    @Bean
    public NewTopic storeViewEventsTopic() {
        return TopicBuilder.name("store-views")
                .partitions(6)
                .configs(Map.of(
                        TopicConfig.RETENTION_MS_CONFIG, "86400000",
                        TopicConfig.CLEANUP_POLICY_CONFIG, "delete"
                ))
                .build();
    }

    @Bean
    public NewTopic reviewEventsTopic() {
        return TopicBuilder.name("review-events")
                .partitions(3)
                .configs(Map.of(
                        TopicConfig.RETENTION_MS_CONFIG, "604800000",
                        TopicConfig.CLEANUP_POLICY_CONFIG, "delete"
                ))
                .build();
    }

    @Bean
    public NewTopic interactionEventsTopic() {
        return TopicBuilder.name("interaction-events")
                .partitions(3)
                .configs(Map.of(
                        TopicConfig.RETENTION_MS_CONFIG, "604800000",
                        TopicConfig.CLEANUP_POLICY_CONFIG, "delete"
                ))
                .build();
    }
}
