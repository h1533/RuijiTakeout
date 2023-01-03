package com.jh.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jh.reggie.model.entity.Employee;

import javax.servlet.http.HttpServletRequest;

/**
 * @author JH
 * @description 员工Service
 * @date 2022-12-05 20:48:03
 */
public interface EmployeeService extends IService<Employee> {

    /**
     * 登录
     *
     * @param employee
     * @param request
     * @return
     */
    Employee login(Employee employee, HttpServletRequest request);
}
