package com.jh.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jh.reggie.commons.R;
import com.jh.reggie.model.entity.Employee;
import com.jh.reggie.service.EmployeeService;
import com.jh.reggie.utils.Md5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author JH
 * @description 员工Controller
 * @date 2022-12-05 20:54:58
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param employee 账号密码
     * @param request  添加Session
     * @return EmployeeInfo
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        Employee emp = employeeService.login(employee, request);
        return R.success(emp);
    }

    /**
     * 员工退出
     *
     * @param request 移除Session
     * @return True
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<String> save(@RequestBody Employee employee) {
        // 1、初始密码"123456"
        employee.setPassword(Md5Util.digest("123456"));
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> list(Integer page, Integer pageSize, String name) {
        // 条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        // 过滤条件
        wrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        // 添加排序条件
        wrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        Page<Employee> emps = employeeService.page(new Page<>(page, pageSize), wrapper);
        return R.success(emps);
    }

    /**
     * 修改账号状态
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
        // 执行更新操作
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 获取员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        Employee emp = employeeService.getById(id);
        if (emp != null) {
            return R.success(emp);
        }
        return R.error("没有查询到对应员工信息");
    }
}
