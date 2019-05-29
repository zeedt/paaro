package com.plethub.paaro.core.request.controller;

import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.apimodel.RequestSearchModel;
import com.plethub.paaro.core.request.service.RequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    private Logger logger = LoggerFactory.getLogger(RequestController.class.getName());

    @GetMapping("/get-paged-pending-request-by-action")
    public RequestResponse getPagedPendingRequestByAction(@RequestParam("action")Action action, @RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {

        return requestService.getPagedPendingRequestByAction(action, pageNo, pageNo);
    }


    @PostMapping("/search-by-or-params")
    public RequestResponse getPagedRequestByOrParameters(@RequestBody RequestSearchModel requestSearchModel) {

        return requestService.getPagedRequestByOrParameters(requestSearchModel);
    }


    @PostMapping("/search-by-and-params")
    public RequestResponse getPagedRequestByAndParameters(@RequestBody RequestSearchModel requestSearchModel) {

        return requestService.getPagedRequestByAndParameters(requestSearchModel);
    }

    @PreAuthorize("hasAuthority('VERIFY_REQUEST')")
    @RequestMapping(value = "/verify-request-by-id", method = RequestMethod.GET)
    public RequestResponse verfiyRequesById(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return requestService.verifyRequestById(id, servletRequest);
        } catch (Exception e) {
            logger.error("Errro occurred while verifying request due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System erro occurred while verifying request");
        }

    }


}
