package com.reservation.teaecommerceplatform.service;

import com.reservation.teaecommerceplatform.dto.UserLoginDTO;
import com.reservation.teaecommerceplatform.dto.UserRegisterDTO;
import com.reservation.teaecommerceplatform.entity.User;

import java.util.Map;

/**
 * 用户领域服务接口。
 * <p>
 * 职责划分：前台（注册、登录、资料、商家入驻申请）与后台（用户列表、商家申请审核）
 * 均通过本接口；具体 SQL 在 {@link com.reservation.teaecommerceplatform.mapper.UserMapper} 中定义。
 * </p>
 */
public interface UserService {

    /** 用户注册：校验唯一性后写入 user 表，密码经 MD5 存储 */
    User register(UserRegisterDTO registerDTO);

    /** 登录：校验账号状态与密码，成功则签发 JWT */
    String login(UserLoginDTO loginDTO);

    /** 根据主键查询当前用户（个人中心等） */
    User getCurrentUser(Long userId);

    /** 更新用户资料（邮箱、手机、昵称等，不含密码与角色） */
    User updateUser(Long userId, User user);

    /** 修改密码：校验原密码后更新 */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 商家入驻申请：写入店铺与联系人等信息，并将 {@link User#getMerchantApplyStatus()} 置为待审核(1)。
     * 数据仍落在 user 表，无单独「申请表」。
     */
    void applyMerchant(Long userId, User user);

    // —— 以下仅供管理后台（需管理员 JWT）调用 ——

    /** 分页用户列表，返回 list、total、pageNum 等 */
    Map<String, Object> getUserList(Integer pageNum, Integer pageSize, String keyword);

    User getUserById(Long id);

    /** 启用/禁用账号 */
    void updateUserStatus(Long id, Integer status);

    /** 将用户角色改为普通用户(0)或商家(2)，不可改管理员(1) */
    void updateUserRole(Long id, Integer role);

    /**
     * 商家申请列表：仅包含曾提交过申请的用户（申请状态为 1/2/3），支持关键词与状态筛选。
     */
    Map<String, Object> getMerchantApplicationList(Integer pageNum, Integer pageSize, String keyword, Integer status);

    /** 审核通过：角色改为商家(2)，申请状态改为已通过(2) */
    void approveMerchantApplication(Long id);

    /** 审核驳回：角色保持普通用户(0)，申请状态改为已驳回(3)，可带驳回原因 */
    void rejectMerchantApplication(Long id, String rejectReason);

    /**
     * 删除申请记录：清空入驻字段并将申请状态置为未申请(0)；仅允许待审核/已驳回。
     * 已通过入驻的商家不可从此接口清除，需先在用户管理中处理角色。
     */
    void deleteMerchantApplication(Long id);

    /** 物理删除用户（非管理员），与「清空申请」不同 */
    void deleteUser(Long id);
}
