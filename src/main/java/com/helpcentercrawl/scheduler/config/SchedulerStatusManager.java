package com.helpcentercrawl.scheduler.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.helpcentercrawl.scheduler.config
 * fileName       : SchedulerStatusManager
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@Slf4j
@Getter
@Component
public class SchedulerStatusManager {
    private boolean schedulingEnabled = true;

    /**
     * 스케줄링 상태 변경
     * @param enabled 활성화 여부
     */
    public boolean toggleSchedulingEnabled(boolean enabled) {
        this.schedulingEnabled = enabled;
        log.info("크롤링 스케줄링 상태가 {}으로 변경되었습니다.", enabled ? "활성화" : "비활성화");

        return schedulingEnabled;
    }

}