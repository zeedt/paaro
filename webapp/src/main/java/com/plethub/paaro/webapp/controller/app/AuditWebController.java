package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.authority.services.AuthorityService;
import com.plethub.paaro.core.models.AuditLog;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/app/audit")
public class AuditWebController {

    @Autowired
    AuditLogService auditLogService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(HttpServletRequest httpServletRequest, Model model) {
        //auditLogService.
    }

    @GetMapping
    public String all(Model model){
        return "audit/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<AuditLog> getUsers(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<AuditLog> sq = auditLogService.getAuditLogs(pageable);
        DataTablesOutput<AuditLog> out = new DataTablesOutput<AuditLog>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/detail")
    public String auditDetail(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            model.addAttribute("auditLog", auditLogService.getById(id));
            return "audit/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/audit";
        }
    }
}
