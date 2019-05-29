package com.plethub.paaro.core.usermanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.plethub.paaro.core.appservice.apiresponsemodel.UserDashboardDetails;
import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.appservice.repository.RequestRepository;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.infrastructure.exception.PaaroAuthenticationException;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.appservice.repository.ConfigurationRepository;
import com.plethub.paaro.core.appservice.services.WalletBackgroundService;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.service.RequestService;
import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.enums.ResponseStatus;
import com.plethub.paaro.core.usermanagement.enums.UserCategory;
import com.plethub.paaro.core.usermanagement.repository.AuthorityRepository;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserAuthorityRepository;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserRepository;
import com.plethub.paaro.core.usermanagement.apimodels.PasswordResetRequestModel;
import com.plethub.paaro.core.usermanagement.apimodels.UserUpdateRequestModel;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;

import static com.plethub.paaro.core.usermanagement.enums.ResponseStatus.PENDING_VERIFICATION;

@Service
public class UserService {


    private Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public ManagedUserRepository managedUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    private UserEmailService userEmailService;

    @Autowired
    private TokenStore jdbcTokenStore;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private WalletBackgroundService walletBackgroundService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    public ManagedUserAuthorityRepository managedUserAuthorityRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    public ManagedUserModelApi getUserModelByEmailOrDisplayName(String email, HttpServletRequest servletRequest) throws Exception {

        try {
            ManagedUser user = managedUserRepository.findManagedUserByEmailOrDisplayName(email);

            if (user == null) {
                return new ManagedUserModelApi(ResponseStatus.NOT_FOUND, "User not found");
            } else {
                List<Authority> authorities = getAuthoritiesByUserId(user.getId());

                auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User fetched user details with email %s", email));

                return new ManagedUserModelApi(user, authorities, ResponseStatus.SUCCESSFUL, "User found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while getting user model by email user due to ", e);
            throw new Exception("Unexpected error occured due to " + e);
        }

    }

    public ManagedUserModelApi getUserModelById(Long id, HttpServletRequest servletRequest) throws Exception {

        try {
            ManagedUser user = managedUserRepository.findOne(id);

            if (user == null) {
                return new ManagedUserModelApi(ResponseStatus.NOT_FOUND, "User not found");
            } else {
                List<Authority> authorities = getAuthoritiesByUserId(user.getId());
                auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User fetched user details with id %s", id));

                return new ManagedUserModelApi(user, authorities, ResponseStatus.SUCCESSFUL, "User found");
            }
        } catch (Exception e) {
            logger.error("Error occurred while getting user model by id user due to ", e);
            throw new Exception("Unexpected error occured due to " + e);
        }

    }

    public List<Authority> getAuthoritiesByUserId(Long id) {

        try {
            List<ManagedUserAuthority> managedUserAuthorities = managedUserAuthorityRepository.findAllByManagedUserId(id);

            List<Long> authoritiesIds = new ArrayList<>();

            if (null != managedUserAuthorities) {
                managedUserAuthorities.stream().parallel().forEach(managedUserAuthority -> {
                    authoritiesIds.add(managedUserAuthority.getAuthorityId());
                });
            }

            return authorityRepository.findAllByIdIn(authoritiesIds);

        } catch (Exception e) {
            logger.error("Error occurred while fetching user authorities due to ", e);
            return new ArrayList<>();
        }

    }

    public ManagedUserModelApi addManagedUser(ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest) {

        try {
            if (managedUserModelApi == null || Strings.isNullOrEmpty(managedUserModelApi.getEmail())) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email cannot be null");
            }

            if (Strings.isNullOrEmpty(managedUserModelApi.getDisplayName())) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Display name cannot be null");
            }

            ManagedUser user = managedUserRepository.findOneByEmail(managedUserModelApi.getEmail());
            if (user != null) {
                return new ManagedUserModelApi(ResponseStatus.ALREADY_EXIST, "Username already exist");
            }

            user = managedUserRepository.findOneByDisplayName(managedUserModelApi.getDisplayName());

            if (user != null) {
                return new ManagedUserModelApi(ResponseStatus.ALREADY_EXIST, "Display name already exists");
            }

            if (Strings.isNullOrEmpty(managedUserModelApi.getPassword()) || managedUserModelApi.getPassword().trim().length() < 6) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Password cannot be blank and must not be less than 6 characters ");
            }
            if (Strings.nullToEmpty(managedUserModelApi.getFirstName()).trim().length() < 3 || Strings.nullToEmpty(managedUserModelApi.getLastName()).trim().length() < 3) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "First name and last name cannot be less than 3 characters ");
            }
            if (Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() < 11 || Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() > 20) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Phone number must be between 11-20 characters");
            }
            ManagedUser managedUser = createManagedUserFromManagedUserApiModel(managedUserModelApi);

            managedUser.setDateCreated(new java.util.Date());
            managedUser.setActive(true);
            managedUser.setActivationCode(generateActivationCode(managedUserModelApi.getEmail()));
            managedUser.setUserCategory(UserCategory.CUSTOMER);
            managedUser.setPassword(passwordEncoder.encode(managedUserModelApi.getPassword()));
            managedUserRepository.save(managedUser);
            walletBackgroundService.createAllCurrencyWalletsForCustomer(managedUser);
            userEmailService.sendUserWelcomeEmail(managedUserModelApi.getEmail(), managedUserModelApi.getFirstName(), managedUser.getActivationCode());
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("New user %s created", managedUser.getEmail()));
            return new ManagedUserModelApi(managedUser, null, null, ResponseStatus.SUCCESSFUL);
        } catch (Exception e) {
            logger.error("Error occurred while creating user due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "Error occurred while creating user due to " + e.getCause().toString());
        }
    }

    public ManagedUserModelApi deactivateUser(String email, HttpServletRequest servletRequest) {

        try {
            ManagedUser user = managedUserRepository.findOneByEmail(email);
            if (user == null) {
                return new ManagedUserModelApi(null, null, ResponseStatus.NOT_FOUND, "User not found");
            }

            if (!user.isActive())
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "User already deactivated");

            if (requestService.isRequestPending(Action.DEACTIVATE_USER, user.getId()))
                return new ManagedUserModelApi(PENDING_VERIFICATION, "There's a pending verification on the user account");
            if (requestService.isRequestPending(Action.ACTIVATE_USER, user.getId()))
                return new ManagedUserModelApi(PENDING_VERIFICATION, "There's a pending verification on the user account");
            Configuration configuration = configurationRepository.findTopByAction(Action.DEACTIVATE_USER);

            if (configuration != null && configuration.isMakerCheckerEnabled()) {
                String response = requestService.logRequest(user.getId(), Action.DEACTIVATE_USER);
                if (StringUtils.isEmpty(response)) {
                    return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Deactivation Request awaiting verification");
                }
                return new ManagedUserModelApi(ResponseStatus.FAILED, response);
            }
            user.setActive(false);
            managedUserRepository.save(user);
            userEmailService.sendDeactivateAccountEmail(user);
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User %s deactivated", email));
            return new ManagedUserModelApi(null, null, ResponseStatus.SUCCESSFUL, "User successfully deactivated");
        } catch (Exception e) {
            logger.error("Error occurred while deactivating user due to ", e);
            return new ManagedUserModelApi(null, null, ResponseStatus.SYSTEM_ERROR, "Error occurred while deactivating due to " + e.getCause().toString());
        }
    }

    public ManagedUserModelApi activateUser(String email, HttpServletRequest servletRequest) {

        try {
            ManagedUser user = managedUserRepository.findOneByEmail(email);
            if (user == null) {
                return new ManagedUserModelApi(null, null, ResponseStatus.NOT_FOUND, "User not found");
            }
            if (user.isActive())
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "User already activated");

            if (requestService.isRequestPending(Action.DEACTIVATE_USER, user.getId()))
                return new ManagedUserModelApi(PENDING_VERIFICATION, "There's a pending verification on the user account");
            if (requestService.isRequestPending(Action.ACTIVATE_USER, user.getId()))
                return new ManagedUserModelApi(PENDING_VERIFICATION, "There's a pending verification on the user account");

            Configuration configuration = configurationRepository.findTopByAction(Action.ACTIVATE_USER);

            if (configuration != null && configuration.isMakerCheckerEnabled()) {
                String response = requestService.logRequest(user.getId(), Action.ACTIVATE_USER);
                if (StringUtils.isEmpty(response)) {
                    return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Activation Request awaiting verification");
                }
                return new ManagedUserModelApi(ResponseStatus.FAILED, response);
            }
            user.setActive(true);
            managedUserRepository.save(user);
            userEmailService.sendActivateAccountEmail(user);
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User %s activated", email));
            return new ManagedUserModelApi(null, null, ResponseStatus.SUCCESSFUL, "User successfully activated");
        } catch (Exception e) {
            logger.error("Error occurred while activating user due to ", e);
            return new ManagedUserModelApi(null, null, ResponseStatus.SYSTEM_ERROR, "Error occurred while activating due to " + e.getCause().toString());
        }
    }

    public ManagedUserModelApi resetUserPassword(PasswordResetRequestModel resetRequestModel, HttpServletRequest servletRequest) {

        if (resetRequestModel == null) {
            return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Invalid request");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        if (Strings.isNullOrEmpty(email)) {
            return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email not found for user");
        }

        if (Strings.isNullOrEmpty(resetRequestModel.getOldPassword()) || Strings.isNullOrEmpty(resetRequestModel.getNewPassword())) {
            return new ManagedUserModelApi( ResponseStatus.INVALID_REQUEST, "Both old and new passwords cannot be blank");
        }

        if (resetRequestModel.getNewPassword().trim().length() < 6) {
            return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Password must not be less than 6 characters ");
        }

        try {
            ManagedUser user = managedUserRepository.findOneByEmail(email);
            if (user == null) {
                return new ManagedUserModelApi( ResponseStatus.NOT_FOUND, "User not found");
            }
            boolean exist = passwordEncoder.matches(resetRequestModel.getOldPassword(), user.getPassword());
            if (!exist) {
                return new ManagedUserModelApi( ResponseStatus.NOT_FOUND, "Incorrect old password. Please supply correct password");
            }
            String newEncodedPassword = passwordEncoder.encode(resetRequestModel.getNewPassword());
            user.setPassword(newEncodedPassword);
            managedUserRepository.save(user);
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User password reset with email %s", email));
            return new ManagedUserModelApi( ResponseStatus.SUCCESSFUL, "Password reset successful");
        } catch (Exception e) {
            logger.error("Error occurred while resetting password due to ", e);
            return new ManagedUserModelApi( ResponseStatus.SYSTEM_ERROR, "Error occurred while resetting password due to " + e.getCause().toString());
        }
    }


    public ManagedUserModelApi updateUser(UserUpdateRequestModel requestModel, HttpServletRequest servletRequest) {

        try {
            if (requestModel == null) {
                return new ManagedUserModelApi( ResponseStatus.INVALID_REQUEST, "Invalid request");
            }
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

            if (StringUtils.isEmpty(email)) {
                return new ManagedUserModelApi(ResponseStatus.NOT_FOUND, "User is not logged in");
            }

            if (Strings.isNullOrEmpty(requestModel.getNewEmail())) {
                return new ManagedUserModelApi( ResponseStatus.INVALID_REQUEST, "Both old and new Email cannot be blank");
            }

            ManagedUser user = managedUserRepository.findManagedUserByEmailOrDisplayName(email);

            user.setLastName(requestModel.getLastName());
            user.setFirstName(requestModel.getFirstName());
            user.setPhoneNumber(requestModel.getPhoneNumber());
            managedUserRepository.save(user);
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User details %s updated", email));
            return new ManagedUserModelApi(user, null, ResponseStatus.SUCCESSFUL, "User information updated successfully");
        } catch (Exception e) {
            logger.error("Error occurred while updating details due to ==> ", e);
            return new ManagedUserModelApi( ResponseStatus.SYSTEM_ERROR, "System error occured while updating details due to ==> " + e.getCause().toString());
        }

    }

    public ManagedUserModelApi forgotPassword(ManagedUserModelApi modelApi, HttpServletRequest servletRequest) throws IOException {

        try {
            if (modelApi == null || StringUtils.isEmpty(modelApi.getEmail())) {
                return new ManagedUserModelApi(ResponseStatus.NOT_FOUND, "Email cannot be blank");
            }

            ManagedUser user = managedUserRepository.findOneByEmail(modelApi.getEmail());
            if (user == null) {
                return new ManagedUserModelApi(null, null, ResponseStatus.NOT_FOUND, "User not found");
            }

            String generatedString = RandomStringUtils.randomAlphanumeric(10);
            String encodedPassword = passwordEncoder.encode(generatedString);
            user.setPassword(encodedPassword);
            managedUserRepository.save(user);

            userEmailService.sendForgotPasswordEmail(user.getEmail(), generatedString, user.getFirstName());
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User %s password sent", user.getEmail()));
            return new ManagedUserModelApi(user, null, ResponseStatus.SUCCESSFUL, "New password has been sent to email");
        } catch (Exception e) {
            logger.error("Error occurred while generating new password");
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occured while generating new password due to ==> " + e.getCause().toString());
        }

    }

    public ManagedUserModelApi createAdminUser(ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest) {

        try {
            if (managedUserModelApi == null || Strings.isNullOrEmpty(managedUserModelApi.getEmail())) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email cannot be null");
            }

            ManagedUser user = managedUserRepository.findManagedUserByEmailOrDisplayName(managedUserModelApi.getEmail());
            if (user != null) {
                return new ManagedUserModelApi(ResponseStatus.ALREADY_EXIST, "Email already in use");
            }

            if (Strings.nullToEmpty(managedUserModelApi.getFirstName()).trim().length() < 3 || Strings.nullToEmpty(managedUserModelApi.getLastName()).trim().length() < 3) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "First name and last name cannot be less than 3 characters ");
            }
            if (Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() < 11 || Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() > 20) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Phone number must be between 11-20 characters");
            }

            Configuration configuration = configurationRepository.findTopByAction(Action.CREATE_USER);

            if (configuration != null && configuration.isMakerCheckerEnabled()) {
                return saveAdminCreationRequestForVerification(managedUserModelApi);
            }

            ManagedUser managedUser = createManagedUserFromManagedUserApiModel(managedUserModelApi);

            String generatedString = RandomStringUtils.randomAlphanumeric(10);
            String encodedPassword = passwordEncoder.encode(generatedString);
            managedUser.setDateCreated(new java.util.Date());
            managedUser.setActive(true);
            managedUser.setDisplayName(managedUser.getEmail());
            managedUser.setUserCategory(UserCategory.ADMIN);
            managedUser.setPassword(encodedPassword);
            managedUser.setPassword(passwordEncoder.encode(managedUserModelApi.getPassword()));
            managedUserRepository.save(managedUser);
            userEmailService.sendNewAdminUserEmail(managedUser.getEmail(), generatedString, managedUser.getFirstName(), managedUser.getActivationCode());
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("New admin user %s created", managedUser.getEmail()));
            return new ManagedUserModelApi(managedUser, null, ResponseStatus.SUCCESSFUL, "Admin user created successfully");
        } catch (Exception e) {
            logger.error("Error occurred while creating user due to ", e);
            return new ManagedUserModelApi(null, null, ResponseStatus.SYSTEM_ERROR, "Error occurred while creating admin user");
        }
    }

    private ManagedUser createManagedUserFromManagedUserApiModel(ManagedUserModelApi managedUserModelApi) {
        ManagedUser managedUser = new ManagedUser();
        BeanUtils.copyProperties(managedUserModelApi, managedUser);
        return managedUser;
    }

    public ManagedUserModelApi updateAdminUser(ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest) {

        try {
            if (managedUserModelApi == null || Strings.isNullOrEmpty(managedUserModelApi.getEmail())) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email cannot be null");
            }

            ManagedUser user = managedUserRepository.findManagedUserByEmailOrDisplayName(managedUserModelApi.getEmail());
            if (user == null) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email not valid");
            }

            if (Strings.nullToEmpty(managedUserModelApi.getFirstName()).trim().length() < 3 || Strings.nullToEmpty(managedUserModelApi.getLastName()).trim().length() < 3) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "First name and last name cannot be less than 3 characters ");
            }
            if (Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() < 11 || Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() > 20) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Phone number must be between 11-20 characters");
            }

            Configuration configuration = configurationRepository.findTopByAction(Action.UPDATE_USER);

            ManagedUser managedUser = createManagedUserFromManagedUserApiModel(managedUserModelApi);

            if (configuration != null && configuration.isMakerCheckerEnabled()) {
                return saveAdminUpdateRequestForVerification(managedUser);
            }


            user.setPhoneNumber(managedUser.getPhoneNumber());
            user.setLastName(managedUser.getLastName());
            user.setFirstName(managedUser.getFirstName());
            user.setDisplayName(managedUser.getEmail());
            user.setUserCategory(UserCategory.ADMIN);
            managedUserRepository.save(user);
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("Updated admin user %s ", managedUserModelApi.getEmail()));
            return new ManagedUserModelApi(managedUser, null, ResponseStatus.SUCCESSFUL, "Admin user updated successfully");
        } catch (Exception e) {
            logger.error("Error occurred while updating user due to ", e);
            return new ManagedUserModelApi( ResponseStatus.SYSTEM_ERROR, "Error occurred while updating admin user");
        }
    }

    public ManagedUserModelApi updateCustomerUser(ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest) {

        try {
            if (managedUserModelApi == null || Strings.isNullOrEmpty(managedUserModelApi.getEmail())) {
                return new ManagedUserModelApi( ResponseStatus.INVALID_REQUEST, "Email cannot be null");
            }

            ManagedUser user = managedUserRepository.findManagedUserByEmailOrDisplayName(managedUserModelApi.getEmail(), managedUserModelApi.getDisplayName());
            if (user == null) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email or Display Name not valid");
            }

            if (Strings.nullToEmpty(managedUserModelApi.getFirstName()).trim().length() < 3 || Strings.nullToEmpty(managedUserModelApi.getLastName()).trim().length() < 3) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "First name and last name cannot be less than 3 characters ");
            }

            if (Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() < 11 || Strings.nullToEmpty(managedUserModelApi.getPhoneNumber()).length() > 20) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Phone number must be between 11-20 characters");
            }

            Configuration configuration = configurationRepository.findTopByAction(Action.UPDATE_USER);

            ManagedUser managedUser = createManagedUserFromManagedUserApiModel(managedUserModelApi);

            if (configuration != null && configuration.isMakerCheckerEnabled()) {
                return saveAdminUpdateRequestForVerification(managedUser);
            }


            user.setPhoneNumber(managedUserModelApi.getPhoneNumber());
            user.setLastName(managedUserModelApi.getLastName());
            user.setFirstName(managedUserModelApi.getFirstName());
            user.setDisplayName(managedUserModelApi.getDisplayName());
            user.setUserCategory(UserCategory.CUSTOMER);
            managedUserRepository.save(user);
            managedUserModelApi.setId(user.getId());
            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("Updated admin user %s ", managedUserModelApi.getEmail()));
            return new ManagedUserModelApi(managedUser, null, ResponseStatus.SUCCESSFUL, "Admin user updated successfully");
        } catch (Exception e) {
            logger.error("Error occurred while updating user due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "Error occurred while updating admin user");
        }
    }

    public ManagedUserModelApi logout(HttpServletRequest servletRequest) {

        try {
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String email = userDetailsTokenEnvelope.getManagedUser().getEmail();
            Collection<OAuth2AccessToken> oAuth2AccessTokens = ((JdbcTokenStore) jdbcTokenStore).findTokensByUserName(email);

            for (OAuth2AccessToken oAuth2AccessToken : oAuth2AccessTokens) {
                ((JdbcTokenStore) jdbcTokenStore).removeAccessToken(oAuth2AccessToken);
            }

            auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("User %s logged out", email));
            return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Successfully logged out user");
        } catch (Exception e) {
            logger.error("Error occured while logging out user due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occured");

        }

    }

    @Transactional
    public ManagedUserModelApi updateOrUploadImage(ManagedUser managedUser) {

        try {
            if (managedUser == null || StringUtils.isEmpty(managedUser.getBase64Image())) {
                return new ManagedUserModelApi(null, null, ResponseStatus.INVALID_REQUEST, "Image cannot be blank");
            }

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) {
                return new ManagedUserModelApi(null, null, ResponseStatus.FAILED, "Login user not found. Token might have expired");
            }

            ManagedUser user = userDetailsTokenEnvelope.getManagedUser();
            user.setBase64Image(managedUser.getBase64Image());
            managedUserRepository.save(user);

            return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Successfully update user's image");
        } catch (Exception e) {
            logger.error("Error occurred while uploading user's profile picture due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occured while uploadating picture");
        }

    }

    private ManagedUserModelApi saveAdminCreationRequestForVerification(ManagedUserModelApi managedUserModelApi) {

        try {
            if (managedUserModelApi == null) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Unable to create request. Request object cannot be null");
            }

            String newData = objectMapper.writeValueAsString(managedUserModelApi);

            String message = requestService.logRequest(null, newData, Action.CREATE_USER, managedUserModelApi.getIniatorComment());

            if (!StringUtils.isEmpty(message)) {
                return new ManagedUserModelApi(ResponseStatus.FAILED, message);
            }

            return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Admin user created for verification");
        } catch (Exception e) {

            logger.error("Error occurred while logging admin user creation request due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred while saving admin creation request");

        }

    }

    private ManagedUserModelApi saveAdminUpdateRequestForVerification(ManagedUser managedUser) {

        try {
            if (managedUser == null) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Unable to create request. Request object cannot be null");
            }

            String newData = objectMapper.writeValueAsString(managedUser);

            String message = requestService.logRequest(null, newData, Action.UPDATE_USER, managedUser.getIniatorComment());

            if (!StringUtils.isEmpty(message)) {
                return new ManagedUserModelApi(ResponseStatus.FAILED, message);
            }

            return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Admin user update sent for verification");
        } catch (Exception e) {

            logger.error("Error occurred while logging admin user update request due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred while saving admin update request");

        }

    }

    public ManagedUserModelApi fetchCustomerUserDetails(Integer pageNo, Integer pageSize) {

        try {
            pageNo = pageNo == null ? 0 : pageNo;
            pageSize = pageSize == null || pageSize < 1 ? 15 : pageSize;

            Page<ManagedUser> managedUserPage = managedUserRepository.findAllByUserCategory(UserCategory.CUSTOMER, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setManagedUserPage(managedUserPage);
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("System error occurred while fetching customers records due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred");
        }

    }

    public ManagedUserModelApi fetchAdminUserDetails(Integer pageNo, Integer pageSize) {

        try {
            pageNo = pageNo == null ? 0 : pageNo;
            pageSize = pageSize == null || pageSize < 1 ? 15 : pageSize;

            Page<ManagedUser> managedUserPage = managedUserRepository.findAllByUserCategory(UserCategory.ADMIN, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setManagedUserPage(managedUserPage);
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("System error occurred while fetching admin records due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred");
        }

    }

    public ManagedUserModelApi getAdminUserDetails(Pageable pageable) {

        try {
            Page<ManagedUser> managedUserPage = managedUserRepository.findAllByUserCategory(UserCategory.ADMIN, pageable);

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setManagedUserPage(managedUserPage);
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("System error occurred while fetching admin records due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred");
        }

    }

    public RequestResponse verifyUserCreationRequest(Request request, HttpServletRequest servletRequest) throws IOException {

        ManagedUser managedUser = objectMapper.readValue(request.getNewData(), ManagedUser.class);
        ManagedUser user = managedUserRepository.findManagedUserByEmailOrDisplayName(managedUser.getEmail());
        if (user != null) {
            return RequestResponse.fromCodeAndNarration( ApiResponseCode.ALREADY_EXIST, "Email already in use");
        }

        if (Strings.nullToEmpty(managedUser.getFirstName()).trim().length() < 3 || Strings.nullToEmpty(managedUser.getLastName()).trim().length() < 3) {
            return  RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "First name and last name cannot be less than 3 characters ");
        }
        if (Strings.nullToEmpty(managedUser.getPhoneNumber()).length() < 11 || Strings.nullToEmpty(managedUser.getPhoneNumber()).length() > 20) {
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.ALREADY_EXIST, "Phone number must be between 11-20 characters");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null ) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Logged in user not found");


        String generatedString = RandomStringUtils.randomAlphanumeric(10);
        String encodedPassword = passwordEncoder.encode(generatedString);
        managedUser.setDateCreated(new java.util.Date());
        managedUser.setActive(true);
        managedUser.setDisplayName(managedUser.getEmail());
        managedUser.setUserCategory(UserCategory.ADMIN);
        managedUser.setPassword(encodedPassword);
        managedUser.setActivationCode(generateActivationCode(managedUser.getEmail()));
        managedUserRepository.save(managedUser);
        userEmailService.sendNewAdminUserEmail(managedUser.getEmail(), generatedString, managedUser.getFirstName(), managedUser.getActivationCode());
        auditLogService.saveAudit(null, null, Module.USER_MANAGEMENT, servletRequest, String.format("New admin user %s verified", managedUser.getEmail()));

        request.setStatus(Status.VERIFIED);

        request.setVerifierId(userDetailsTokenEnvelope.getManagedUser());
        requestRepository.save(request);

        return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User verified successfully");

    }
    public ManagedUserModelApi getCustomerUserDetails(Pageable pageable) {

        try {
            Page<ManagedUser> managedUserPage = managedUserRepository.findAllByUserCategory(UserCategory.CUSTOMER, pageable);

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setManagedUserPage(managedUserPage);
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("System error occurred while fetching admin records due to ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred");
        }

    }

    public ManagedUser getCurrentLoggedInUser() {
        try {
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetailsTokenEnvelope.getManagedUser();
        } catch (Exception e) {
            logger.error("Errro occurred while trying to retrieve current user details");
            return null;
        }

    }

    public RequestResponse verifyUserDeactivationRequest(Request request, HttpServletRequest servletRequest) {

        try {
            if (request == null || request.getEntityId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            ManagedUser user = getCurrentLoggedInUser();
            if (user == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Logged in user account not found");

            ManagedUser managedUser  = managedUserRepository.findOne(request.getEntityId());

            if (managedUser == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "User not found");

            managedUser.setActive(false);
            managedUserRepository.save(managedUser);

            request.setVerifiedOrDeclinedDate(new Date());
            request.setVerifierId(user);
            request.setStatus(Status.VERIFIED);
            requestRepository.save(request);

            auditLogService.saveAudit("","",Module.USER_MANAGEMENT, servletRequest, "User deactivated", request.getEntityId());
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User deactivation successfully verified");
        } catch (Exception e) {
            logger.error("Error occurred while verifying deactivation request due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying deactivation");
        }

    }
    public RequestResponse verifyUserActivationRequest(Request request, HttpServletRequest servletRequest) {

        try {
            if (request == null || request.getEntityId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            ManagedUser user = getCurrentLoggedInUser();
            if (user == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Logged in user account not found");
            ManagedUser managedUser  = managedUserRepository.findOne(request.getEntityId());

            if (managedUser == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "User not found");


            managedUser.setActive(true);
            managedUserRepository.save(managedUser);

            request.setVerifiedOrDeclinedDate(new Date());
            request.setVerifierId(user);
            request.setStatus(Status.VERIFIED);
            requestRepository.save(request);
            auditLogService.saveAudit("","",Module.USER_MANAGEMENT, servletRequest, "User activated", request.getEntityId());
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User activation successfully verified");
        } catch (Exception e) {
            logger.error("Error occurred while verifying activation request due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying activation");
        }

    }

    public UserDashboardDetails getDashboardDetailsForSignUps() {

        try {


            Date currentDate = new Date();
            UserDashboardDetails userDashboardDetails = new UserDashboardDetails();
            userDashboardDetails.setDailySignupsCount(NumberFormat.getInstance().format(managedUserRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentDay(), currentDate)));
            userDashboardDetails.setWeeklySignupsCount(NumberFormat.getInstance().format(managedUserRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentWeek(), currentDate)));
            userDashboardDetails.setMonthlySignupsCount(NumberFormat.getInstance().format(managedUserRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentMonth(), currentDate)));
            userDashboardDetails.setYearlySignupsCount(NumberFormat.getInstance().format(managedUserRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentYear(), currentDate)));
            userDashboardDetails.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            return userDashboardDetails;
        } catch (Exception e) {
            logger.error("System error occurred while fetching user management dashboard details due to ", e);
            return UserDashboardDetails.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occcurred while fetching user management dashboard details");
        }
    }


    private String generateActivationCode(String email) {
        return passwordEncoder.encode(email.substring(0,15));
    }

    public String activateUserAccountWithEmail(String email, String activationCode) {

            ManagedUser managedUser = managedUserRepository.findTopByEmailAndActivationCode(email, activationCode);

            if (managedUser == null)
                throw new PaaroAuthenticationException("Activation link invalid");
            if (managedUser.isAccountActivatedViaLink())
                throw new PaaroAuthenticationException("Account already activated");
            managedUser.setAccountActivatedViaLink(true);
            managedUserRepository.save(managedUser);
            return "Account successfully activated";

    }

}
