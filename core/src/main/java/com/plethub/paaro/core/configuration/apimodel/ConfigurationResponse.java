package com.plethub.paaro.core.configuration.apimodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.Configuration;
import org.springframework.data.domain.Page;

import java.util.List;

public class ConfigurationResponse {

    private ApiResponseCode apiResponseCode;

    private String message;

    private Configuration configuration;

    private List<Configuration> configurationList;

    private Page<Configuration> configurationPage;

    public static ConfigurationResponse fromCodeAndNarration(ApiResponseCode apiResponseCode, String message) {

        ConfigurationResponse configurationResponse = new ConfigurationResponse();
        configurationResponse.setApiResponseCode(apiResponseCode);
        configurationResponse.setMessage(message);

        return configurationResponse;
    }

    public static ConfigurationResponse fromCodeAndNarration(ApiResponseCode apiResponseCode, String message, Configuration configuration) {

        ConfigurationResponse configurationResponse = new ConfigurationResponse();
        configurationResponse.setApiResponseCode(apiResponseCode);
        configurationResponse.setMessage(message);
        configurationResponse.setConfiguration(configuration);

        return configurationResponse;
    }

    public static ConfigurationResponse fromCodeAndNarration(ApiResponseCode apiResponseCode, String message, Page<Configuration> configurationPage) {

        ConfigurationResponse configurationResponse = new ConfigurationResponse();
        configurationResponse.setApiResponseCode(apiResponseCode);
        configurationResponse.setMessage(message);
        configurationResponse.setConfigurationPage(configurationPage);

        return configurationResponse;
    }

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<Configuration> getConfigurationList() {
        return configurationList;
    }

    public void setConfigurationList(List<Configuration> configurationList) {
        this.configurationList = configurationList;
    }

    public Page<Configuration> getConfigurationPage() {
        return configurationPage;
    }

    public void setConfigurationPage(Page<Configuration> configurationPage) {
        this.configurationPage = configurationPage;
    }
}
