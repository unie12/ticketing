package com.example.ticketing.config;

import com.example.ticketing.model.user.UserActivityEvent;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * 고려해야할 사항
     * 1. 컨슈머 리밸런싱 전략 설정 + heartbeat, max_poll 등
     * 2. 브로커와 클러스터 구성 -> 현재 단일 브로커인데 클러스터 구성 확장 가능
     * 3. 다중 프로듀서-컨슈머 설정 -> 동시 처리 스레드 + 배치 리스닝 + 에러 핸들링 등
     * 4. 토픽 설정 -> cleanup_policy, retention_ms, segment_bytes, segment_ms 등
     * 5. 모니터링 및 메트릭스 추가
     */

    /**
     * producer 설정
     */
    @Bean
    public ProducerFactory<String, UserActivityEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers); // kafka 브로커 주소 -> localhost:9092
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // 메시지 키의 직렬화 방식 -> String
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class); // 메시지 값의 직렬화 방식 -> JSON
        config.put(ProducerConfig.ACKS_CONFIG, "all"); // replica가 메시지 제대로 수신했는지 acks=0, 1, all
        config.put(ProducerConfig.RETRIES_CONFIG, 3); // 전송 실패 재시도 횟수

        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * consumer 설정
     */
    @Bean
    public ConsumerFactory<String, UserActivityEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analytics-group"); // 컨슈머 그룹 식별자
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 처음부터 메시지 소비

        // UserActivityEvent 객체로 역직렬화
        JsonDeserializer<UserActivityEvent> jsonDeserializer = new JsonDeserializer<>(UserActivityEvent.class);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    /**
     * Listener Container Factory
     * @KafkaListener 어노테이션이 사용할 컨테이너 팩토리
     * 메시지 리스너의 동시 처리 설정
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserActivityEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserActivityEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    /**
     * Kafka template
     * 메시지 프로듀서의 편리한 사용을 위한 템플릿
     * 높은 수준의 추상화 제공
     */
    @Bean
    public KafkaTemplate<String, UserActivityEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
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
    public NewTopic userActivitiesTopic() {
        return TopicBuilder.name("user-activities")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
