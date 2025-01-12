package com.tsh.slt.service.usgm;

import com.tsh.slt.dao.entity.usgm.jpa.SnUsgmRdsEntity;
import com.tsh.slt.dao.repository.usgm.jpa.SnUsgmRdsRepository;
import com.tsh.slt.dao.repository.usgm.jpa.SnUsgmRdsService;
import com.tsh.slt.service.usgm.util.UpstreamSyncUtil;
import com.tsh.slt.service.usgm.vo.UpStreamGitInfoVo;
import com.tsh.slt.spec.common.AbsMsgHead;
import com.tsh.slt.spec.common.MsgReasonVo;
import com.tsh.slt.spec.usgm.*;
import com.tsh.slt.spec.usgm.common.GitUnitRecordVo;
import com.tsh.slt.util.code.UseStatCd;
import com.tsh.slt.util.service.security.SecurityUtilService;
import com.tsh.slt.util.service.vo.SecurityInfoVo;
import com.tsh.slt.util.service.vo.SecurityValidateReqVo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로컬에서 관리하는 Git 을 별도의 리모트 원격지로 Push 하는 기능
 *
 */
@Slf4j
@Service
public class UsgmBizServiceImpl implements UsgmBizService{

    @Autowired
    SnUsgmRdsService snUsgmRdsService;

    @Autowired
    SecurityUtilService securityUtilService;

    @Autowired
    SnUsgmRdsRepository snUsgmRdsRepository;

    /**
     * 깃 정보를 저장하는 메모리
     */
    public static ConcurrentHashMap<String, UpStreamGitInfoVo> gitInfoVoMap;



    // TODO 데이터 초기화 로직. 추후 DB화
    static {

        log.info("Initialize data.");
        gitInfoVoMap = new ConcurrentHashMap<>();


        String[] localRepoNameList = {"wm", "lvs", "LogAppender"};
        String[] localRepoPathList = {"C:\\Workspace\\Workflow\\wm",
                "C:\\Workspace\\Workflow\\lvs",
                "C:\\Workspace\\Workflow\\LogAppender"};
        String[] remoteRepoUrlList = {"https://github.com/david-YJ-Kim/wm.git",
                "https://github.com/david-YJ-Kim/wfs.lvs",
                "https://github.com/david-YJ-Kim/AbsLogAppender"};
        String[] remoteRepoBranchList = {"main", "main", "main"};


        for(int i=0;i<localRepoNameList.length;i++) {
            String key = UpstreamSyncUtil.generateGitInfoKey(localRepoNameList[i], remoteRepoUrlList[i], remoteRepoBranchList[i]);
            gitInfoVoMap.put(key,
                    UpStreamGitInfoVo.builder()
                            .mapKey(key)
                            .localRepositoryFileName(localRepoNameList[i])
                            .localRepositoryPath(localRepoPathList[i])
                            .remoteRepositoryUrl(remoteRepoUrlList[i])
                            .remoteRepositoryBranchName(remoteRepoBranchList[i])
                            .build());

        }

        log.info("Complete initialize Git info Map. size of map: {}. keys: {}", gitInfoVoMap.size(), gitInfoVoMap.keySet());
        log.debug("Scan Git info map. Map: {}", gitInfoVoMap.toString());


    }



    @Override
    public OiUsgmFetchRepIvo srvUsgmFetchReq(SrvUsgmFetchReqIvo ivo) {

        ArrayList<OiUsgmFetchRepIvo> result;
        List<SnUsgmRdsEntity> fetchedEntities =  this.snUsgmRdsRepository.findByUseStatCd(UseStatCd.Usable);
        log.info("Fetch result: {}", fetchedEntities);

        ArrayList<GitUnitRecordVo> unitRecordVos = new ArrayList<>();
        for(SnUsgmRdsEntity entity: fetchedEntities){
            unitRecordVos.add(
                    GitUnitRecordVo.builder()
                            .objId(entity.getObjId())
                            .fileName(entity.getLclRpNm())
                            .localPath(entity.getLclRpPth())
                            .upstreamUrl(entity.getRmtRpUrl())
                            .branch(entity.getRmtRpBrnNm())
                            .build()
            );

        }

        return OiUsgmFetchRepIvo.builder()
                .head(
                        AbsMsgHead.builder()
                                .src("slt.agent")
                                .tgt("slt.oi")
                                .cid(OiUsgmFetchRepIvo.cid)
                                .build()
                )
                .body(
                        OiUsgmFetchRepIvo.Body.builder()
                                .userId("slt.agent")
                                .siteId("SLT")
                                .data(unitRecordVos)
                                .build()
                )
                .build();

    }

    /**
     * Git Info Vo 생성하여 저장
     * @param ivo
     * @return
     */
    public SnUsgmRdsEntity srvUsgmNewRecord(SrvUsgmNewRecordIvo ivo){

        SecurityInfoVo securityInfoVo = this.securityUtilService.generateSecurityInfo(ivo.getBody().getGitEmail(),
                ivo.getBody().getGitToken(),
                true);
        SnUsgmRdsEntity entity =  this.snUsgmRdsService.saveNewRecord(ivo, securityInfoVo, true);

        // TODO DB 접근 객체 바로 Return 하는 것은 보안 리스크. Return 객체에 대한 고민 필요
        return entity;
    }


    @Override
    public SrvUsgmEditRecordIvo srvUsgmEditRecord(SrvUsgmEditRecordIvo ivo) {

        SrvUsgmEditRecordIvo.Body body = ivo.getBody();

        if(body.getObjId() == null || body.getObjId().isEmpty()){
            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("0")
                            .reasonComment("조회 요청에 필수 파라미터가 부족합니다.")
                            .data(new HashMap<String, String>() {{ put("cnt", "0"); }})
                            .build()
            );
            return ivo;
        }

        Optional<SnUsgmRdsEntity> optionalEntity = this.snUsgmRdsRepository.findById(body.getObjId());

        if(optionalEntity.isPresent()){

            SnUsgmRdsEntity entity = optionalEntity.get();
            entity.setLclRpNm(body.getRepoFileName());
            entity.setLclRpPth(body.getRepoFilePath());
            entity.setRmtRpUrl(body.getGitRepoUrl());
            entity.setRmtRpBrnNm(body.getGitRepoBranchName());

            boolean validResult = this.securityUtilService.validateSecurityInfo(
                    SecurityValidateReqVo.builder()
                            .userPwd(body.getGitToken())
                            .userId(body.getUserId())
                            .salt(entity.getSalt())
                            .hashedPwd(entity.getPwdHsh())
                            .build(),
                    true
            );

            // 기존 Token과 다르다면, 업데이트
            if(!validResult){
                SecurityInfoVo securityInfoVo = this.securityUtilService.generateSecurityInfo(
                        entity.getEmail(),
                        ivo.getBody().getGitToken(),
                        true
                );
                entity.setSalt(securityInfoVo.getSalt());
                entity.setPwdHsh(securityInfoVo.getHashedPwd());

            }

            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("0")
                            .data(new HashMap<String, String>() {{ put("cnt", "1"); }})
                            .build()
            );

            try{
                this.snUsgmRdsRepository.save(entity);
                log.info("Update data.");

            }catch (Exception e){
                e.printStackTrace();
                ivo.getBody().setReason(
                        MsgReasonVo.builder()
                                .reasonCode("1")
                                .reasonComment(e.getMessage())
                                .data(new HashMap<String, String>() {{ put("cnt", "0"); }})
                                .build()
                );
            }

        }else{
            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("0")
                            .reasonComment("조회 결과가 없어 데이터 업데이트를 하지 않았습니다. ")
                            .data(new HashMap<String, String>() {{ put("cnt", "0"); }})
                            .build()
            );

        }



        return ivo;

    }

    @Override
    public SrvUsgmDeleteRecordIvo srvUsgmDeleteRecord(SrvUsgmDeleteRecordIvo ivo) {

        SrvUsgmDeleteRecordIvo.Body body = ivo.getBody();

        if(body.getObjId() == null || body.getObjId().isEmpty()){
            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("1")
                            .reasonComment("조회 요청에 필수 파라미터가 부족합니다.")
                            .data(new HashMap<String, String>() {{ put("cnt", "0"); }})
                            .build()
            );
            return ivo;
        }

        Optional<SnUsgmRdsEntity> optionalEntity = this.snUsgmRdsRepository.findById(body.getObjId());
        if(optionalEntity.isPresent()){
            SnUsgmRdsEntity entity = optionalEntity.get();
            this.snUsgmRdsRepository.delete(entity);
            log.info("Delete Entity.");

            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("0")
                            .build()
            );
        }else {

            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("1")
                            .reasonComment("대상 record 가 없습니다.")
                            .build()
            );
        }

        return ivo;


    }

    @Override
    public SrvUsgmGitPullIvo srvUsgmGitPull(SrvUsgmGitPullIvo ivo) {
        return null;
    }

    @Override
    public SrvUsgmGitPushIvo srvUsgmGitPush(SrvUsgmGitPushIvo ivo) {
        try {
            this.pushLocalStageToRemote(ivo);

            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("0")
                            .build()
            );
        } catch (GitAPIException e) {
            e.printStackTrace();
            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("1")
                            .reasonComment(e.getMessage())
                            .build()
            );
        } catch (IOException e) {
            e.printStackTrace();
            ivo.getBody().setReason(
                    MsgReasonVo.builder()
                            .reasonCode("1")
                            .reasonComment(e.getMessage())
                            .build()
            );
        }
        return ivo;
    }




    /**
     * 현재 등록된 전체 항목에 대해서 푸쉬 진행
     * @return
     */
//    public int pushAllStageToRemote(){
//
//        if(gitInfoVoMap == null || gitInfoVoMap.isEmpty()){
//            log.error("Nothing is registered in map.");
//            throw new NullPointerException("Nothing is registered and map has not been initialized.");
//        }
//
//        log.info("Push all registered local repository into remote one. Registered repository count: {}", gitInfoVoMap.keySet().stream().count());
//
//
//        int pushCount = 1;
//        for(String key: gitInfoVoMap.keySet()){
//
//            try{
//
//                UpStreamGitInfoVo vo = gitInfoVoMap.get(key);
//                this.pushCurrentStageToRemote(vo.getLocalRepositoryFileName(), vo.getRemoteRepositoryUrl(), vo.getRemoteRepositoryBranchName());
//
//            }catch (Exception e){
//                e.printStackTrace();
//                log.error("Error:{}", e);
//
//            }
//
//            pushCount ++;
//
//        }
//        log.info("Complete push all registered repository. completed push count: {}",pushCount);
//        return pushCount;
//
//    }

    /**
     * 저장된 특정 저장소 상위로 Push 하기
     * @param localRepoName
     * @param remoteRepoUrl
     * @param remoteRepoBranchName
     * @return
     */
//    public int pushCurrentStageToRemote(String localRepoName, String remoteRepoUrl, String remoteRepoBranchName) throws GitAPIException, IOException {
//
//
//        try{
//
//            int pushCnt = this.pushLocalStageToRemote(localRepoName, remoteRepoUrl, remoteRepoBranchName);
//            if(pushCnt < 1) {
//                throw new NullPointerException("Nothing has been pushed.");
//            }
//            return pushCnt;
//
//        }catch (Exception e){
//            e.printStackTrace();
//            log.error("Error: {}", e, e);
//            throw e;
//        }
//    }


    /**
     * 요청 받은 git을 기준으로 remote 로 push 하고 push 한 remote 개수를 리턴
     * @param ivo
     * @return
     */
    private int pushLocalStageToRemote(SrvUsgmGitPushIvo ivo)
            throws GitAPIException, IOException {

        SrvUsgmGitPushIvo.Body body = ivo.getBody();

        SnUsgmRdsEntity entity = this.snUsgmRdsRepository.findByLclRpNmAndLclRpPthAndRmtRpUrlAndRmtRpBrnNmAndUseStatCd(
                body.getRepoFileName(),
                body.getRepoFilePath(),
                body.getGitRepoUrl(),
                body.getGitRepoBranchName(),
                UseStatCd.Usable
        );

        if(entity == null){
            // TODO Scenario Exception
            log.error("Entity is not found.");
//            throw new Exception("Entity is not found.");
        }


        try {
            // 로컬 저장소 열기
            Git git = Git.open(new File(entity.getLclRpPth()));


            try{
                // 원격 저장소로 브랜치 푸시
                Iterable<PushResult> pushResults = git.push()
                        .setRemote(entity.getRmtRpUrl()) // 원격 저장소 URL
                        .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                                entity.getEmail(),
                                entity.getPwdHsh())) // 인증
                        .setRefSpecs(new RefSpec(entity.getRmtRpUrl() + ":" + entity.getRmtRpBrnNm())) // 브랜치 매핑
                        .call();

                int cnt = 0;
                for (PushResult result : pushResults) {
                    System.out.println("PushResult: " + result.getMessages());
                    cnt++;
                }
                return cnt;
            }catch (Exception e){
                throw new IllegalArgumentException(e.getMessage());
            }


        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error:{}", e);
            throw e;
        }

    }


    public static void main(String[] args) {
        // Git 리포지토리 경로
        String repoFileName = "slt.agent";
        String localRepoPath = "C:\\workspace\\slt\\slt.agent";
        // Git 원격 저장소 URL
        String remoteRepoUrl = "https://github.com/david-YJ-Kim/slt.agent.git";
        // 사용자 인증 정보
        String username = "david.yj.kim@gmail.com";
        String password = "a";

        // 푸시할 브랜치
        String branchName = "master";

        try {
            // 로컬 저장소 열기
            Git git = Git.open(new File(localRepoPath));

            // 파일 추가 및 커밋
            git.add().addFilepattern(".").call(); // 모든 변경된 파일 추가
            git.commit().setMessage("Committing changes - UpstreamManager Test").call();

            // 원격 저장소로 브랜치 푸시
            Iterable<PushResult> pushResults = git.push()
                    .setRemote(remoteRepoUrl) // 원격 저장소 URL
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)) // 인증
                    .setRefSpecs(new RefSpec(branchName + ":" + branchName)) // 브랜치 매핑
                    .call();

            for (PushResult result : pushResults) {
                System.out.println("PushResult: " + result.getMessages());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
