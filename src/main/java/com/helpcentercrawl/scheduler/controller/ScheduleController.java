package com.helpcentercrawl.scheduler.controller;

import com.helpcentercrawl.scheduler.config.SchedulerStatusManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.helpcentercrawl.scheduler.controller
 * fileName       : ScheduleController
 * author         : MinKyu Park
 * date           : 25. 4. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 25. 4. 28.        MinKyu Park       최초 생성
 */
@RestController
@RequestMapping("/api/v1/schedule")
@Slf4j
public class ScheduleController {
    private final SchedulerStatusManager statusManager;

    @Autowired
    public ScheduleController(SchedulerStatusManager statusManager) {
        this.statusManager = statusManager;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getScheduleStatus() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("status", statusManager.isSchedulingEnabled());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/control")
    public ResponseEntity<Map<String, Boolean>> controlSchedule(@RequestBody Map<String, Boolean> request) {
        boolean enabled = request.get("status");
        statusManager.setSchedulingEnabled(enabled);

        Map<String, Boolean> response = new HashMap<>();
        response.put("status", statusManager.isSchedulingEnabled());
        return ResponseEntity.ok(response);
    }
}