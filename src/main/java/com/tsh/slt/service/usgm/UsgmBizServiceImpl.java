package com.tsh.slt.service.usgm;

import com.tsh.slt.dao.entity.usgm.jpa.SnUsgmRdsEntity;
import com.tsh.slt.dao.repository.usgm.jpa.SnUsgmRdsRepository;
import com.tsh.slt.dao.repository.usgm.jpa.SnUsgmRdsService;
import com.tsh.slt.service.usgm.util.UpstreamSyncUtil;
import com.tsh.slt.service.usgm.vo.UpStreamGitInfoVo;
import com.tsh.slt.spec.common.AbsMsgHead;
import com.tsh.slt.spec.usgm.*;
import com.tsh.slt.spec.usgm.common.GitUnitRecordVo;
import com.tsh.slt.util.code.UseStatCd;
import com.tsh.slt.util.service.SecurityUtilService;
import com.tsh.slt.util.service.vo.SecurityInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
                                                                                        ivo.getBody().getGitToken());
        SnUsgmRdsEntity entity =  this.snUsgmRdsService.saveNewRecord(ivo, securityInfoVo, true);
        
        // TODO DB 접근 객체 바로 Return 하는 것은 보안 리스크. Return 객체에 대한 고민 필요
        return entity;
    }

    @Override
    public SnUsgmRdsEntity srvUsgmEditRecord(SrvUsgmEditRecordIvo ivo) {

        SrvUsgmEditRecordIvo.Body body = ivo.getBody();
        List<SnUsgmRdsEntity> fetchEntities = this.findEntityWithUl(
                body.getRepoFileName(), body.getRepoFilePath(),body.getGitRepoUrl(),body.getGitRepoBranchName()
        );

        return null;
    }

    @Override
    public int srvUsgmDeleteRecord(SrvUsgmDeleteRecordIvo ivo) {
        return 0;
    }

    @Override
    public int srvUsgmGitPull(SrvUsgmGitPullIvo ivo) {
        return 0;
    }

    @Override
    public int srvUsgmGitPush(SrvUsgmGitPushIvo ivo) {
        return 0;
    }


    /**
     * 테이블 UK로 조회
     * @param lclRpNm
     * @param lclRpPath
     * @param rmtRpUrl
     * @param rmtRpBrnNm
     * @return
     */
    private List<SnUsgmRdsEntity> findEntityWithUl(String lclRpNm, String lclRpPath, String rmtRpUrl, String rmtRpBrnNm){
        return this.snUsgmRdsRepository.findByLclRpNmAndLclRpPthAndRmtRpUrlAndRmtRpBrnNmAndUseStatCd(
                lclRpNm,
                lclRpPath,
                rmtRpUrl,
                rmtRpBrnNm,
                UseStatCd.Usable
        );
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
     * @param localRepoName
     * @param remoteRepoUrl
     * @param remoteRepoBranchName
     * @return
     */
//    private int pushLocalStageToRemote(String localRepoName, String remoteRepoUrl, String remoteRepoBranchName) throws GitAPIException, IOException {
//
//        String username = "david.yj.kim@gmail.com";
//        String password = "personal_token";

//
//        UpStreamGitInfoVo upStreamGitInfoVo = this.getRegisteredGitInfo(localRepoName, remoteRepoUrl, remoteRepoBranchName);
//
//        String localRepoFilePath = upStreamGitInfoVo.getLocalRepositoryPath();
//
//        try {
//            // 로컬 저장소 열기
//            Git git = Git.open(new File(localRepoFilePath));
//
//
//            // 원격 저장소로 브랜치 푸시
//            Iterable<PushResult> pushResults = git.push()
//                    .setRemote(remoteRepoUrl) // 원격 저장소 URL
//                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)) // 인증
//                    .setRefSpecs(new RefSpec(remoteRepoBranchName + ":" + remoteRepoBranchName)) // 브랜치 매핑
//                    .call();
//
//            int cnt = 0;
//            for (PushResult result : pushResults) {
//                System.out.println("PushResult: " + result.getMessages());
//                cnt++;
//            }
//            return cnt;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error("Error:{}", e);
//            throw e;
//        }
//
//    }


//    public static void main(String[] args) {
//        // Git 리포지토리 경로
//        String repoFileName = "wm";
//        String localRepoPath = "C:\\Workspace\\Workflow\\wm";
//        // Git 원격 저장소 URL
//        String remoteRepoUrl = "https://github.com/david-YJ-Kim/wm.git";
//        // 사용자 인증 정보
//        String username = "david.yj.kim@gmail.com";
//        String password = "personal_token";
//
//        // 푸시할 브랜치
//        String branchName = "main";
//
//        try {
//            // 로컬 저장소 열기
//            Git git = Git.open(new File(localRepoPath));
//
//            // 파일 추가 및 커밋
//            git.add().addFilepattern(".").call(); // 모든 변경된 파일 추가
//            git.commit().setMessage("Committing changes - UpstreamManager Test").call();
//
//            // 원격 저장소로 브랜치 푸시
//            Iterable<PushResult> pushResults = git.push()
//                    .setRemote(remoteRepoUrl) // 원격 저장소 URL
//                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)) // 인증
//                    .setRefSpecs(new RefSpec(branchName + ":" + branchName)) // 브랜치 매핑
//                    .call();
//
//            for (PushResult result : pushResults) {
//                System.out.println("PushResult: " + result.getMessages());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}