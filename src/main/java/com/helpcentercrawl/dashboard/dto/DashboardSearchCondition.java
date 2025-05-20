package com.helpcentercrawl.dashboard.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * packageName    : com.helpcentercrawl.dashboard.dto
 * fileName       : DashboardSearchCondition
 * author         : MinKyu Park
 * date           : 25. 5. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 5. 15.        MinKyu Park       최초 생성
 */
@Getter
@Setter
public class DashboardSearchCondition {
    private LocalDate startDate;
    private LocalDate endDate;


    @AssertTrue(message = "시작일은 종료일보다 이전이거나 같아야 합니다")
    public boolean isValidDateRange() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }

}
