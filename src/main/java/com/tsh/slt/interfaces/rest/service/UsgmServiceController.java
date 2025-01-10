package com.tsh.slt.interfaces.rest.service;


import com.tsh.slt.dao.dto.usgm.SaveRecordDto;
import com.tsh.slt.dao.entity.usgm.jpa.SnUsgmRdsEntity;
import com.tsh.slt.dao.repository.usgm.jpa.SnUsgmRdsService;
import com.tsh.slt.interfaces.util.ApMessageList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/srv/usgm")
@RequiredArgsConstructor
@Slf4j
public class UsgmServiceController {

    @Autowired
    SnUsgmRdsService snUsgmRdsService;


    /**
     * Fetch all data from db.
     * @return
     */
    @GetMapping(ApMessageList.SRV_USGM_FETCH_ALL)
    public List<SnUsgmRdsEntity> fetchUsableData(){
        return this.snUsgmRdsService.findByUseStatCd();
    }

    @PostMapping(ApMessageList.SRV_USGM_NEW_RECORD)
    public ResponseEntity execute(@RequestBody SaveRecordDto dto){

        this.snUsgmRdsService.saveNewRecord(dto, true);

        return new ResponseEntity(HttpStatus.OK);
    }
}
