package com.tsh.slt.interfaces.rest;


import com.tsh.slt.interfaces.util.ApMessageList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/util")
@RequiredArgsConstructor
@Slf4j
public class ApCommonController {

    @GetMapping(ApMessageList.AP_HEALTH_TEST)
    public String executeWfsAlarmReport() throws Exception {

        return "AGENT Hello Word! ".concat(String.valueOf(System.currentTimeMillis()));

    }
}
