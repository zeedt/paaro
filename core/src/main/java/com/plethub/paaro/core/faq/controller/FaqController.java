package com.plethub.paaro.core.faq.controller;

import com.plethub.paaro.core.faq.apimodel.FaqRequest;
import com.plethub.paaro.core.faq.apimodel.FaqResponse;
import com.plethub.paaro.core.faq.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/faq-management")
public class FaqController {

    @Autowired
    private FaqService faqService;

    @PreAuthorize(value = "hasAnyAuthority('ADD_FAQ')")
    @PostMapping("/add-faq")
    public FaqResponse addFaq(@RequestBody FaqRequest faqRequest, HttpServletRequest servletRequest) {

        return faqService.addFaq(faqRequest, servletRequest);

    }

    @PreAuthorize(value = "hasAnyAuthority('UPDATE_FAQ')")
    @PostMapping("/update-faq")
    public FaqResponse updateFaq(@RequestBody FaqRequest faqRequest, HttpServletRequest servletRequest) {

        return faqService.updateFaq(faqRequest, servletRequest);

    }

    @PreAuthorize(value = "hasAnyAuthority('DELETE_FAQ')")
    @GetMapping("/delete-faq")
    public FaqResponse deleteFaq(@RequestParam("faqId") Long faqId, HttpServletRequest servletRequest) {

        return faqService.deleteFaq(faqId, servletRequest);

    }

    //@PreAuthorize(value = "hasAnyAuthority('FETCH_FAQ')")
    @GetMapping("/all")
    public FaqResponse getFaqs(@RequestParam("pageSize") int pageSize, @RequestParam("pageNo") int pageNo) {

        return faqService.getFaqs(pageNo, pageSize);

    }

    @PreAuthorize(value = "hasAnyAuthority('FETCH_FAQ')")
    @GetMapping("/by-id")
    public FaqResponse getFaqById(@RequestParam("id") Long id) {

        return faqService.getFaq(id);

    }





}
