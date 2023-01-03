package com.jh.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jh.reggie.model.entity.AddressBook;
import com.jh.reggie.service.AddressBookService;
import com.jh.reggie.mappers.AddressBookMapper;
import org.springframework.stereotype.Service;

/**
* @author JH
* @description 针对表【address_book(地址管理)】的数据库操作Service实现
* @createDate 2022-12-21 14:32:08
*/
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook>
    implements AddressBookService{

}




