package com.ssafy.pillme.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

@Configuration
public class ESConfig extends ReactiveElasticsearchConfiguration {
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration
                .builder()
                .connectedTo("localhost:9200")
                .usingSsl()
                .withBasicAuth("elastic", "dKRbrylAMPbSDRWmx9E-")
                .build();
    }
}
