package com.tsh.slt.spec.usgm.common;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class GitUnitRecordVo {
    String objId;
    String fileName;
    String localPath;
    String upstreamUrl;
    String branch;
    String gitEmail;
    String gitToken;
}
