package com.reservation.teaecommerceplatform.mapper;

import com.reservation.teaecommerceplatform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户表 {@code user}：登录注册、个人资料、商家入驻状态与店铺信息均在本表字段。
 * SQL 见 {@code mapper/UserMapper.xml}。
 */
@Mapper
public interface UserMapper {

    User selectById(Long id);
    User selectByUsername(String username);
    User selectByEmail(String email);
    int insert(User user);
    int update(User user);
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    // —— 后台：用户列表 ——
    java.util.List<User> selectList(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int count(@Param("keyword") String keyword);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    /** 不允许把管理员(role=1)改成其他角色 */
    int updateRole(@Param("id") Long id, @Param("role") Integer role);

    // —— 商家入驻：字段均在 user 表 ——
    /** 用户提交/修改入驻资料，状态置为待审核(1) */
    int updateMerchantInfo(User user);
    java.util.List<User> selectMerchantApplicationList(@Param("keyword") String keyword, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);
    int countMerchantApplication(@Param("keyword") String keyword, @Param("status") Integer status);
    int approveMerchantApplication(@Param("id") Long id);
    int rejectMerchantApplication(@Param("id") Long id, @Param("rejectReason") String rejectReason);
    /** 清空入驻信息，申请状态置 0；XML 中限定仅 status in (1,3) */
    int clearMerchantApplication(@Param("id") Long id);

    int delete(Long id);
}

