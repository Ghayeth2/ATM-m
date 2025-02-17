package com.atm.controller;

import com.atm.business.abstracts.TransactionsServices;
import com.atm.model.dtos.payloads.requests.TransactionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/atm/transactions")

public class TransactionController {

    /*
    I don't why it happened, but this is how i solved this problem.
     */
    @Autowired
    @Qualifier("transactionsManager")
    private TransactionsServices transactionServices;

    @PostMapping
    public String newTransaction(@Valid @ModelAttribute("transaction")
                                 TransactionRequest request,
                                 BindingResult bindingResult,
                                 Model model, RedirectAttributes redirectAttributes) {
        // Adding Key, value pairs for types: withdraw, transfer, deposit redirections
        Map<String, String> redirections = new HashMap<>();
        redirections.put("Withdrawal", "withdraw");
        redirections.put("Deposit", "deposit");
        redirections.put("Transfer", "transfer");
        // Redirection URLs Success & Errors
        String successUrl = "/atm/";
        String errorsUrl = "layout/transactions/";
        log.info("TransactionController -> request is hit the endpoint");
        // Handle the form's errors & return to
        if (bindingResult.hasErrors()) {
            log.error("TransactionController -> bindingResult has errors");
            log.error("Errors: {}", bindingResult.getAllErrors());
            model.addAttribute(redirections.get(request.getType()), request);
            return errorsUrl + redirections.get(request.getType());
        }
        log.info("TransactionController -> newTransaction -> request valid -> "
        + request.getType());
        // Calling transaction services
        String[] numbers = {request.getSenderNumber()
                , request.getReceiverNumber()};
        String response = transactionServices.newTransaction(
                request.getType(), request.getAmount(),
                numbers
        );
        // Redirecting to transaction template with response
        redirectAttributes.addFlashAttribute("success", response);
        log.info("TransactionController -> redirecting to: "
        + successUrl + redirections.get(request.getType()));
        return "redirect:" + successUrl + redirections.get(request.getType());
    }
}
