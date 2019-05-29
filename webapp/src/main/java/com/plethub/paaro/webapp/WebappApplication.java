package com.plethub.paaro.webapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@SpringBootApplication
@EnableConfigurationProperties
@ComponentScan(basePackages = {"com.plethub.paaro"})
public class WebappApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
//        BigDecimal one = new BigDecimal(2000);
//        String test = NumberFormat.getInstance().format(one);
//        System.out.println(test);
    }
}
