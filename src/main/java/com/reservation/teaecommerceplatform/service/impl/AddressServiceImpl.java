package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.entity.Address;
import com.reservation.teaecommerceplatform.mapper.AddressMapper;
import com.reservation.teaecommerceplatform.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** 地址默认互斥、归属用户校验。 */
@Service
public class AddressServiceImpl implements AddressService {
    
    @Autowired
    private AddressMapper addressMapper;

    /** 查询用户所有收货地址 */
    @Override
    public List<Address> getAddressList(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    /** 根据ID查询地址 */
    @Override
    public Address getById(Long id) {
        return addressMapper.selectById(id);
    }

    /** 新增地址，设置默认时取消其他默认 */
    @Override
    @Transactional
    public void add(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.cancelDefault(address.getUserId());
        }
        addressMapper.insert(address);
    }

    /** 更新地址信息 */
    @Override
    @Transactional
    public void update(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.cancelDefault(address.getUserId());
        }
        addressMapper.update(address);
    }

    /** 设置默认地址 */
    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        addressMapper.cancelDefault(userId);
        addressMapper.updateDefault(userId, addressId);
    }

    /** 删除地址 */
    @Override
    public void delete(Long id) {
        addressMapper.delete(id);
    }
}

