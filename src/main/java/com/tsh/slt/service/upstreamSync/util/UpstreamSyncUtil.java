package com.tsh.slt.service.upstreamSync.util;


import com.tsh.slt.service.upstreamSync.vo.UpStreamGitInfoVo;
import com.tsh.slt.util.ApCommonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpstreamSyncUtil {

    public static void main(String[] args) {

        System.out.println(
                UpstreamSyncUtil.generateGitInfoKey("main", "https://github.com/wm.git", "main")
        );
    }


    /**
     * Upstream Git 정보 관리 Map 의 Key 생성
     *
     * @param repoFileName
     * @param remoteRepoUrl
     * @param remoteBranchName
     * @return
     */
    public static String generateGitInfoKey(String repoFileName, String remoteRepoUrl, String remoteBranchName){


        return ApCommonUtil.generateKey("|", repoFileName, remoteRepoUrl.replaceAll("https://", ""), remoteBranchName);
    }
}
