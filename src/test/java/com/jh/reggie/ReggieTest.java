package com.jh.reggie;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.jh.reggie.model.entity.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * @author JH
 * @description TODO
 * @date 2022-12-12 14:25:29
 */
@SpringBootTest
public class ReggieTest {

    @Test
    public void Test(){
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println(uuid);
    }

    public static void main(String[] args) {
        long id = IdWorker.getId(new Orders());
        System.out.println(id);
    }
}
