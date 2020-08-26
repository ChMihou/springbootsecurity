package com.vicente.vicentedemo.service;

import com.vicente.vicentedemo.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 系统用户表 服务类
 * </p>
 */
public interface SysUserService extends IService<SysUser> {

    SysUser findUserByName(String userName);

}
