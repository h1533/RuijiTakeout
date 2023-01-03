package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.exception.CustomException;
import com.jh.reggie.commons.ErrorCode;
import com.jh.reggie.model.entity.Employee;
import com.jh.reggie.mappers.EmployeeMapper;
import com.jh.reggie.service.EmployeeService;
import com.jh.reggie.utils.Md5Util;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author JH
 * @description 员工Service
 * @date 2022-12-05 20:48:39
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
        implements EmployeeService {

    /**
     * 登录
     *
     * @param employee
     * @param request
     * @return
     */
    @Override
    public Employee login(Employee employee, HttpServletRequest request) {
        // 1.将页面提交的密码进行md5加密
        String password = employee.getPassword();
        String newPasswd = Md5Util.digest(password);
        // 2、根据页面提交的用户名username查询数据库
        Employee emp = this.getOne(new LambdaQueryWrapper<Employee>()
                .eq(Employee::getUsername, employee.getUsername()));
        // 3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            throw new CustomException(ErrorCode.LOGIN_ERROR.getMessage());
        }
        // 4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(newPasswd)) {
            throw new CustomException(ErrorCode.PASSWORD_ERROR.getMessage());
        }
        // 5、查看员工状态，如果为己禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            throw new CustomException(ErrorCode.ACCOUNT_ERROR.getMessage());
        }
        // 6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        request.getSession().setMaxInactiveInterval(86400);
        return emp;
    }
}
