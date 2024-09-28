package com.tsh.slt.data;


import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * AP 공통 사용 데이터 구조체
 */
@Component
public class ApSharedVariable {

    Environment env;

    @Getter
    private static ApSharedVariable instance;


    @Getter
    ScheduledExecutorService apExecutorService;


    public ApSharedVariable(Environment env) {
        this.env = env;
        instance = this;
    }


    /**
     * Shared 사용할 객체들을 초기화 하는 단계
     * @param env
     * @return
     */
    public static ApSharedVariable createInstance(Environment env){
        if(instance == null){
            synchronized (ApSharedVariable.class){
                if(instance == null){
                    instance = new ApSharedVariable(env);
                }
            }
        }

        ApSharedVariable.initialize();

        return instance;
    }

    private static void initialize(){

        // Executor 초기화
        if(instance.apExecutorService == null){
            instance.apExecutorService = Executors.newScheduledThreadPool(1);
        }


    }


}
