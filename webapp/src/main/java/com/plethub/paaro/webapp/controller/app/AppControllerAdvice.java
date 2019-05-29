//package com.plethub.paaro.webapp.controller.app;
//
//import com.plethub.paaro.webapp.ApiModel.UserDetails;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//@ControllerAdvice(basePackages = {"com.plethub.paaro.webapp.controller.app"})
//public class AppControllerAdvice {
//
//    //TODO
//    //@ExceptionHandler
//
//
//    @ModelAttribute
//    public String globalAttributes(Model model, RedirectAttributes redirectAttrs, HttpServletRequest hsr, HttpServletResponse response){
//
//        //System.out.println("Controller Advice");
////        UserDetails principal = getCurrentUser(hsr);
////        if(null == principal) {
////            System.out.println("redirect");
////            //response.sendRedirect();
////            return "redirect:/app/logout";
////        }
//
//        return "";
//    }
//
//    private UserDetails getCurrentUser(HttpServletRequest hsr){
//        UserDetails userDetails = (UserDetails) hsr.getSession().getAttribute("userDetails");
//        if (null != userDetails) {
//            return userDetails;
//        }
//        return (null) ;
//    }
//}
