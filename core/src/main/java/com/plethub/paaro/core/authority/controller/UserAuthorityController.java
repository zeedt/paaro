package com.plethub.paaro.core.authority.controller;

import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.models.Authority;
import com.plethub.paaro.core.authority.services.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/authority")
public class UserAuthorityController {

    @Autowired
    private AuthorityService authorityService;

    @ResponseBody
    @RequestMapping(value = "/fetchAllAuthorities", method = RequestMethod.GET)
    public ManagedUserModelApi fetchAllAuthorities(HttpServletRequest httpServletRequest){
        return authorityService.getAllAuthorities(httpServletRequest);
    }


    @ResponseBody
    @RequestMapping(value = "/fetchAuthorityById", method = RequestMethod.GET)
    public ManagedUserModelApi fetchAuthorityById(@RequestParam("id") Long id){
        return authorityService.getAuthorityId(id);
    }


    @ResponseBody
    @PreAuthorize("hasAuthority('MAP_AUTHORITIES_TO_USER')")
    @RequestMapping(value = "/mapAuthoritiesToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthoritiesToUser(@RequestBody ManagedUserModelApi userModelApi, HttpServletRequest servletRequest){
        return authorityService.mapAuthoritiesToUser(userModelApi.getEmail(), userModelApi.getAuthorities(), servletRequest);
    }


    @ResponseBody
    @PreAuthorize("hasAuthority('MAP_AUTHORITIES_TO_USER')")
    @RequestMapping(value = "/mapAuthorityToUser", method = RequestMethod.POST)
    public ManagedUserModelApi mapAuthorityToUser(@RequestBody ManagedUserModelApi userModelApi, HttpServletRequest servletRequest){
        return authorityService.mapAuthorityToUser(userModelApi.getEmail(), userModelApi.getAuthority(), servletRequest);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('ADD_AUTHORITY')")
    @RequestMapping(value = "/addAuthority", method = RequestMethod.POST)
    public ManagedUserModelApi addAuthority(@RequestBody Authority authority, HttpServletRequest httpServletRequest){
        return authorityService.addAuthority(authority, httpServletRequest);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('MAP_AUTHORITIES_TO_USER')")
    @RequestMapping(value = "/fetchUnMappedAuthoritiesForUser", method = RequestMethod.GET)
    public ManagedUserModelApi fetchUnMappedAuthoritiesForUser(@RequestParam("userId") Long userId){
        return authorityService.fetchUnMappedAuthoritiesForUser(userId);
    }



}
