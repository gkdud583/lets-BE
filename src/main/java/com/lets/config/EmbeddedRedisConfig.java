//package com.lets.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.util.StringUtils;
//import redis.embedded.RedisServer;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//
//@Slf4j
//@Profile("local") // profile이 local일때만 활성화
//@Configuration
//public class EmbeddedRedisConfig {
//    @Value("${spring.redis.port}")
//    private int redisPort;
//
//
//    private RedisServer redisServer;
//
//    @PostConstruct
//    public void redisServer() throws IOException {
//        redisPort = isRedisRunning() ? findAvailablePort() : redisPort;
//        redisServer = RedisServer.builder()
//                .port(redisPort)
//                .setting("maxmemory 128M")
//                .build();
//
//        redisServer.start();
//    }
//
//    /**
//     * Embedded Redis가 현재 실행중인지 확인
//     */
//    private boolean isRedisRunning() throws IOException{
//        return isRunning(executeGrepProcessCommand(redisPort));
//    }
//
//    /**
//     * 해당 Process가 현재 실행중인지 확인
//     */
//    private boolean isRunning(Process process) {
//        String line;
//        StringBuilder pidInfo = new StringBuilder();
//
//        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//
//            while ((line = input.readLine()) != null) {
//                pidInfo.append(line);
//            }
//
//        } catch (Exception e) {
//        }
//
//        return !StringUtils.isEmpty(pidInfo.toString());
//    }
//    /**
//     * 해당 Port를 사용중인 프로세스 확인하는 sh 실행
//     */
//    private Process executeGrepProcessCommand(int redisPort) throws IOException{
//        String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", redisPort);
//        String[] shell = {"cmd.exe", "/y", "/c", command};
//        return Runtime.getRuntime().exec(shell);
//    }
//
//    /**
//     * 현재 PC서버에서 사용가능한 포트 조회
//     */
//    private int findAvailablePort() throws IOException{
//        for(int port = 10000; port <= 65535; port++){
//            Process process = executeGrepProcessCommand(port);
//            if(!isRunning(process)){
//                return port;
//            }
//        }
//        throw new IllegalArgumentException("Not Found Available port: 10000 ~ 65535");
//    }
//
//
//
//
//
//    @PreDestroy
//    public void stopRedis(){
//
//        if(redisServer != null){
//            redisServer.stop();
//        }
//    }
//}
