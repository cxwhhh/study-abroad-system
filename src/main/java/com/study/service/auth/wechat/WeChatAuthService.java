package com.study.service.auth.wechat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeChatAuthService {

    @Value("${wechat.appId:}")
    private String appId;

    @Value("${wechat.appSecret:}")
    private String appSecret;

    @Value("${wechat.redirectUri:https://example.com/auth/wechat/callback}")
    private String redirectUri;

    private final ConcurrentHashMap<String, StateRecord> stateStore = new ConcurrentHashMap<>();

    public PrepareResponse prepare() {
        String state = UUID.randomUUID().toString().replace("-", "");
        stateStore.put(state, new StateRecord(StateRecord.Status.PENDING, null));
        PrepareResponse res = new PrepareResponse();
        res.appid = appId;
        res.state = state;
        res.redirectUri = redirectUri;
        return res;
    }

    public void handleCallback(String code, String state) {
        StateRecord rec = stateStore.get(state);
        if (rec == null)
            return;
        try {
            RestTemplate rt = new RestTemplate();
            String tokenUrl = String.format(
                    "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                    appId, appSecret, code);
            ResponseEntity<Map<String, Object>> tokenResp = rt.exchange(
                    tokenUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    });
            Map<String, Object> body = tokenResp.getBody();
            String openid = body == null ? null : (String) body.get("openid");
            String unionid = body == null ? null : (String) body.get("unionid");
            String token = "wechat-" + (unionid != null ? unionid : openid);
            stateStore.put(state, new StateRecord(StateRecord.Status.SUCCESS, token));
        } catch (Exception e) {
            stateStore.put(state, new StateRecord(StateRecord.Status.FAILED, null));
        }
    }

    public StatusResponse status(String state) {
        StateRecord rec = stateStore.get(state);
        StatusResponse s = new StatusResponse();
        if (rec == null) {
            s.status = "invalid";
            return s;
        }
        if (rec.status == StateRecord.Status.SUCCESS) {
            s.status = "success";
            s.token = rec.token;
        } else if (rec.status == StateRecord.Status.FAILED) {
            s.status = "failed";
        } else {
            s.status = "pending";
        }
        return s;
    }

    public static class PrepareResponse {
        public String appid;
        public String state;
        public String redirectUri;
    }

    public static class StatusResponse {
        public String status;
        public String token;
    }

    private static class StateRecord {
        enum Status {
            PENDING, SUCCESS, FAILED
        }

        Status status;
        String token;

        StateRecord(Status status, String token) {
            this.status = status;
            this.token = token;
        }
    }
}
