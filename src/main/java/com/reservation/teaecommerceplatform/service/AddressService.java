package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.entity.Address;

import java.util.List;

/** 用户地址簿；默认地址互斥在实现层用事务保证。 */
public interface AddressService {
    List<Address> getAddressList(Long userId);
    Address getById(Long id);
    void add(Address address);
    void update(Address address);
    void setDefault(Long userId, Long addressId);
    void delete(Long id);
}

