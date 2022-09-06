package com.lets.security.oauth2.user;


import java.util.Map;

public class GithubOAuth2UserInfo extends OAuth2UserInfo {
    public GithubOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
        super(accessToken, attributes);
    }

    @Override
    public String getSocialLoginId() {
        String id = String.valueOf(attributes.get("id"));
        return id;
    }

//    @Override
//    public String getEmail(){
//
//        String baseUrl = "https://api.github.com/user/emails";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "token " + accessToken);
//
//        HttpEntity<String> entity = new HttpEntity<String>("", headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> respEntity = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);
//
//        String body = respEntity.getBody();
//        JSONObject jObject = new JSONObject(body.substring(1, body.length()-1));
//
//        String email = jObject.getString("email");
//
//        return email;
//
//    }
}
