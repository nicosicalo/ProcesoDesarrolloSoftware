package com.escrims.infra.services;

import com.escrims.domain.events.DomainEventBus;
import com.escrims.infra.factory.DevNotifierFactory;
import com.escrims.infra.factory.NotifierFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfig {
    @Bean
    public DomainEventBus bus(){ return new DomainEventBus(); }

    @Bean
    public NotifierFactory notifierFactory(){ return new DevNotifierFactory(); }
}
