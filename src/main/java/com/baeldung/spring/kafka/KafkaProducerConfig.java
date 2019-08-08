package com.baeldung.spring.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //设置消息缓存大小，默认32M，此时设置为64M（实际参数设置要根据每条的消息量来进行压测）
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 67108864);
        //设置batch的大小，默认16k，此时为128K，（多个消息收集成一个batch）
        //如果设置过大会造成消息延迟
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 131072);
        //batch无法凑满设置时间linger.ms 100ms就发送消息
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        //发送请求的数据量大小  10M
        configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 10485760);
        //acks默认1 磁盘写入partitions leader就认为发送成功
        configProps.put(ProducerConfig.ACKS_CONFIG, "1");
        //retries和retries.backoff.ms决定重试机制
        //消息发送失败后可以重试几次
        configProps.put(ProducerConfig.RETRIES_CONFIG, 10);
        //每次重试的间隔是多少毫秒
        configProps.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 500);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public ProducerFactory<String, Greeting> greetingProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
    
    @Bean
    public KafkaTemplate<String, Greeting> greetingKafkaTemplate() {
        return new KafkaTemplate<>(greetingProducerFactory());
    }
    
}
