package com.amaiz.example;

import com.amaiz.example.cs.QuotesProcessor;
import com.amaiz.example.cs.aggregator.CandleAggregators;
import com.amaiz.example.cs.provider.ChartsProvider;
import com.amaiz.example.quotes.impl.RandomQuoteProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.util.Properties;

@Configuration
public class ChartServerConfig {

    @Autowired
    @Qualifier("instanceProperties")
    private Properties properties;


    @Bean
    public CandleAggregators candleAggregators() {
        return new CandleAggregators();
    }

    @Bean
    public QuotesProcessor quotesProcessor() {
        QuotesProcessor quotesProcessor = new QuotesProcessor();
        quotesProcessor.setAggregators(candleAggregators());
        quotesProcessor.setThreadsNumber(Integer.parseInt(properties.getProperty(ChartServerProperties.THREADS_NUMBER)));
        quotesProcessor.start();
        return quotesProcessor;
    }

    @Bean
    public RandomQuoteProvider randomQuoteProvider() {
        RandomQuoteProvider provider = new RandomQuoteProvider();
        provider.setSymbolsNumber(Integer.parseInt(properties.getProperty(ChartServerProperties.QUOTE_GEN_SYMBOLS)));
        provider.setDurationSeconds(Integer.parseInt(properties.getProperty(ChartServerProperties.QUOTE_GEN_DURATION)));
        provider.setQuotesPerSecond(Integer.parseInt(properties.getProperty(ChartServerProperties.QUOTE_GEN_RATE)));
        provider.setQuotesProcessor(quotesProcessor());
        provider.start();
        return provider;
    }

    @Bean
    public ChartsProvider chartsProvider() {
        ChartsProvider chartsProvider = new ChartsProvider();
        chartsProvider.setAggregators(candleAggregators());
        return chartsProvider;
    }

    @Bean(name = "instanceProperties")
    public PropertiesFactoryBean instancePropertiesFactoryBean() {
        PropertiesFactoryBean bean = new PropertiesFactoryBean();
        bean.setLocation(new ClassPathResource(System.getProperty("cs.conf")));
        return bean;
    }


}
