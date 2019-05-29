package com.plethub.paaro.core.authority.controller;

import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.models.Authority;
import com.plethub.paaro.core.usermanagement.repository.AuthorityRepository;
import com.plethub.paaro.core.authority.services.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/app/authority")
public class AuthorityController {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @ResponseBody
    @RequestMapping(value = "/fetchAllAuthorities", method = RequestMethod.GET)
    public ManagedUserModelApi fetchAllAuthorities(HttpServletRequest servletRequest){
        return authorityService.getAllAuthorities(servletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/fetchAllAuthorities/list", method = RequestMethod.GET)
    public List<Authority> fetchAllAuthoritiesFromRepository(){
        return authorityRepository.findAllByAuthorityIsNotNull();
    }


    @ResponseBody
    @RequestMapping(value = "/mapAuthoritiesToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthoritiesToUser(@RequestBody ManagedUserModelApi userModelApi, HttpServletRequest servletRequest){
        return authorityService.mapAuthoritiesToUser(userModelApi.getEmail(),userModelApi.getAuthorities(), servletRequest);
    }


    @ResponseBody
    @RequestMapping(value = "/mapAuthorityToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthorityToUser(@RequestBody ManagedUserModelApi userModelApi, HttpServletRequest servletRequest){
        return authorityService.mapAuthorityToUser(userModelApi.getEmail(), userModelApi.getAuthority(), servletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/addAuthority", method = RequestMethod.POST)
    public ManagedUserModelApi addAuthorityToUser(@RequestBody Authority authority, HttpServletRequest servletRequest){
        return authorityService.addAuthority(authority, servletRequest);
    }

}
