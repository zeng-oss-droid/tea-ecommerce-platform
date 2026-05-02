package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** 收货地址表。 */
@Mapper
public interface AddressMapper {
    Address selectById(Long id);
    List<Address> selectByUserId(Long userId);
    Address selectDefaultByUserId(Long userId);
    int insert(Address address);
    int update(Address address);
    int updateDefault(@Param("userId") Long userId, @Param("id") Long id);
    int cancelDefault(@Param("userId") Long userId);
    int delete(Long id);
}

