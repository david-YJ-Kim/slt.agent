package com.tsh.slt.activator;

import com.solacesystems.jcsmp.JCSMPException;
import com.tsh.slt.config.ApPropertyObject;
import com.tsh.slt.config.SolaceSessionConfiguration;
import com.tsh.slt.data.ApSharedVariable;
import com.tsh.slt.interfaces.solace.InterfaceSolacePub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class ApStartedActivator implements ApplicationRunner {

    @Autowired
    private Environment env;


//    @Autowired
//    BatchInvokeScheduler batchInvokeScheduler;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        this.initializeSharedVariables();

        this.initializeApService();

//        this.initializeSolaceResources();

        log.info("Complete Initialization.");
    }


    /**
     * AP Common 영역 초기화
     */
    private void initializeSharedVariables(){

        // Shared Variable 초기화
        ApSharedVariable.createInstance(env);
    }

    /**
     * AP 서비스 영역 초기화
     */
    private void initializeApService(){

//        ScheduledExecutorService mainThread =  ApSharedVariable.getInstance().getApExecutorService();
//        batchInvokeScheduler.startSchedule(mainThread, TimeUnit.SECONDS, 30L);
    }

    private void initializeSolaceResources(){

        SolaceSessionConfiguration sessionConfiguration = SolaceSessionConfiguration.createSessionConfiguration(env);

        try {
            InterfaceSolacePub interfaceSolacePub = InterfaceSolacePub.getInstance();
            interfaceSolacePub.init();
            ApPropertyObject.getInstance().setInterfaceSolacePub(interfaceSolacePub);

        } catch (JCSMPException e) {
            throw new RuntimeException(e);
        }

//        try {
//            InterfaceSolaceSub interfaceSolaceSub = new InterfaceSolaceSub();
//            interfaceSolaceSub.run();
//            ApPropertyObject.getInstance().setInterfaceSolaceSub(interfaceSolaceSub);
//
//        } catch (JCSMPException e) {
//            throw new RuntimeException(e);
//        }

    }
}
