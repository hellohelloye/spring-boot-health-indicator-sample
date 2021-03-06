package de.mdoninger.sample.health;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.api.core.client.HornetQClient;
import org.hornetq.api.core.client.ServerLocator;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class SpringBootHealthIndicatorSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootHealthIndicatorSampleApplication.class, args);
    }

    @Bean
    @DependsOn("hornetQServer")
    public ClientSessionFactory clientSessionFactory() throws Exception {
        ServerLocator serverLocator = HornetQClient.createServerLocatorWithoutHA(new TransportConfiguration(InVMConnectorFactory.class.getName()));
        return serverLocator.createSessionFactory();
    }

    @Service
    static class MessageProducer implements CommandLineRunner {

        private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);

        private final JmsTemplate jmsTemplate;

        @Autowired
        public MessageProducer(JmsTemplate jmsTemplate) {
            this.jmsTemplate = jmsTemplate;
        }

        @Override
        public void run(String... strings) throws Exception {
            process("Hello World");
        }

        public void process(String msg) {
            logger.info("============= Sending " + msg);
            for (int i = 0; i < 20; i++) {
                this.jmsTemplate.convertAndSend("testQueue", msg + 1);
                this.jmsTemplate.convertAndSend("testQueue2", msg + 1);
            }
        }
    }
}
