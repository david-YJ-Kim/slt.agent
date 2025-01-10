package com.tsh.slt.service.usgm;

import com.tsh.slt.service.usgm.util.UpstreamSyncUtil;
import com.tsh.slt.service.usgm.vo.UpStreamGitInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 로컬에서 관리하는 Git 을 별도의 리모트 원격지로 Push 하는 기능
 *
 */
@Slf4j
@Service
public class UpstreamManageService {

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


    /**
     * 전체 등록된 정보 출력
     * @return
     */
    public ConcurrentHashMap<String, UpStreamGitInfoVo> getAllRegisteredGitInfo(){
        log.info("Return all stored git info.");
        return gitInfoVoMap;
        
    }

    /**
     * Git Info Vo 생성하여 저장
     * TODO Datasource 정리 후 저장 로직 개발
     * @param localRepoName
     * @param localRepoPath
     * @param remoteRepoUrl
     * @param remoteRepoBranchName
     * @return
     */
    public UpStreamGitInfoVo registerGitInfo(String localRepoName, String localRepoPath, String remoteRepoUrl, String remoteRepoBranchName ){
        return null;
    }

    /**
     * 메모리 맵에 저장된 Git 정보 Vo 획득
     *
     * @param localRepoName
     * @param remoteRepoUrl
     * @param remoteRepoBranchName
     * @return
     */
    public UpStreamGitInfoVo getRegisteredGitInfo(String localRepoName, String remoteRepoUrl, String remoteRepoBranchName) {

        log.info("Retrieve git info. with parameters. localRepositoryName:{}, remoteRepositoryUrl:{}, remoteRepositoryBranchName:{}"
                , localRepoName, remoteRepoUrl, remoteRepoBranchName);

        String mapKey = UpstreamSyncUtil.generateGitInfoKey(localRepoName, remoteRepoUrl, remoteRepoBranchName);

        if(gitInfoVoMap.containsKey(mapKey)) {
            log.info("Complete to find data with key:{}", mapKey);
            return gitInfoVoMap.get(mapKey);

        }else {
            log.info("Fail to find data with key:{}. isContainKey:{}. keyList:{}", mapKey, gitInfoVoMap.containsKey(mapKey), gitInfoVoMap.keySet());
            throw new NullPointerException(String.format("Fail to find data with key:%s. isContainKey:%s. keyList:%s"
                    , mapKey, gitInfoVoMap.containsKey(mapKey), gitInfoVoMap.keySet().toString()));
        }
    }

    /**
     * 현재 등록된 전체 항목에 대해서 푸쉬 진행
     * @return
     */
    public int pushAllStageToRemote(){

        if(gitInfoVoMap == null || gitInfoVoMap.isEmpty()){
            log.error("Nothing is registered in map.");
            throw new NullPointerException("Nothing is registered and map has not been initialized.");
        }

        log.info("Push all registered local repository into remote one. Registered repository count: {}", gitInfoVoMap.keySet().stream().count());


        int pushCount = 1;
        for(String key: gitInfoVoMap.keySet()){

            try{

                UpStreamGitInfoVo vo = gitInfoVoMap.get(key);
                this.pushCurrentStageToRemote(vo.getLocalRepositoryFileName(), vo.getRemoteRepositoryUrl(), vo.getRemoteRepositoryBranchName());

            }catch (Exception e){
                e.printStackTrace();
                log.error("Error:{}", e);

            }

            pushCount ++;

        }
        log.info("Complete push all registered repository. completed push count: {}",pushCount);
        return pushCount;

    }

    /**
     * 저장된 특정 저장소 상위로 Push 하기
     * @param localRepoName
     * @param remoteRepoUrl
     * @param remoteRepoBranchName
     * @return
     */
    public int pushCurrentStageToRemote(String localRepoName, String remoteRepoUrl, String remoteRepoBranchName) throws GitAPIException, IOException {


        try{

            int pushCnt = this.pushLocalStageToRemote(localRepoName, remoteRepoUrl, remoteRepoBranchName);
            if(pushCnt < 1) {
                throw new NullPointerException("Nothing has been pushed.");
            }
            return pushCnt;

        }catch (Exception e){
            e.printStackTrace();
            log.error("Error: {}", e, e);
            throw e;
        }
    }


    /**
     * 요청 받은 git을 기준으로 remote 로 push 하고 push 한 remote 개수를 리턴
     * @param localRepoName
     * @param remoteRepoUrl
     * @param remoteRepoBranchName
     * @return
     */
    private int pushLocalStageToRemote(String localRepoName, String remoteRepoUrl, String remoteRepoBranchName) throws GitAPIException, IOException {

        String username = "david.yj.kim@gmail.com";
        String password = "personal_token";


        UpStreamGitInfoVo upStreamGitInfoVo = this.getRegisteredGitInfo(localRepoName, remoteRepoUrl, remoteRepoBranchName);

        String localRepoFilePath = upStreamGitInfoVo.getLocalRepositoryPath();

        try {
            // 로컬 저장소 열기
            Git git = Git.open(new File(localRepoFilePath));


            // 원격 저장소로 브랜치 푸시
            Iterable<PushResult> pushResults = git.push()
                    .setRemote(remoteRepoUrl) // 원격 저장소 URL
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password)) // 인증
                    .setRefSpecs(new RefSpec(remoteRepoBranchName + ":" + remoteRepoBranchName)) // 브랜치 매핑
                    .call();

            int cnt = 0;
            for (PushResult result : pushResults) {
                System.out.println("PushResult: " + result.getMessages());
                cnt++;
            }
            return cnt;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error:{}", e);
            throw e;
        }

    }


    public static void main(String[] args) {
        // Git 리포지토리 경로
        String repoFileName = "wm";
        String localRepoPath = "C:\\Workspace\\Workflow\\wm";
        // Git 원격 저장소 URL
        String remoteRepoUrl = "https://github.com/david-YJ-Kim/wm.git";
        // 사용자 인증 정보
        String username = "david.yj.kim@gmail.com";
        String password = "personal_token";

        // 푸시할 브랜치
        String branchName = "main";

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
