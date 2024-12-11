package com.tsh.slt.service.upstreamSync.vo;


import lombok.Builder;
import lombok.Data;

/**
 * Upstream 관리를 위한 Git 정보 객체
 * 해당 VO 별로 관리
 */
@Data
@Builder
public class UpStreamGitInfoVo {

    /* 해당 Vo 관리 key */
    String mapKey;

    /* 로컬에 등록된 Repository 파일 명
    *  FileName 을 Key 로 메모리에서 관리 할 계획
    * ex) wm */
    String localRepositoryFileName;

    /* 로컬에 등록된 Repository 파일 경로
    *  Repository 에 초기화된 Git 연결을 위해 사용
    *  ex) C:\Workspace\Workflow\wm */
    String localRepositoryPath;


    /* 원격 Repository 주소
    *  ex) https://github.com/david-YJ-Kim/wm.git */
    String remoteRepositoryUrl;

    /* 원격 Repository 브랜치 명
    *  ex) main
    *  ** 브랜치를 여러 개 등록 될 가능성 존재.
    *  ** 현재는 'main' 만 기본으로 사용 */
    String remoteRepositoryBranchName;


    /* 원격 Repository 사용자 이름
     * github 로그인 이메일
     * ex) david.yj.kim@gmail.com
     */
    String userName;


    /* 원격 Repository 접속 보안 키 (비밀번호)
     * github 로그인 비밀번호 혹은 계정에서 발행한 Access-Token
     */
    String credential;


}
