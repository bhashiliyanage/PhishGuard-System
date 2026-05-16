package edu.nsbm.phishguard.controller;

import edu.nsbm.phishguard.annotation.CurrentUserId;
import edu.nsbm.phishguard.enums.Type;
import edu.nsbm.phishguard.util.client.LlmClient;
import edu.nsbm.phishguard.util.response.LlmResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Deprecated
@RequiredArgsConstructor
@RequestMapping("api/v1/test")
public class TestServiceController {

    private final LlmClient client;

    @GetMapping("/mail/fake{id}")
    public LlmResponse test(@PathVariable String id){
        if (id.equals("1")) return client.generateMail(Type.NORMAL);
        return client.generateMail(Type.PHISHING);
    }

    @GetMapping("/current/user")
    public String getCurrentUser(@Parameter(hidden = true) @CurrentUserId String userId){
        return userId;
    }
}
