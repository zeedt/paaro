package com.plethub.paaro.core.configuration.service;

import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.repository.ConfigurationRepository;
import com.plethub.paaro.core.configuration.apimodel.ActionResponse;
import com.plethub.paaro.core.configuration.apimodel.ActionWrapper;
import com.plethub.paaro.core.configuration.apimodel.ConfigurationResponse;
import com.plethub.paaro.core.models.Configuration;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.request.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class ConfigurationService {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private RequestService requestService;

    private Logger logger = LoggerFactory.getLogger(ConfigurationService.class.getName());

    @Transactional
    public ConfigurationResponse toggleMakerCheckerForAction(Action action, boolean enable) {

        try {
            Configuration configuration = configurationRepository.findTopByAction(action);

            if (configuration == null) {
                return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Configuration not found for specified action");
            }

            if (configuration.isMakerCheckerEnabled() == enable) {
                return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, String.format("Configuration already toggled to %s", Boolean.toString(enable)));
            }

            if (isMakerCheckerEnabledForAction(Action.TOGGLE_MAKER_CHECKER)) {
                String response = requestService.logRequest(configuration.getId(), Action.TOGGLE_MAKER_CHECKER, Boolean.toString(configuration.isMakerCheckerEnabled()), Boolean.toString(enable));
                if (StringUtils.isEmpty(response)) {
                    return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Maker checker seting awaiting verification");
                }
                return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.FAILED, response);
            }

            ManagedUser loggedInUser = requestService.getCurrentlyLoggedInUser();

            if (loggedInUser == null || loggedInUser.getId() == null) {
                return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.NULL_RESPONSE, "Unable to retrieve logged in user");
            }

            configuration.setMakerCheckerEnabled(enable);
            configuration.setUpdatedDate(new Date());
            configuration.setVerifiedBy(loggedInUser.getId());

            configurationRepository.save(configuration);

            return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Maker checker for Action successfully toggled");
        } catch (Exception e) {
            logger.error("System error occurred due to ", e);
            return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred when saving maker checker option for action");
        }

    }

    public ConfigurationResponse getConfigurationForAction(Action action) {

        try {
            if (action == null) {
                return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Action cannot be null");
            }

            Configuration configuration = configurationRepository.findTopByAction(action);

            if (configuration == null) {
                return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.NULL_RESPONSE, "No configuration found for action");
            }

            return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Configuration found", configuration);
        } catch (Exception e) {
            logger.error("Error occurred while getting configuration for action due to ", e);
            return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurrdd while getting configuration for action");
        }

    }

    public ConfigurationResponse getConfigurationListByPage(Integer pageNo, Integer pageSize) {

        try {
            pageNo = pageNo == null || pageNo < 0 ? 0 : pageNo;
            pageSize = pageNo == null || pageNo < 1 ? 15 : pageSize;

            PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

            Page<Configuration> configurationPage = configurationRepository.findAllByIdNotNull(pageRequest);

            return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL,"Configurations fetched", configurationPage);
        } catch (Exception e) {
            logger.error("Error occurred while fetching configuration list due to ", e);
            return ConfigurationResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }

    }





    public boolean isMakerCheckerEnabledForAction(Action action) {

        Configuration configuration = configurationRepository.findTopByAction(action);

        if (configuration == null || !configuration.isMakerCheckerEnabled()) {
            return false;
        }

        return true;

    }


    public ActionResponse getActionList() {
        List<Action> actionList = Arrays.asList(Action.values());

        final List<ActionWrapper> actionWrappers = new ArrayList<>();

        if (CollectionUtils.isEmpty(actionList)) return ActionResponse.fromCodeAndNarration(ApiResponseCode.NULL_RESPONSE, "No action found");

        actionList.stream().forEach(action -> {
            if (action != null) {
                ActionWrapper actionWrapper = new ActionWrapper();
                actionWrapper.setAction(action);
                actionWrapper.setActionDescription(action.getDecsription());
                actionWrappers.add(actionWrapper);
            }
        });
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        actionResponse.setActionWrapperList(actionWrappers);

        return actionResponse;

    }

    public ActionResponse getPossiblePendingActionList() {
        List<Action> actionList = Arrays.asList(Action.values());

        final List<ActionWrapper> actionWrappers = new ArrayList<>();

        if (CollectionUtils.isEmpty(actionList)) return ActionResponse.fromCodeAndNarration(ApiResponseCode.NULL_RESPONSE, "No action found");

        actionList.stream().forEach(action -> {
            if (action != null && !StringUtils.isEmpty(action.getDecsription()) && action.getDecsription().toLowerCase().contains("initiation")) {
                ActionWrapper actionWrapper = new ActionWrapper();
                actionWrapper.setAction(action);
                actionWrapper.setActionDescription(action.getDecsription());
                actionWrappers.add(actionWrapper);
            }
        });
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        actionResponse.setActionWrapperList(actionWrappers);

        return actionResponse;

    }

    public ActionResponse getPossibleVerificationActionList() {
        List<Action> actionList = Arrays.asList(Action.values());

        final List<ActionWrapper> actionWrappers = new ArrayList<>();

        if (CollectionUtils.isEmpty(actionList)) return ActionResponse.fromCodeAndNarration(ApiResponseCode.NULL_RESPONSE, "No action found");

        actionList.stream().forEach(action -> {
            if (action != null && !StringUtils.isEmpty(action.getDecsription()) && !action.getDecsription().toLowerCase().contains("initiation")) {
                ActionWrapper actionWrapper = new ActionWrapper();
                actionWrapper.setAction(action);
                actionWrapper.setActionDescription(action.getDecsription());
                actionWrappers.add(actionWrapper);
            }
        });
        ActionResponse actionResponse = new ActionResponse();
        actionResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        actionResponse.setActionWrapperList(actionWrappers);

        return actionResponse;

    }



}
