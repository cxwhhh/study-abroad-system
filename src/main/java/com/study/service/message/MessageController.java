package com.study.service.message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息控制器
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 获取用户发送的消息列表
     */
    @GetMapping("/sender/{userId}")
    public ResponseEntity<Map<String, Object>> getSentMessages(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Message> messages = messageService.getMessagesBySenderId(userId);
            response.put("code", 200);
            response.put("message", "获取发送消息列表成功");
            response.put("data", messages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取发送消息列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取用户消息列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserMessages(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Message> messages = messageService.getMessagesByReceiverId(userId);
            response.put("code", 200);
            response.put("message", "获取消息列表成功");
            response.put("data", messages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取消息列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取老师发送的消息（用于ApplicationProgress页面）
     */
    @GetMapping("/teacher")
    public ResponseEntity<Map<String, Object>> getTeacherMessages(
            @RequestParam Long userId,
            @RequestParam(required = false) Long applicationId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Message> messages;
            if (applicationId != null) {
                messages = messageService.getMessagesByReceiverIdAndApplicationId(userId, applicationId);
            } else {
                messages = messageService.getMessagesByReceiverId(userId);
            }
            
            response.put("code", 200);
            response.put("message", "获取老师消息成功");
            response.put("data", messages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取老师消息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据消息类型获取消息
     */
    @GetMapping("/type/{messageType}")
    public ResponseEntity<Map<String, Object>> getMessagesByType(
            @PathVariable String messageType,
            @RequestParam Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Message> messages = messageService.getMessagesByType(userId, messageType);
            response.put("code", 200);
            response.put("message", "获取消息成功");
            response.put("data", messages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取消息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取消息详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMessageById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Message message = messageService.getMessageById(id);
            if (message != null) {
                response.put("code", 200);
                response.put("message", "获取消息详情成功");
                response.put("data", message);
            } else {
                response.put("code", 404);
                response.put("message", "消息不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取消息详情失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送消息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Message message) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = messageService.sendMessage(message);
            if (success) {
                response.put("code", 200);
                response.put("message", "发送消息成功");
                response.put("data", message);
            } else {
                response.put("code", 400);
                response.put("message", "发送消息失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "发送消息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 发送老师消息
     */
    @PostMapping("/teacher")
    public ResponseEntity<Map<String, Object>> sendTeacherMessage(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long senderId = Long.valueOf(request.get("senderId").toString());
            String senderName = request.get("senderName").toString();
            Long receiverId = Long.valueOf(request.get("receiverId").toString());
            String title = request.get("title").toString();
            String content = request.get("content").toString();
            Long applicationId = request.get("applicationId") != null ? 
                Long.valueOf(request.get("applicationId").toString()) : null;

            boolean success = messageService.sendTeacherMessage(senderId, senderName, receiverId, 
                                                               title, content, applicationId);
            if (success) {
                response.put("code", 200);
                response.put("message", "发送老师消息成功");
            } else {
                response.put("code", 400);
                response.put("message", "发送老师消息失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "发送老师消息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = messageService.markMessageAsRead(id);
            if (success) {
                response.put("code", 200);
                response.put("message", "标记已读成功");
            } else {
                response.put("code", 400);
                response.put("message", "标记已读失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "标记已读失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量标记消息为已读
     */
    @PutMapping("/read-batch")
    public ResponseEntity<Map<String, Object>> markAsReadBatch(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = messageService.markMessagesAsRead(ids);
            if (success) {
                response.put("code", 200);
                response.put("message", "批量标记已读成功");
            } else {
                response.put("code", 400);
                response.put("message", "批量标记已读失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "批量标记已读失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = messageService.deleteMessage(id);
            if (success) {
                response.put("code", 200);
                response.put("message", "删除消息成功");
            } else {
                response.put("code", 400);
                response.put("message", "删除消息失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除消息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int count = messageService.getUnreadMessageCount(userId);
            response.put("code", 200);
            response.put("message", "获取未读消息数量成功");
            response.put("data", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取未读消息数量失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}