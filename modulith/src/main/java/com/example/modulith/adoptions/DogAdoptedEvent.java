package com.example.modulith.adoptions;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.DirectChannelSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.file.dsl.Files;
import org.springframework.messaging.MessageChannel;
import org.springframework.modulith.events.Externalized;

import java.io.File;

@Externalized(AdoptionsIntegrationConfiguration.ADOPTIONS_CHANNEL)
public record DogAdoptedEvent(int dogId) {
}

@Configuration
class AdoptionsIntegrationConfiguration {

    static final String ADOPTIONS_CHANNEL = "adoptions";

    @Bean(ADOPTIONS_CHANNEL)
    DirectChannelSpec messageChannel() {
        return MessageChannels.direct();
    }

    @Bean
    IntegrationFlow integrationFlow(
            @Qualifier(ADOPTIONS_CHANNEL) MessageChannel inbound,
            @Value("file://${HOME}/Desktop/out") File out
    ) {
        return IntegrationFlow
                .from(inbound)
                .handle((payload, headers) -> {
                    IO.println("received " + payload);
                    headers.forEach((key, value) -> IO.println(key + " -> " + value));
                    return payload;
                })
                .transform(DogAdoptedEvent::dogId)
                .transform(String::valueOf)
                .handle(Files.outboundAdapter(out).autoCreateDirectory(true))
//                .filter(payload -> payload.dogId > 0)
//                .route()
//                .split()
//                .aggregate()
//                .transform()
//                .
                .get();
    }
}