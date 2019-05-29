package com.plethub.paaro.core.authority.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.appservice.repository.ConfigurationRepository;
import com.plethub.paaro.core.appservice.repository.RequestRepository;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import com.plethub.paaro.core.models.*;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.service.RequestService;
import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.enums.ResponseStatus;
import com.plethub.paaro.core.usermanagement.repository.AuthorityRepository;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserAuthorityRepository;
import com.plethub.paaro.core.usermanagement.repository.ManagedUserRepository;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class AuthorityService {


    @Autowired
    public ManagedUserRepository managedUserRepository;

    @Autowired
    public AuthorityRepository authorityRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    public ManagedUserAuthorityRepository managedUserAuthorityRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private ConfigurationService configurationService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    Logger logger = LoggerFactory.getLogger(AuthorityService.class.getName());

    @Transactional
    public ManagedUserModelApi mapAuthoritiesToUser(String email, List<String> authorities, HttpServletRequest servletRequest) {


        try {

            if (CollectionUtils.isEmpty(authorities)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "No authority passed");
            }

            if (StringUtils.isEmpty(email)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email cannot be empty");
            }

            Set<String> setAuthorities = authorities.stream().filter(authority->!StringUtils.isEmpty(authority)).collect(Collectors.toSet());

            if (CollectionUtils.isEmpty(setAuthorities)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "No authority passed");
            }

            List<Authority> savedAuthorities = authorityRepository.findAuthoritiesByAuthorityIn(setAuthorities);

            if (savedAuthorities == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "None of the authorities found");
            }

            ManagedUser managedUser = managedUserRepository.findOneByEmail(email);

            if (managedUser == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "User not found");
            }

            if (configurationService.isMakerCheckerEnabledForAction(Action.MAP_AUTHORITIES_TO_USER))  {
                return saveMapAuthoritiesRequestForVerification(managedUser, savedAuthorities, servletRequest );
            }

            return auditAndMapAuthoritiesToUser(savedAuthorities, managedUser, servletRequest, authorities.size());

        } catch (Exception e) {
            logger.error("Error occurred while mapping authority to user all authorities ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while mapping authorities to user");
        }
    }

    @Transactional
    public ManagedUserModelApi saveMapAuthoritiesRequestForVerification(ManagedUser managedUser, List<Authority> savedAuthorities, HttpServletRequest servletRequest) {

        try {
            if (managedUser == null || CollectionUtils.isEmpty(savedAuthorities)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Unable to create request. Request object cannot be null");
            }

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setManagedUser(managedUser);
            managedUserModelApi.setAuthorityList(savedAuthorities);

            String newData = objectMapper.writeValueAsString(managedUserModelApi);

            String message = requestService.logRequest(null,newData, Action.MAP_AUTHORITIES_TO_USER, managedUser.getIniatorComment());

            if (!StringUtils.isEmpty(message)) {
                return new ManagedUserModelApi(ResponseStatus.FAILED, message);
            }

            auditLogService.saveAudit(null,objectMapper.writeValueAsString(savedAuthorities), Module.AUTHORITY, servletRequest, String.format("User mapped authorities to user %s for verification",managedUser.getEmail()));

            return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Successfully created authorities request for user");
        } catch (Exception e) {

            logger.error("Error occurred while adding authorities to user ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred while adding authorities to user");

        }


    }

    private ManagedUserModelApi saveMapAuthorityRequestForVerification(ManagedUser managedUser, Authority authority, HttpServletRequest servletRequest) {

        try {
            if (managedUser == null || authority == null) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Unable to create request. Request object cannot be null");
            }

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setManagedUser(managedUser);
            managedUserModelApi.setAuthorityModel(authority);

            String newData = objectMapper.writeValueAsString(managedUserModelApi);

            String message = requestService.logRequest(null,newData, Action.MAP_AUTHORITY_TO_USER, managedUser.getIniatorComment());

            if (!StringUtils.isEmpty(message)) {
                return new ManagedUserModelApi(ResponseStatus.FAILED, message);
            }
            auditLogService.saveAudit(null,objectMapper.writeValueAsString(authority), Module.AUTHORITY, servletRequest, String.format("User mapped authority to user %s for verification",managedUser.getEmail()));

            return new ManagedUserModelApi(ResponseStatus.SUCCESSFUL, "Successfully created authority request for user");
        } catch (Exception e) {

            logger.error("Error occurred while adding authorities to user ", e);
            return new ManagedUserModelApi(ResponseStatus.SYSTEM_ERROR, "System error occurred while adding authority to user");

        }


    }


    public ManagedUserModelApi getAllAuthorities(HttpServletRequest servletRequest) {

        try {
            List<Authority> authorities = (List<Authority>) authorityRepository.findAll();

            if (authorities == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NULL_RESPONSE, "No authority found in system");
            }

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            managedUserModelApi.setAuthorityList(authorities);
            managedUserModelApi.setMessage("Authorities fetched successfully");
            auditLogService.saveAudit(null,null, Module.AUTHORITY, servletRequest, "User fetched all authorities");

            return managedUserModelApi;

        } catch (Exception e) {
            logger.error("Error occurred while fetching all authorities ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while fetching authorities");
        }
    }

    public ManagedUserModelApi mapAuthorityToUser(String email, String authority, HttpServletRequest servletRequest) {

        try {

            if (StringUtils.isEmpty(email) || StringUtils.isEmpty(authority)) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Email and authority cannot be blank");
            }

            Authority savedAuthority = authorityRepository.findAuthorityByAuthority(authority);

            if (savedAuthority == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "Authority not found");
            }

            ManagedUser managedUser = managedUserRepository.findOneByEmail(email);

            if (managedUser == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NOT_FOUND, "User not found");
            }

            List<ManagedUserAuthority> existingManagedUserAuthority = managedUserAuthorityRepository.findAllByManagedUserIdAndAuthorityId(managedUser.getId(), savedAuthority.getId());

            if (!CollectionUtils.isEmpty(existingManagedUserAuthority)) {
                return new ManagedUserModelApi(ResponseStatus.ALREADY_EXIST, "Authority has already been mapped to uer");
            }

            if (configurationService.isMakerCheckerEnabledForAction(Action.MAP_AUTHORITY_TO_USER))  {
                return saveMapAuthorityRequestForVerification(managedUser, savedAuthority, servletRequest );
            }

            return auditAndMapAuthorityToUser(savedAuthority,managedUser,servletRequest);

        } catch (Exception e) {
            logger.error("Error occurred while mapping authority to user all authorities ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while mapping authority to user");
        }
    }

    public ManagedUserModelApi addAuthority(Authority authority, HttpServletRequest servletRequest) {

        try {
            if (authority == null || StringUtils.isEmpty(authority.getAuthority()) || StringUtils.isEmpty(authority.getDescription()) ) {
                return new ManagedUserModelApi(ResponseStatus.INVALID_REQUEST, "Description and authority name cannot be blank");
            }

            Authority existingAuthority = authorityRepository.findAuthorityByAuthority(authority.getAuthority());

            if (existingAuthority != null) {
                return new ManagedUserModelApi(ResponseStatus.ALREADY_EXIST, "Authority already exist");
            }

            Authority newAuthority = new Authority();
            newAuthority.setAuthority(authority.getAuthority());
            newAuthority.setDescription(authority.getDescription());

            authorityRepository.save(newAuthority);

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setMessage("Successfully added authority");
            managedUserModelApi.setAuthorityModel(newAuthority);
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            auditLogService.saveAudit(null,newAuthority.getAuthority(), Module.AUTHORITY, servletRequest, "User added new authority");

            return managedUserModelApi;
        } catch (Exception e) {
            logger.error("Error occurred while adding authority ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while adding authority");
        }

    }

    private ManagedUserModelApi auditAndMapAuthorityToUser (Authority savedAuthority, ManagedUser managedUser, HttpServletRequest servletRequest) {

        ManagedUserAuthority managedUserAuthority = new ManagedUserAuthority();
        managedUserAuthority.setAuthorityId(savedAuthority.getId());
        managedUserAuthority.setManagedUserId(managedUser.getId());

        managedUserAuthorityRepository.save(managedUserAuthority);

        ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
        managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
        managedUserModelApi.setAuthorityModel(savedAuthority);
        managedUserModelApi.setMessage("Authority mapped to user successfully");
        auditLogService.saveAudit(null,savedAuthority.getAuthority(), Module.AUTHORITY, servletRequest, String.format("User mapped authority to user %s", managedUser.getEmail()));
        return managedUserModelApi;
    }


    private ManagedUserModelApi auditAndMapAuthoritiesToUser (List<Authority> savedAuthorities, ManagedUser managedUser, HttpServletRequest servletRequest, Integer totalNoOfAuthorities) {
        final String[] concatenatedMappedAuthority = {""};

        List<Authority> successfullyPersisted = new ArrayList<>();
        savedAuthorities.stream().parallel().forEach(authority -> {
            try {

                List<ManagedUserAuthority> existingManagedUserAuthority = managedUserAuthorityRepository.findAllByManagedUserIdAndAuthorityId(managedUser.getId(), authority.getId());
                if (CollectionUtils.isEmpty(existingManagedUserAuthority)) {
                    ManagedUserAuthority managedUserAuthority = new ManagedUserAuthority();
                    managedUserAuthority.setAuthorityId(authority.getId());
                    managedUserAuthority.setManagedUserId(managedUser.getId());
                    managedUserAuthorityRepository.save(managedUserAuthority);
                    successfullyPersisted.add(authority);
                    concatenatedMappedAuthority[0] = concatenatedMappedAuthority[0].concat(authority.getAuthority()) + "," + authority.getAuthority();
                }
            } catch (Exception e) {
                logger.error("Error occurred while persisting managed user authority due to ", e);
            }
        });

        ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
        managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
        managedUserModelApi.setAuthorityList(successfullyPersisted);
        managedUserModelApi.setMessage(String.format("%d out of %d authorities mapped to user successfully", successfullyPersisted.size(), totalNoOfAuthorities));
        auditLogService.saveAudit(null,concatenatedMappedAuthority[0], Module.AUTHORITY, servletRequest, String.format("User mapped authorities to user %s",totalNoOfAuthorities));
        return managedUserModelApi;
    }

    public  ManagedUserModelApi fetchUnMappedAuthoritiesForUser(Long userId) {

        if (managedUserRepository.findOne(userId) == null) return new ManagedUserModelApi(ResponseStatus.NOT_FOUND, "User not found");

        List<Authority> unMappedUserAuthority;
        List<Long> existingAuthoritiesId = new ArrayList<>();

        List<ManagedUserAuthority> userExistingAuthorities = managedUserAuthorityRepository.findAllByManagedUserId(userId);

        if (!CollectionUtils.isEmpty(userExistingAuthorities)) {
            userExistingAuthorities.forEach(authority-> {
                if (authority != null && authority.getAuthorityId() != null) {
                    existingAuthoritiesId.add(authority.getAuthorityId());
                }
            });
        }

        unMappedUserAuthority = (CollectionUtils.isEmpty(existingAuthoritiesId)) ? authorityRepository.findAllByAuthorityIsNotNull()
                : authorityRepository.findAllByIdNotIn(existingAuthoritiesId);

        ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
        managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
        managedUserModelApi.setAuthorityList(unMappedUserAuthority);

        return managedUserModelApi;

    }


    public ManagedUserModelApi getAuthorityId(Long id) {

        try {

            if (id == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.INVALID_REQUEST, "Id cannot be null");
            }

            Authority authority = authorityRepository.findOne(id);

            if (authority == null) {
                return new ManagedUserModelApi(null,null,ResponseStatus.NULL_RESPONSE, "Authority not found in system");
            }

            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            managedUserModelApi.setResponseStatus(ResponseStatus.SUCCESSFUL);
            managedUserModelApi.setAuthorityModel(authority);
            managedUserModelApi.setMessage("Authority fetched successfully");

            return managedUserModelApi;

        } catch (Exception e) {
            logger.error("Error occurred while fetching all authority ", e);
            return new ManagedUserModelApi(null, null,ResponseStatus.SYSTEM_ERROR, "Error occurred while fetching authority");
        }
    }


    public RequestResponse verifyAuthorityMappingToUser(Request request, HttpServletRequest servletRequest) {

        try {

            ManagedUserModelApi userModelApi = objectMapper.readValue(request.getNewData(), ManagedUserModelApi.class);

            Authority authority = userModelApi.getAuthorityModel() ;

            if (authority == null || authority.getId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            ManagedUser user = userService.getCurrentLoggedInUser();

            if (user == null || user.getId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Logged in user not found");

            ManagedUserAuthority managedUserAuthority = new ManagedUserAuthority();
            managedUserAuthority.setAuthorityId(authority.getId());
            managedUserAuthority.setManagedUserId(user.getId());

            managedUserAuthorityRepository.save(managedUserAuthority);

            request.setVerifierId(user);
            request.setVerifiedOrDeclinedDate(new Date());
            request.setStatus(Status.VERIFIED);
            requestRepository.save(request);

            auditLogService.saveAudit(null,objectMapper.writeValueAsString(authority), Module.AUTHORITY, servletRequest, String.format("User verified authority mapping to user %s",user.getEmail()));
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Authority successfully mapped to user");

        } catch (Exception e) {
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying authority map request");
        }

    }

    public RequestResponse verifyAuthoritiesMappingToUser(Request request, HttpServletRequest servletRequest) {

        try {
            ManagedUserModelApi userModelApi = objectMapper.readValue(request.getNewData(), ManagedUserModelApi.class);

            List<Authority> authorities = userModelApi.getAuthorityList() ;

            if (CollectionUtils.isEmpty(authorities)) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request. No authority found");

            ManagedUser user = userService.getCurrentLoggedInUser();

            if (user == null || user.getId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Logged in user not found");

            final String[] concatenatedMappedAuthority = {""};

            List<Authority> successfullyPersisted = new ArrayList<>();
            authorities.stream().parallel().forEach(authority -> {
                try {

                    List<ManagedUserAuthority> existingManagedUserAuthority = managedUserAuthorityRepository.findAllByManagedUserIdAndAuthorityId(user.getId(), authority.getId());
                    if (CollectionUtils.isEmpty(existingManagedUserAuthority)) {
                        ManagedUserAuthority managedUserAuthority = new ManagedUserAuthority();
                        managedUserAuthority.setAuthorityId(authority.getId());
                        managedUserAuthority.setManagedUserId(user.getId());
                        managedUserAuthorityRepository.save(managedUserAuthority);
                        successfullyPersisted.add(authority);
                        concatenatedMappedAuthority[0] = concatenatedMappedAuthority[0].concat(authority.getAuthority()) + "," + authority.getAuthority();
                    }
                } catch (Exception e) {
                    logger.error("Error occurred while persisting managed user authority due to ", e);
                }
            });

            request.setVerifierId(user);
            request.setVerifiedOrDeclinedDate(new Date());
            request.setStatus(Status.VERIFIED);
            requestRepository.save(request);

            auditLogService.saveAudit(null,concatenatedMappedAuthority[0], Module.AUTHORITY, servletRequest, String.format("User verified authorities mapping to to user %s",user.getEmail()));
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Successfully verified authorities mapping");

        } catch (Exception e) {
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying authorities mapping request");
        }

    }
}
