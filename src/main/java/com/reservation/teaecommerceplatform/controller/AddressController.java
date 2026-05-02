package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import com.reservation.teaecommerceplatform.entity.Address;
import com.reservation.teaecommerceplatform.service.AddressService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** 用户收货地址 CRUD 与默认地址切换。 */
@RestController
@RequestMapping("/api/address")
public class AddressController {
    
    @Autowired
    private AddressService addressService;

    @GetMapping("/list")
    public Result<List<Address>> getAddressList(HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            List<Address> list = addressService.getAddressList(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<Address> getById(@PathVariable Long id) {
        try {
            Address address = addressService.getById(id);
            return Result.success(address);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Address address, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            address.setUserId(userId);
            addressService.add(address);
            return Result.success("添加成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Address address) {
        try {
            addressService.update(address);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/setDefault")
    public Result<Void> setDefault(@RequestBody Map<String, Long> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            Long addressId = params.get("addressId");
            addressService.setDefault(userId, addressId);
            return Result.success("设置成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            addressService.delete(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

