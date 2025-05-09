package com.helpcentercrawl.crawler.model;

import lombok.Builder;
import lombok.Getter;
import org.openqa.selenium.support.ui.ExpectedCondition;

/**
 * packageName    : com.helpcentercrawl.crawler.model
 * fileName       : LoginModel
 * author         : MinKyu Park
 * date           : 25. 5. 7.
 * description    : 크롤러 로그인 설정 정보를 담는 모델 클래스
 *                  각 사이트별 로그인 필드 ID, 선택자, 계정 정보 및 로그인 성공 조건 등을
 *                  관리하여 로그인 프로세스를 표준화하고 공통화하기 위한 데이터 모델
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 7.        MinKyu Park       최초 생성
 */
@Getter
@Builder
public class LoginModel {
    private String idFieldId;          // 아이디 입력 필드 ID
    private String pwFieldId;          // 비밀번호 입력 필드 ID
    private String loginButtonSelector; // 로그인 버튼 CSS 선택자
    private String username;           // 사용자명
    private String password;           // 비밀번호
    private boolean jsLogin;           // JS 로그인 여부
    private String jsLoginScript;      // JS 로그인 스크립트 (jsLogin이 true인 경우만 사용)
    private ExpectedCondition<?> successCondition; // 로그인 성공 조건
}