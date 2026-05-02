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

    @Override
    public List<Address> getAddressList(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    public Address getById(Long id) {
        return addressMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.cancelDefault(address.getUserId());
        }
        addressMapper.insert(address);
    }

    @Override
    @Transactional
    public void update(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (address.getIsDefault() == 1) {
            addressMapper.cancelDefault(address.getUserId());
        }
        addressMapper.update(address);
    }

    @Override
    @Transactional
    public void setDefault(Long userId, Long addressId) {
        addressMapper.cancelDefault(userId);
        addressMapper.updateDefault(userId, addressId);
    }

    @Override
    public void delete(Long id) {
        addressMapper.delete(id);
    }
}

