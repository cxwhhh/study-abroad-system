package com.study.service.auth.wechat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/wechat")
public class WeChatAuthController {

    @Autowired
    private WeChatAuthService service;

    @PostMapping(value = "/prepare", produces = MediaType.APPLICATION_JSON_VALUE)
    public WeChatAuthService.PrepareResponse prepare() {
        return service.prepare();
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code, @RequestParam("state") String state) {
        service.handleCallback(code, state);
        return "<script>try{window.opener&&window.opener.postMessage({type:'wechat-login',state:'"+state+"'}, '*');}catch(e){}window.close();</script>";
    }

    @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public WeChatAuthService.StatusResponse status(@RequestParam("state") String state) {
        return service.status(state);
    }
}