package com.tsh.slt.config;


import com.tsh.slt.interfaces.solace.InterfaceSolacePub;
import com.tsh.slt.interfaces.solace.InterfaceSolaceSub;
import com.tsh.slt.util.ApCommonUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Property 파일을 객체화
 */
@Getter
@Component
public class ApPropertyObject {

    Environment env;
    @Value("${ap.info.group}")
    private String groupName;

    @Value("${ap.info.site}")
    private String siteName;

    @Value("${ap.info.env}")
    private String envType;

    @Value("${ap.info.sequence}")
    private String processSeq;

    @Setter
    private InterfaceSolaceSub interfaceSolaceSub;

    @Setter
    private InterfaceSolacePub interfaceSolacePub;

    private String clientName;

    @Getter
    private static ApPropertyObject instance;

    // Public method to get the Singleton instance
    public static ApPropertyObject createInstance(Environment env) {
        if (instance == null) {
            synchronized (ApPropertyObject.class) {
                // Double-check to ensure only one instance is created
                if (instance == null) {
                    instance = new ApPropertyObject(env);
                }
            }
        }

        if(instance.clientName == null){
            instance.clientName = ApCommonUtil.generateClientName(instance.groupName, instance.siteName, instance.envType, instance.processSeq);
        }


        return instance;
    }

    public ApPropertyObject(Environment env) {
        this.env = env;
        instance = this;
    }

}
