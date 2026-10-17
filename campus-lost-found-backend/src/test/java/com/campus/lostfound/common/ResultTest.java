package com.campus.lostfound.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("统一响应结果 Result 单元测试")
class ResultTest {

    @Test
    @DisplayName("成功响应测试")
    void testSuccessResult() {
        Result<String> result = Result.ok("操作成功", "testData");
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("testData", result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    @DisplayName("错误响应测试")
    void testErrorResult() {
        Result<?> result = Result.error(ResultCode.BAD_REQUEST, "参数错误");
        assertEquals(400, result.getCode());
        assertEquals("参数错误", result.getMessage());
        assertNull(result.getData());
    }
}
