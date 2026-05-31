package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Address;

import java.util.List;

/** 用户地址簿；默认地址互斥在实现层用事务保证。 */
public interface AddressService {
    /** 查询用户所有收货地址 */
    List<Address> getAddressList(Long userId);
    /** 根据ID查询地址 */
    Address getById(Long id);
    /** 新增地址，设置默认时取消其他默认 */
    void add(Address address);
    /** 更新地址信息 */
    void update(Address address);
    /** 设置默认地址 */
    void setDefault(Long userId, Long addressId);
    /** 删除地址 */
    void delete(Long id);
}

