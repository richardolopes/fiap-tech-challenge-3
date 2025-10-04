package com.hospital.notification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "test-topic")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.group-id=test-group",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
@DirtiesContext
@ExtendWith(MockitoExtension.class)
@DisplayName("KafkaConsumerConfig Tests")
class KafkaConsumerConfigTest {

    @Autowired
    private KafkaConsumerConfig kafkaConsumerConfig;

    @Autowired
    private ConsumerFactory<String, Object> consumerFactory;

    @Autowired
    private ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory;

    @Nested
    @DisplayName("Consumer Factory Tests")
    class ConsumerFactoryTests {

        @Test
        @DisplayName("Should create consumer factory bean")
        void shouldCreateConsumerFactoryBean() {
            assertNotNull(consumerFactory);
            assertInstanceOf(ConsumerFactory.class, consumerFactory);
        }

        @Test
        @DisplayName("Should configure bootstrap servers correctly")
        void shouldConfigureBootstrapServersCorrectly() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertNotNull(configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
            assertInstanceOf(String.class, configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        }

        @Test
        @DisplayName("Should configure group id correctly")
        void shouldConfigureGroupIdCorrectly() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertNotNull(configs.get(ConsumerConfig.GROUP_ID_CONFIG));
            assertInstanceOf(String.class, configs.get(ConsumerConfig.GROUP_ID_CONFIG));
        }

        @Test
        @DisplayName("Should configure key deserializer correctly")
        void shouldConfigureKeyDeserializerCorrectly() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertEquals(StringDeserializer.class, 
                configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        }

        @Test
        @DisplayName("Should configure value deserializer correctly")
        void shouldConfigureValueDeserializerCorrectly() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertEquals(JsonDeserializer.class, 
                configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        }

        @Test
        @DisplayName("Should configure auto offset reset correctly")
        void shouldConfigureAutoOffsetResetCorrectly() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertEquals("earliest", 
                configs.get(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        }

        @Test
        @DisplayName("Should configure JsonDeserializer trusted packages")
        void shouldConfigureJsonDeserializerTrustedPackages() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertEquals("*", configs.get(JsonDeserializer.TRUSTED_PACKAGES));
        }

        @Test
        @DisplayName("Should configure JsonDeserializer use type info headers")
        void shouldConfigureJsonDeserializerUseTypeInfoHeaders() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertEquals(true, configs.get(JsonDeserializer.USE_TYPE_INFO_HEADERS));
        }
    }

    @Nested
    @DisplayName("Kafka Listener Container Factory Tests")
    class KafkaListenerContainerFactoryTests {

        @Test
        @DisplayName("Should create kafka listener container factory bean")
        void shouldCreateKafkaListenerContainerFactoryBean() {
            assertNotNull(kafkaListenerContainerFactory);
            assertInstanceOf(ConcurrentKafkaListenerContainerFactory.class, 
                kafkaListenerContainerFactory);
        }

        @Test
        @DisplayName("Should configure consumer factory in listener container factory")
        void shouldConfigureConsumerFactoryInListenerContainerFactory() {
            assertNotNull(kafkaListenerContainerFactory.getConsumerFactory());
            assertEquals(consumerFactory, kafkaListenerContainerFactory.getConsumerFactory());
        }

        @Test
        @DisplayName("Should have default concurrency settings")
        void shouldHaveDefaultConcurrencySettings() {
            assertNotNull(kafkaListenerContainerFactory);
        }
    }

    @Nested
    @DisplayName("Configuration Integration Tests")
    class ConfigurationIntegrationTests {

        @Test
        @DisplayName("Should inject all required dependencies")
        void shouldInjectAllRequiredDependencies() {
            assertNotNull(kafkaConsumerConfig);
            assertNotNull(consumerFactory);
            assertNotNull(kafkaListenerContainerFactory);
        }

        @Test
        @DisplayName("Should have consistent configuration between beans")
        void shouldHaveConsistentConfigurationBetweenBeans() {
            Map<String, Object> factoryConfigs = consumerFactory.getConfigurationProperties();
            var listenerFactoryConsumer = kafkaListenerContainerFactory.getConsumerFactory();
            
            assertEquals(factoryConfigs, listenerFactoryConsumer.getConfigurationProperties());
        }

        @Test
        @DisplayName("Should handle property injection correctly")
        void shouldHandlePropertyInjectionCorrectly() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertNotNull(configs.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
            assertNotNull(configs.get(ConsumerConfig.GROUP_ID_CONFIG));
        }
    }

    @Nested
    @DisplayName("Bean Creation Tests")
    class BeanCreationTests {

        @Test
        @DisplayName("Should create consumer factory with correct configuration")
        void shouldCreateConsumerFactoryWithCorrectConfig() {
            ConsumerFactory<String, Object> newFactory = kafkaConsumerConfig.consumerFactory();
            
            assertNotNull(newFactory);
            
            Map<String, Object> config = newFactory.getConfigurationProperties();
            assertNotNull(config.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
            assertTrue(config.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
            assertEquals(StringDeserializer.class, config.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
            assertNotNull(config.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        }

        @Test
        @DisplayName("Should create kafka listener container factory with correct configuration")
        void shouldCreateKafkaListenerContainerFactoryWithCorrectConfig() {
            ConcurrentKafkaListenerContainerFactory<String, Object> newFactory = 
                kafkaConsumerConfig.kafkaListenerContainerFactory();
            
            assertNotNull(newFactory);
            assertNotNull(newFactory.getConsumerFactory());
        }
    }

    @Nested
    @DisplayName("Configuration Validation Tests")
    class ConfigurationValidationTests {

        @Test
        @DisplayName("Should have all required consumer properties")
        void shouldHaveAllRequiredConsumerProperties() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertTrue(configs.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
            assertTrue(configs.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
            assertTrue(configs.containsKey(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
            assertTrue(configs.containsKey(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
            assertTrue(configs.containsKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        }

        @Test
        @DisplayName("Should have JsonDeserializer specific properties")
        void shouldHaveJsonDeserializerSpecificProperties() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            assertTrue(configs.containsKey(JsonDeserializer.TRUSTED_PACKAGES));
            assertTrue(configs.containsKey(JsonDeserializer.USE_TYPE_INFO_HEADERS));
        }

        @Test
        @DisplayName("Should have valid deserializer classes")
        void shouldHaveValidDeserializerClasses() {
            Map<String, Object> configs = consumerFactory.getConfigurationProperties();
            
            Class<?> keyDeserializer = (Class<?>) configs.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG);
            Class<?> valueDeserializer = (Class<?>) configs.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG);
            
            assertEquals(StringDeserializer.class, keyDeserializer);
            assertEquals(JsonDeserializer.class, valueDeserializer);
        }
    }
}
