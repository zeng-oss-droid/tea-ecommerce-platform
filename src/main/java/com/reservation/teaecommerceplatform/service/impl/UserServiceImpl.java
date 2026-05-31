package com.reservation.teaecommerceplatform.service.impl;

import com.reservation.teaecommerceplatform.dto.UserLoginDTO;
import com.reservation.teaecommerceplatform.dto.UserRegisterDTO;
import com.reservation.teaecommerceplatform.entity.User;
import com.reservation.teaecommerceplatform.mapper.UserMapper;
import com.reservation.teaecommerceplatform.service.UserService;
import com.reservation.teaecommerceplatform.util.JwtUtil;
import com.reservation.teaecommerceplatform.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link UserService} 实现类：所有用户相关持久化经 {@link UserMapper} 访问数据库。
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // ==================== 前台：账号与资料 ====================

    /** 用户注册 */
    @Override
    public User register(UserRegisterDTO registerDTO) {
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (registerDTO.getEmail() != null && userMapper.selectByEmail(registerDTO.getEmail()) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(Md5Util.encrypt(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setPhone(registerDTO.getPhone());
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        user.setStatus(1);
        user.setRole(0);
        
        userMapper.insert(user);
        return user;
    }

    /** 用户登录，返回JWT */
    @Override
    public String login(UserLoginDTO loginDTO) {
        User user = userMapper.selectByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        String encryptedPassword = Md5Util.encrypt(loginDTO.getPassword());
        if (!user.getPassword().equals(encryptedPassword)) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    /** 查询当前用户信息 */
    @Override
    public User getCurrentUser(Long userId) {
        return userMapper.selectById(userId);
    }

    /** 更新用户资料 */
    @Override
    public User updateUser(Long userId, User user) {
        user.setId(userId);
        userMapper.update(user);
        return userMapper.selectById(userId);
    }

    /** 修改密码 */
    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        // 验证原密码
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            throw new RuntimeException("请输入原密码");
        }
        
        // 验证新密码
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("请输入新密码");
        }
        
        if (newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }
        
        // 检查新密码是否与原密码相同
        if (oldPassword.equals(newPassword)) {
            throw new RuntimeException("新密码不能与原密码相同");
        }
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证原密码是否正确
        String encryptedOldPassword = Md5Util.encrypt(oldPassword);
        if (!user.getPassword().equals(encryptedOldPassword)) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        String encryptedNewPassword = Md5Util.encrypt(newPassword);
        userMapper.updatePassword(userId, encryptedNewPassword);
    }

    /**
     * 入驻申请业务规则：已是商家/待审核/管理员/禁用账号均不可重复提交；
     * 通过后调用 {@link UserMapper#updateMerchantInfo} 覆盖店铺信息并置为待审核。
     */
    @Override
    public void applyMerchant(Long userId, User user) {
        User currentUser = userMapper.selectById(userId);
        if (currentUser == null) {
            throw new RuntimeException("用户不存在");
        }
        if (currentUser.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用，无法申请商家");
        }
        if (currentUser.getRole() != null && currentUser.getRole() == 1) {
            throw new RuntimeException("管理员账号无需申请商家");
        }
        if (currentUser.getRole() != null && currentUser.getRole() == 2) {
            throw new RuntimeException("你已是商家账号");
        }
        if (currentUser.getMerchantApplyStatus() != null && currentUser.getMerchantApplyStatus() == 1) {
            throw new RuntimeException("你已提交申请，请等待管理员审核");
        }
        if (user.getMerchantName() == null || user.getMerchantName().trim().isEmpty()) {
            throw new RuntimeException("店铺名称不能为空");
        }
        if (user.getContactName() == null || user.getContactName().trim().isEmpty()) {
            throw new RuntimeException("联系人姓名不能为空");
        }
        if (user.getContactPhone() == null || user.getContactPhone().trim().isEmpty()) {
            throw new RuntimeException("联系电话不能为空");
        }
        if (user.getBusinessScope() == null || user.getBusinessScope().trim().isEmpty()) {
            throw new RuntimeException("主营类目不能为空");
        }

        user.setId(userId);
        userMapper.updateMerchantInfo(user);
    }

    // ==================== 管理后台：用户与商家申请 ====================

    /** 管理端分页查询用户列表 */
    @Override
    public Map<String, Object> getUserList(Integer pageNum, Integer pageSize, String keyword) {
        int offset = (pageNum - 1) * pageSize;
        List<User> list = userMapper.selectList(keyword, offset, pageSize);
        int total = userMapper.count(keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return result;
    }

    /** 根据ID查询用户 */
    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /** 启用/禁用用户 */
    @Override
    public void updateUserStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    /** 修改用户角色 */
    @Override
    public void updateUserRole(Long id, Integer role) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getRole() != null && user.getRole() == 1) {
            throw new RuntimeException("管理员角色不可修改");
        }
        if (role == null || (role != 0 && role != 2)) {
            throw new RuntimeException("仅支持设置为普通用户或商家");
        }
        userMapper.updateRole(id, role);
    }

    /** 分页查询「有过申请记录」的用户，条件与统计见 Mapper XML */
    @Override
    public Map<String, Object> getMerchantApplicationList(Integer pageNum, Integer pageSize, String keyword, Integer status) {
        int offset = (pageNum - 1) * pageSize;
        List<User> list = userMapper.selectMerchantApplicationList(keyword, status, offset, pageSize);
        int total = userMapper.countMerchantApplication(keyword, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        return result;
    }

    /** 乐观条件：仅当当前状态为待审核(1)时 UPDATE 成功，否则返回影响行数 0 并抛错 */
    @Override
    public void approveMerchantApplication(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getMerchantApplyStatus() == null || user.getMerchantApplyStatus() != 1) {
            throw new RuntimeException("该申请不在待审核状态");
        }
        int updated = userMapper.approveMerchantApplication(id);
        if (updated == 0) {
            throw new RuntimeException("审核状态已变更，请刷新后重试");
        }
    }

    /** 驳回后用户仍为普通用户，申请状态为已驳回(3)，并记录驳回原因 */
    @Override
    public void rejectMerchantApplication(Long id, String rejectReason) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getMerchantApplyStatus() == null || user.getMerchantApplyStatus() != 1) {
            throw new RuntimeException("该申请不在待审核状态");
        }
        String reason = (rejectReason == null || rejectReason.trim().isEmpty()) ? "资料不符合入驻要求" : rejectReason.trim();
        int updated = userMapper.rejectMerchantApplication(id, reason);
        if (updated == 0) {
            throw new RuntimeException("审核状态已变更，请刷新后重试");
        }
    }

    /**
     * 管理员删除「申请记录」：数据库层仅对状态 1、3 清空商户字段（见 clearMerchantApplication）；
     * 已通过(2)在 Service 层直接拒绝，避免误清已有商家身份及关联业务。
     */
    @Override
    public void deleteMerchantApplication(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getMerchantApplyStatus() != null && user.getMerchantApplyStatus() == 2) {
            throw new RuntimeException("已通过入驻的商家无法从此处删除，请在用户管理中调整角色");
        }
        int updated = userMapper.clearMerchantApplication(id);
        if (updated == 0) {
            throw new RuntimeException("仅可删除待审核或已驳回的申请，或数据已变更请刷新后重试");
        }
    }

    /** 删除用户 */
    @Override
    public void deleteUser(Long id) {
        userMapper.delete(id);
    }
}

