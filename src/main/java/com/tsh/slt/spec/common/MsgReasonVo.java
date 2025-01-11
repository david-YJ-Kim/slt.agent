package com.tsh.slt.spec.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MsgReasonVo {

    /* 요청에 대한 처리 결과, 0,1 등의 숫자 혹은 ErrorCode*/
    private String reasonCode;
    /* 결과에 대한 상세 내용을 기재 ex) 에러 내용 */
    private String reasonComment;
    /* 부가정보 */
    private Map<String, String> data;

    public static void main(String[] args) {
        HashMap<String, String> addData = new HashMap<>();
        addData.put("cnt", "1");

        MsgReasonVo.builder()
                .reasonCode("0")
                .reasonComment("해당 이벤트는 처리에 성공하였습니다.")
                .data(new HashMap<String, String>() {{ put("cnt", "1"); }})
                .build();
    }
}
